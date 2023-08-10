package bot.command.root;

import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.root.BaseCommandBranch;
import bot.feature.booru.ImageCommandBranch;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ImagesCommand implements Command {

    private final CommandDataImpl commandData;

    public ImagesCommand() {
        this.commandData = new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public void run(CommandContext context, List<String> args) {
        BaseCommandBranch baseCommandHandler = BaseCommandBranch.getInstance();
        ImageCommandBranch handler = (ImageCommandBranch) baseCommandHandler.getChildren().get(getCommandWord());

        handler.parse(context.getEvent());
    }

    @Override
    public @NotNull String getCommandWord() {
        return "images";
    }

    @Override
    public @NotNull String getDescription() {
        return "Image commands. Use `[prefix] images help` for more info";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
