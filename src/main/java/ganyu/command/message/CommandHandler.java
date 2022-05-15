package ganyu.command.message;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * This abstract class allows for a set of commands to be stored
 * along with their synonyms
 *
 * @author Aron Navodh Kumarawatta
 * @version 15.05.2022
 */
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
