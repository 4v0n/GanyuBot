package ganyu.base.commands;

import ganyu.base.BaseCommandHandler;
import ganyu.command.message.Command;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MusicPlayerCommand implements Command {

    private final CommandData commandData;

    public MusicPlayerCommand() {
        this.commandData = new CommandData(getCommandWord(), "Music bot commands");
    }

    @Override
    public void run(Event event, List<String> args) {
        BaseCommandHandler.getINSTANCE().getChildren().get(getCommandWord()).parse(event);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "musicplayer";
    }

    @Override
    public @NotNull String getDescription() {
        return "Music bot commands. use `[prefix] mp help` for more info. These commands can be directly accessed by using `[prefix]m ...`";
    }

    @Override
    public @NotNull CommandData getCommandData() {
        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"mp"};
    }
}
