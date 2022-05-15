package ganyu.command.message;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

/**
 * This represents the action performed
 * when a command is invoked
 *
 * @author Aron Navodh Kumarawatta
 * @version 15.05.2022
 */
public interface Action {
    /**
     * What the command will do
     * @param event The event that triggered the command
     * @param args Any arguments that may be used by the command
     */
    void run(MessageReceivedEvent event, List<String> args);
}
