package bot.command;

import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Command {
    void run(CommandContext context, List<String> args);

    @NotNull
    String getCommandWord();

    @NotNull
    String getDescription();
    @NotNull
    CommandDataImpl getCommandData();

    String[] getSynonyms();
}
