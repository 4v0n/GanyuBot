package ganyu.command.reaction;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;

/**
 * This allows for reaction commands to be stored
 *
 * @author Aron Navodh Kumarawatta
 * @version 15.05.2022
 */
public abstract class ReactionCommandHandler {
    private final ReactionCommandCenter commandCenter;

    public ReactionCommandHandler(Message controller) {
        commandCenter = new ReactionCommandCenter(controller);
        buildCommands();
        buildSynonyms();
    }

    public abstract void buildCommands();
    public abstract void buildSynonyms();

    public void parse(GenericMessageReactionEvent event) {
        commandCenter.parse(event);
    }

    public ReactionCommandCenter getCommandCenter() {
        return commandCenter;
    }
}
