package bot.command.root;

import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.root.BaseCommandBranch;
import bot.feature.blackjack.BlackJackCommandBranch;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlackjackCommand implements Command {

    private final CommandDataImpl commandData;

    public BlackjackCommand() {
        this.commandData = new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public void run(CommandContext context, List<String> args) {
        BlackJackCommandBranch handler = (BlackJackCommandBranch) BaseCommandBranch.getInstance().getChildren().get(getCommandWord());
        handler.parse(context.getEvent());
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
