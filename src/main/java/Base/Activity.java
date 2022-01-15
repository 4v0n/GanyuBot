package Base;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;

public class Activity {
    private final MessageChannel channel;
    private final String userID;
    private final CommandHandler parser;
    private final Message message;
    private ArrayList<String> commandList;

    public Activity(MessageReceivedEvent event, CommandHandler parser, Message message){
        this.channel = event.getChannel();
        this.userID = event.getAuthor().getId();
        this.message = message;
        //System.out.println(channel);
        //System.out.println(userID);
        this.parser = parser;
    }

    public Message getMessage() {
        return message;
    }

    public CommandHandler getParser() {
        return parser;
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
