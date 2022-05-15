package ganyu.base;

import ganyu.command.reaction.ReactionCommandCenter;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

/**
 * This class allows the bot to listen to message reactions
 * and deal with them as commands
 *
 * @author Aron Navodh Kumarawatta
 * @version 15.05.2022
 */
public class ReactionCommandParser extends ListenerAdapter {
    //messageid || parser
    private final HashMap<String, ReactionCommandCenter> controllers;

    public ReactionCommandParser() {
        controllers = new HashMap<>();
    }

    public void addCommandCenter(ReactionCommandCenter commandCenter){
        controllers.put(commandCenter.getController().getId(), commandCenter);
    }

    public void removeCommandCenter(ReactionCommandCenter commandCenter){
        controllers.remove(commandCenter.getController().getId());
    }

    /**
     * run when reaction is given
     * @param event
     */
    @Override
    public void onGenericMessageReaction(@NotNull GenericMessageReactionEvent event) {
        if (Objects.requireNonNull(event.getUser()).isBot()){return;}

        if (controllers.containsKey(event.getMessageId())) {
            controllers.get(event.getMessageId()).parse(event);
        }
    }
}
