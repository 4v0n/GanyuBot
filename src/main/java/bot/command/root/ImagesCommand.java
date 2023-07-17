package bot.command.root;

import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.root.BaseCommandHandler;
import bot.feature.booru.ImageHandler;
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
        BaseCommandHandler baseCommandHandler = BaseCommandHandler.getInstance();
        ImageHandler handler = (ImageHandler) baseCommandHandler.getChildren().get(getCommandWord());

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
