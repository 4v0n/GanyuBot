package bot.command.root;

import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.root.BaseCommandHandler;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MusicPlayerCommand implements Command {

    private final CommandDataImpl commandData;

    public MusicPlayerCommand() {
        this.commandData = new CommandDataImpl(getCommandWord(), "Music bot commands");
    }

    @Override
    public void run(CommandContext context, List<String> args) {
        BaseCommandHandler.getInstance().getChildren().get(getCommandWord()).parse(context.getEvent());
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
    public @NotNull CommandDataImpl getCommandData() {
        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"mp"};
    }
}
