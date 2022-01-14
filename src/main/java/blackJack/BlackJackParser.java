package blackJack;

import Base.Bot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BlackJackParser{

    public void parse(MessageReceivedEvent event, Bot bot) {
        System.out.println("event passed");
        Game game = (Game) bot.getActivities().get(event.getAuthor().getId());


    }
}
