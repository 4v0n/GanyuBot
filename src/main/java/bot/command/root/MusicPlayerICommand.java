package bot.command.root;

import bot.command.ICommand;
import bot.feature.root.BaseCommandHandler;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MusicPlayerICommand implements ICommand {

    private final CommandDataImpl commandData;

    public MusicPlayerICommand() {
        this.commandData = new CommandDataImpl(getCommandWord(), "Music bot commands");
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
    public @NotNull CommandDataImpl getCommandData() {
        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"mp"};
    }
}
