package blackJack;

import Base.Bot;
import Base.CommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;

public class BlackJackParser extends CommandHandler {


    public BlackJackParser(Bot bot) {
        super(bot);
        this.addCommand("hit");
        this.addCommand("stand");
    }

    public void parse(MessageReceivedEvent event, Bot bot) {
        Game game = (Game) bot.getActivities().get(event.getAuthor().getId() + event.getChannel().getId());

        String content = event.getMessage().getContentRaw();
        ArrayList<String> args = splitString(content);
        String commandWord = null;
        args.remove(0); // remove the prefix so it is not dealt with
        if (args.size() > 0){
            commandWord = args.get(0);
        }

        if (game.isActive()) {
            switch (commandWord) {
                case "hit":
                    game.getPlayer().addCard(game.getDeck().dealCard());
                    game.update();
                    break;
                case "stand":
                    game.finish();
                    break;
            }
        } else {
            game.finish();
        }
    }


}
