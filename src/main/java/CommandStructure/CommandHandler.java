package CommandStructure;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class CommandHandler {
    private final CommandCenter commandCenter;


    public CommandHandler(int Layer) {
        commandCenter = new CommandCenter(Layer);
        buildCommands();
        buildSynonyms();
    }

    public abstract void buildCommands();
    public abstract void buildSynonyms();

    public void parse(MessageReceivedEvent event){
        commandCenter.parse(event);
    }

    public CommandCenter getCommandCenter(){
        return commandCenter;
    }
}
