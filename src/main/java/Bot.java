import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.toggle.BareboneToggle;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.Predicate;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.FileService;
import service.UserEmailService;
import service.impl.FileServiceImpl;
import service.MailService;
import service.impl.MailServiceImpl;
import service.impl.UserEmailServiceImpl;

public class Bot extends AbilityBot {

    private final static String BOT_TOKEN;
    private final static String BOT_NAME;
    private final static int CREATOR_ID;

    private final FileService fileService;
    private final MailService mailService;
    private final UserEmailService userEmailService;

    static {
        BOT_TOKEN = System.getenv("BOT_TOKEN");
        BOT_NAME = System.getenv("BOT_NAME");
        CREATOR_ID = Integer.parseInt(System.getenv("CREATOR_ID"));
    }

    public Bot() {
        super(BOT_TOKEN, BOT_NAME, new BareboneToggle());
        fileService = new FileServiceImpl();
        mailService = new MailServiceImpl();
        userEmailService = new UserEmailServiceImpl();
    }

    @Override
    public int creatorId() {
        return CREATOR_ID;
    }

    public Ability startCommand() {
        String message = "Hi!";
        return Ability
                .builder()
                .name("start")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> silent.send(ctx.chatId().toString(), ctx.chatId()))
                .build();
    }

    public Ability emailCommand() {
        String message = "Reply to this message with email address to set or change it";
        return Ability.builder()
                .name("email")
                .privacy(PUBLIC)
                .locality(ALL)
                .input(0)
                .action(ctx -> {
                    if (userEmailService.userEmailExists(ctx.chatId())) {
                        silent.sendMd(userEmailService.getEmailInfo(ctx.chatId()), ctx.chatId());
                    }
                    silent.forceReply(message, ctx.chatId());
                })
                .reply(upd -> silent.send(userEmailService.setEmail(upd.getMessage().getChatId(),
                        upd.getMessage().getText()), upd.getMessage().getChatId()),
                        Flag.MESSAGE, Flag.REPLY, isReplyToBot(), isReplyToMessage(message))
                .build();
    }

    private Predicate<Update> isReplyToMessage(String message) {
        return upd -> {
            Message reply = upd.getMessage().getReplyToMessage();
            return reply.hasText() && reply.getText().equalsIgnoreCase(message);
        };
    }

    private Predicate<Update> isReplyToBot() {
        return upd -> upd.getMessage().getReplyToMessage().getFrom()
                .getUserName().equalsIgnoreCase(getBotUsername());
    }

    public Reply getDocument()  {
        return Reply.of(
                update -> {
                    if (!userEmailService.userEmailExists(update.getMessage().getChatId())) {
                        silent.send("Set your email address using /email before sending files",
                                update.getMessage().getChatId());
                    } else {
                        String email = userEmailService.getEmail(update.getMessage().getChatId());
                        Document document = update.getMessage().getDocument();
                        String message = mailService.sendFile(email, document.getFileName(), document.getMimeType(),
                                fileService.convertTelegramDocumentToInputStream(getFilePath(document)));
                        silent.send(message, update.getMessage().getChatId());
                    }
                },
                Flag.DOCUMENT);
    }

    private String getFilePath(Document document) {
        final GetFile getFileMethod = new GetFile();
        getFileMethod.setFileId(document.getFileId());
        try {
            final org.telegram.telegrambots.meta.api.objects.File file = execute(getFileMethod);
            return file.getFilePath();
        } catch (final TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}
