package Base;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Activity {
    private final MessageChannel channel;
    private final String userID;
    private final CommandHandler parser;


    public Activity(MessageReceivedEvent event, CommandHandler parser){
        this.channel = event.getChannel();
        this.userID = event.getAuthor().getId();
        //System.out.println(channel);
        //System.out.println(userID);
        this.parser = parser;
    }

    public void parse(MessageReceivedEvent event, Bot bot){
        parser.parse(event, bot);
    }

    public String getUserID() {
        return userID;
    }

    public MessageChannel getChannel(){
        return channel;
    }
}
