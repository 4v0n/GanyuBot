package Base;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Activity {
    private final MessageChannel channel;
    private final String userID;


    public Activity(MessageReceivedEvent event){
        this.channel = event.getChannel();
        this.userID = event.getAuthor().getId();
        //System.out.println(channel);
        //System.out.println(userID);
    }

    public String getUserID() {
        return userID;
    }

    public MessageChannel getChannel(){
        return channel;
    }
}
