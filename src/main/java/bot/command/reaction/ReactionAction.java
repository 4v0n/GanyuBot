package bot.command.reaction;

import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;

/**
 * This is the action performed when a reaction command
 * is invoked
 *
 * @author Aron Navodh Kumarawatta
 * @version 15.05.2022
 */
public interface ReactionAction {
    /**
     * What the command does
     *
     * @param event The event that triggered the action
     */
    void run(GenericMessageReactionEvent event);
}
