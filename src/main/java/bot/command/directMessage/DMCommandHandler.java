package bot.command.directMessage;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a new branch of DM commands.
 */
public abstract class DMCommandHandler {
    private final DMCommandCenter commandCenter;
    private final DMCommandHandler parent;
    private final int layer;
    private final HashMap<String, DMCommandHandler> childrenCommandHandlers;
    private final String accessCommand;

    protected DMCommandHandler(DMCommandHandler parent) {
        this.parent = parent;
        this.childrenCommandHandlers = new HashMap<>();
        this.accessCommand = " ";

        if (parent == null) {
            layer = 1;
        } else {
            layer = parent.getNewLayer();
        }

        commandCenter = new DMCommandCenter(layer);
        buildCommands();
        buildSynonyms();
        buildChildrenCommandHandlers();
    }

    protected DMCommandHandler(DMCommandHandler parent, String accessCommand) {
        this.parent = parent;
        this.childrenCommandHandlers = new HashMap<>();
        this.accessCommand = accessCommand;

        if (parent == null) {
            layer = 1;
        } else {
            layer = parent.getNewLayer();
        }

        commandCenter = new DMCommandCenter(layer);
        buildCommands();
        buildSynonyms();
        buildChildrenCommandHandlers();
    }

    private int getNewLayer() {
        return layer + 1;
    }

    /**
     * Build all commands here using
     * addCommand()
     */
    protected abstract void buildCommands();

    private void buildSynonyms() {
        for (DMCommand command : commandCenter.getCommands().values()) {
            for (String synonym : command.getSynonyms()) {
                addSynonym(synonym, command.getCommandWord());
            }
        }

        if (commandCenter.getSynonyms().isEmpty()) {
            commandCenter.getCommands().remove("synonyms");
        }
    }

    /**
     * Build children commandhandlers here using addHandler()
     */
    protected abstract void buildChildrenCommandHandlers();

    public void parse(MessageReceivedEvent event) {
        commandCenter.parse(event);
    }


    protected void addCommand(DMCommand command) {
        commandCenter.addCommand(command);
    }

    protected void addSynonym(String synonym, String original) {
        commandCenter.addSynonym(synonym.toLowerCase(), original.toLowerCase());
    }

    protected void addHandler(DMCommandHandler commandHandler) {
        childrenCommandHandlers.put(commandHandler.accessCommand, commandHandler);
    }

    protected void addHelpMessage(String text) {
        commandCenter.addHelpMessage(text);
    }

    public DMCommandCenter getCommandCenter() {
        return commandCenter;
    }

    public HashMap<String, DMCommandHandler> getChildren() {
        return childrenCommandHandlers;
    }

    @Override
    public int hashCode() {
        ArrayList<String> commandStrings = new ArrayList<>();

        for (DMCommand command : commandCenter.getCommands().values()) {
            commandStrings.add(command.getCommandWord());
        }

        for (DMCommandHandler handler : childrenCommandHandlers.values()) {
            for (DMCommand command : handler.getCommandCenter().getCommands().values()) {
                commandStrings.add(command.getCommandWord());
            }
        }

        return commandStrings.toString().hashCode();
    }
}
