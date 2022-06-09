package ganyu.command.message;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * This abstract class allows for a set of commands to be stored
 * along with their synonyms
 *
 * @author Aron Navodh Kumarawatta
 * @version 09.06.2022
 */
public abstract class CommandHandler {
    private final CommandCenter commandCenter;


    protected CommandHandler(int Layer) {
        commandCenter = new CommandCenter(Layer);
        buildCommands();
        buildSynonyms();
    }

    protected abstract void buildCommands();

    protected abstract void buildSynonyms();

    public void parse(MessageReceivedEvent event) {
        commandCenter.parse(event);
    }

    protected void addCommand(String commandName, String description, Action action){
        commandCenter.addCommand(commandName.toLowerCase(), description, action);
    }

    protected void addSynonym(String synonym, String original){
        commandCenter.addSynonym(synonym.toLowerCase(), original.toLowerCase());
    }

    protected void addHelpMessage(String text){
        commandCenter.addHelpMessage(text);
    }

    public CommandCenter getCommandCenter() {
        return commandCenter;
    }
}
