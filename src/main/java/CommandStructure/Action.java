package CommandStructure;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public interface Action {
    void run(MessageReceivedEvent event, List<String> args);
}
