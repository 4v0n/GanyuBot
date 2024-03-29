package bot.command;

import bot.Bot;
import bot.db.legacy.server.ServerData;
import bot.feature.root.BaseCommandBranch;
import dev.morphia.Datastore;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This abstract class allows for a set of commands to be stored
 * along with their synonyms
 *
 * @author Aron Navodh Kumarawatta
 * @version 09.06.2022
 */
public abstract class CommandBranch {
    private final CommandParser commandParser;
    private final CommandBranch parent;
    private final int layer;

    private final HashMap<String, CommandBranch> childrenCommandHandlers;
    private final String accessCommand;

    protected CommandBranch(CommandBranch parent) {
        this.parent = parent;
        this.childrenCommandHandlers = new HashMap<>();
        this.accessCommand = " ";

        if (parent == null) {
            layer = 1;
        } else {
            layer = parent.getNewLayer();
        }

        commandParser = new CommandParser(layer);
        buildCommands();
        buildSynonyms();
        buildChildrenCommandHandlers();
        mapCommands();
    }

    protected CommandBranch(CommandBranch parent, String accessCommand) {
        this.parent = parent;
        this.childrenCommandHandlers = new HashMap<>();
        this.accessCommand = accessCommand;

        if (parent == null) {
            layer = 1;
        } else {
            layer = parent.getNewLayer();
        }

        commandParser = new CommandParser(layer);
        buildCommands();
        buildSynonyms();
        buildChildrenCommandHandlers();
        mapCommands();
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
        for (Command command : commandParser.getCommands().values()) {
            for (String synonym : command.getSynonyms()) {
                addSynonym(synonym, command.getCommandWord());
            }
        }

        if (commandParser.getSynonyms().isEmpty()) {
            commandParser.getCommands().remove("synonyms");
        }
    }

    /**
     * Build children commandhandlers here using addHandler()
     */
    protected abstract void buildChildrenCommandHandlers();

    private void mapCommands() {
        for (Command command : commandParser.getCommands().values()) {
            if (command.getCommandWord().equals("help") || command.getCommandWord().equals("synonyms")) {
                continue;
            }

            CommandBranch handler = childrenCommandHandlers.get(command.getCommandWord());

            if (handler != null) {
                // the command has children
                CommandDataImpl commandData = command.getCommandData();

                for (Command subCommand : handler.getCommandCenter().getCommands().values()) {

                    if (subCommand.getCommandWord().equals("help") || subCommand.getCommandWord().equals("synonyms")) {
                        continue;
                    }

                    commandData.addSubcommands(convertToSubCommand(subCommand.getCommandData()));
                }
            }
        }
    }

    public void parse(Event event) {
        if (event instanceof MessageReceivedEvent) {
            commandParser.parse((MessageReceivedEvent) event);
            return;
        }

        if (event instanceof SlashCommandInteractionEvent) {
            commandParser.parse((SlashCommandInteractionEvent) event);
        }
    }


    protected void addCommand(Command command) {
        commandParser.addCommand(command);
    }

    protected void addSynonym(String synonym, String original) {
        commandParser.addSynonym(synonym.toLowerCase(), original.toLowerCase());
    }

    protected void addHandler(CommandBranch commandBranch) {
        childrenCommandHandlers.put(commandBranch.accessCommand, commandBranch);
    }

    protected void addHelpMessage(String text) {
        commandParser.addHelpMessage(text);
    }

    public CommandParser getCommandCenter() {
        return commandParser;
    }

    public HashMap<String, CommandBranch> getChildren() {
        return childrenCommandHandlers;
    }

    public void upsertCommands(Guild guild) {
        ServerData serverData = Bot.getInstance().getGuildData(guild);

        if (serverData.getCommandSetVersion() != BaseCommandBranch.getInstance().hashCode()) {
            serverData.setCommandSetVersion(BaseCommandBranch.getInstance().hashCode());

            try {
                Datastore datastore = Bot.getInstance().getDatastore();
                datastore.save(serverData);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            for (Command command : commandParser.getCommands().values()) {
                if (command.getCommandWord().equals("help") || command.getCommandWord().equals("synonyms")) {
                    continue;
                }

                guild.upsertCommand(command.getCommandData()).queue();
            }

            guild.updateCommands().queue();
        }
    }

    public void forceUpsertCommands(Guild guild) {
        ServerData serverData = Bot.getInstance().getGuildData(guild);

        if (serverData == null) {
            serverData = new ServerData(guild);
            Bot.getInstance().addGuildData(serverData);
        }

        serverData.setCommandSetVersion(BaseCommandBranch.getInstance().hashCode());

        try {
            Datastore datastore = Bot.getInstance().getDatastore();
            datastore.save(serverData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (Command command : commandParser.getCommands().values()) {
            if (command.getCommandWord().equals("help") || command.getCommandWord().equals("synonyms")) {
                continue;
            }

            guild.upsertCommand(command.getCommandData()).queue();
        }

        guild.updateCommands().queue();
    }

    private SubcommandData convertToSubCommand(CommandDataImpl child) {
        SubcommandData subcommandData = new SubcommandData(child.getName(), child.getDescription());
        subcommandData.addOptions(child.getOptions());

        return subcommandData;
    }

    @Override
    public int hashCode() {
        ArrayList<String> commandStrings = new ArrayList<>();

        for (Command command : commandParser.getCommands().values()) {
            commandStrings.add(command.getCommandWord());
        }

        for (CommandBranch handler : childrenCommandHandlers.values()) {
            for (Command command : handler.getCommandCenter().getCommands().values()) {
                commandStrings.add(command.getCommandWord());
            }
        }

        return commandStrings.toString().hashCode();
    }
}
