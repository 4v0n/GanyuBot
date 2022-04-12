package CommandStructure;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface Action {
    void run(MessageReceivedEvent event);
}
