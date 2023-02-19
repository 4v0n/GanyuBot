package bot.feature.reaction;

import bot.command.CommandExistsException;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;

import java.util.HashMap;

/**
 * This class allows for reaction commands to be handled
 *
 * @author Aron Navodh Kumarawatta
 * @version 15.05.2022
 */
public class ReactionCommandCenter {

    private final HashMap<String, IReactionAction> commandList;
    private final Message controller;

    public ReactionCommandCenter(Message controller) {
        commandList = new HashMap<>();
        this.controller = controller;
    }

    public void addCommand(String unicode, IReactionAction action) {
        if (commandList.containsKey(unicode)) {
            throw new CommandExistsException(unicode);
        }

        commandList.put(unicode, action);
        controller.addReaction(unicode).queue();
    }

    public void parse(GenericMessageReactionEvent event) {

        for (MessageReaction reaction : controller.getReactions()) {
            if (!commandList.containsKey(reaction.getReactionEmote().getAsReactionCode())) {
                controller.removeReaction(reaction.getReactionEmote().getEmote(), event.getUser()).queue();
            }
        }

        IReactionAction reactionAction = commandList.get(event.getReactionEmote().getAsReactionCode());

        if (reactionAction != null) {
            reactionAction.run(event);
        }
    }

    public Message getController() {
        return controller;
    }
}
