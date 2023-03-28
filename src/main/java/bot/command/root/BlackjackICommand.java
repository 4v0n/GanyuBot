package bot.command.root;

import bot.command.ICommand;
import bot.feature.root.BaseCommandHandler;
import bot.feature.blackjack.BlackJackHandler;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlackjackICommand implements ICommand {

    private final CommandDataImpl commandData;

    public BlackjackICommand() {
        this.commandData = new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public void run(Event event, List<String> args) {
        BlackJackHandler handler = (BlackJackHandler) BaseCommandHandler.getINSTANCE().getChildren().get(getCommandWord());
        handler.parse(event);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "blackjack";
    }

    @Override
    public @NotNull String getDescription() {
        return "Blackjack mini-game commands. use `[prefix] blackjack help` for more info";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"bj"};
    }
}
