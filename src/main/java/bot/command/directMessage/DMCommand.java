package bot.command.directMessage;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A command that is accessed in a private channel (Direct Message channel)
 */
public interface DMCommand {
    void run(MessageReceivedEvent event, List<String> args);

    @NotNull
    String getCommandWord();

    @NotNull
    String getDescription();

    String[] getSynonyms();
}
