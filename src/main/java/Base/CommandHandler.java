package Base;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandHandler {

    private final Bot bot;

    public CommandHandler(Bot bot){
        this.bot = bot;
    }
    public void parse(MessageReceivedEvent event, Bot bot){
        //template class
    }
}
