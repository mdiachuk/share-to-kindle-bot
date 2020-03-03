import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.toggle.BareboneToggle;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

public class Bot extends AbilityBot {

    private final static String BOT_TOKEN;
    private final static String BOT_NAME;
    private final static int CREATOR_ID;


    static {
        BOT_TOKEN = System.getenv("BOT_TOKEN");
        BOT_NAME = System.getenv("BOT_NAME");
        CREATOR_ID = Integer.parseInt(System.getenv("CREATOR_ID"));
    }

    public Bot() {
        super(BOT_TOKEN, BOT_NAME, new BareboneToggle());
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
                .action(ctx -> silent.send(message, ctx.chatId()))
                .build();
    }
}
