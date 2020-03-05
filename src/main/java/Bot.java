import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.MessageContext;
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

    private final static String ERROR_MESSAGE = "\u274C An error occurred";

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
        String message = "Hi! To configure me, follow steps below:\n\n" +
                "1. Add my email address `sendtokindlebot@gmail.com` to your [approved email list]" +
                "(https://www.amazon.com/gp/help/customer/display.html?nodeId=201974240)\n" +
                "2. Set your Kindle email address using /email command\n\n" +
                "Supported formats: _doc_, _docx_, _pdf_, _txt_, _jpg_, _jpeg_, _png_, _bmp_," +
                " _azw_, _mobi_, _rtf_, _prc_, _psz_";
        return Ability
                .builder()
                .name("start")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> silent.sendMd(message, ctx.chatId()))
                .build();
    }

    public Ability emailCommand() {
        String message = "Reply to this message with email address to set or change it";
        return Ability.builder()
                .name("email")
                .privacy(PUBLIC)
                .locality(ALL)
                .input(0)
                .action(ctx -> checkEmail(message, ctx))
                .reply(this::setEmail, Flag.MESSAGE, Flag.REPLY,
                        isReplyToBot(), isReplyToMessage(message))
                .build();
    }

    private void checkEmail(String message, MessageContext ctx) {
        long chatId = ctx.chatId();
        userEmailService.getEmail(chatId).ifPresent(email ->
                silent.sendMd(String.format("\uD83D\uDCE7 Current email address " +
                        "of your Kindle is `%s`", email), chatId));
        silent.forceReply(message, chatId);
    }

    private void setEmail(Update upd) {
        long chatId = upd.getMessage().getChatId();
        String reply = userEmailService.setEmail(chatId, upd.getMessage().getText()) ?
                "\u2705 Email was successfully changed" : ERROR_MESSAGE;
        silent.send(reply, chatId);
    }

    public Reply replyToDocument() {
        return Reply.of(this::processDocument, Flag.DOCUMENT);
    }

    private void processDocument(Update update) {
        long chatId = update.getMessage().getChatId();
        String message = userEmailService.getEmail(chatId).map(email -> {
            silent.send("\u2699 Processing file...", chatId);
            Document document = update.getMessage().getDocument();
            String fileName = document.getFileName();
            return fileService.convertTelegramDocumentToInputStream(getTelegramFilePath(document))
                    .map(fileStream -> {
                        silent.send("\u27A1 Sending file to your Kindle...", chatId);
                        boolean isSent = mailService.sendFile(email, fileName, document.getMimeType(),
                                fileStream);
                        return isSent ? String.format("\u2705 *%s* was sent to your Kindle. " +
                                "It will be delivered in a couple of minutes", fileName) : ERROR_MESSAGE;
                    }).orElse(ERROR_MESSAGE);

        }).orElse("Set your email address using /email before sending files");
        silent.sendMd(message, chatId);
    }

    private String getTelegramFilePath(Document document) {
        GetFile getFileMethod = new GetFile();
        getFileMethod.setFileId(document.getFileId());
        try {
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFileMethod);
            return file.getFilePath();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
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
}
