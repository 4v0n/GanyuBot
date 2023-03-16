package bot.command.root;

import bot.command.ICommand;
import bot.feature.root.BaseCommandHandler;
import bot.feature.booru.ImageHandler;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ImagesICommand implements ICommand {

    private final CommandData commandData;

    public ImagesICommand() {
        this.commandData = new CommandData(getCommandWord(), getDescription());
    }

    @Override
    public void run(Event event, List<String> args) {
        BaseCommandHandler baseCommandHandler = BaseCommandHandler.getINSTANCE();
        ImageHandler handler = (ImageHandler) baseCommandHandler.getChildren().get(getCommandWord());

        handler.parse(event);
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
    public @NotNull CommandData getCommandData() {
        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
