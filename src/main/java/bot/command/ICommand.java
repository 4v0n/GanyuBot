package bot.command;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ICommand {
    void run(Event event, List<String> args);

    @NotNull
    String getCommandWord();

    @NotNull
    String getDescription();
    @NotNull
    CommandDataImpl getCommandData();

    String[] getSynonyms();
}
