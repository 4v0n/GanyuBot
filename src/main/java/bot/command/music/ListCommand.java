package bot.command.music;

import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.root.BaseCommandHandler;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListCommand implements Command {
    private final CommandDataImpl commandData;

    public ListCommand() {
        this.commandData = new CommandDataImpl(getCommandWord(), "Playlist commands");
    }

    @Override
    public void run(CommandContext context, List<String> args) {
        BaseCommandHandler.getInstance().getChildren().get("musicplayer").getChildren().get(getCommandWord()).parse(context.getEvent());
    }

    @Override
    public @NotNull String getCommandWord() {
        return "list";
    }

    @Override
    public @NotNull String getDescription() {
        return "Music bot commands. use `[prefix] mp l help` for more info. These commands can be directly accessed by using `[prefix]m l ...`";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"l"};
    }
}
