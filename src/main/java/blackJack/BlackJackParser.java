package blackJack;

import Base.Bot;
import Base.CommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BlackJackParser extends CommandHandler {

    public BlackJackParser(Bot bot) {
        super(bot);
    }

    public void parse(MessageReceivedEvent event, Bot bot) {
        System.out.println("event passed");
        Game game = (Game) bot.getActivities().get(event.getAuthor().getId() + event.getChannel().getId());

        game.edit("game text reception pass");


    }
}
