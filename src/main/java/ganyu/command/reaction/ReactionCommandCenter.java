package ganyu.command.reaction;

import ganyu.base.Bot;
import ganyu.base.Main;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;

import java.util.HashMap;

/**
 * This class allows for reaction commands to be handled
 *
 * @author Aron Navodh Kumarawatta
 * @version 15.05.2022
 */
public class ReactionCommandCenter {

    private final HashMap<String, ReactionAction> commandList;
    private final Bot bot;
    private final Message controller;

    public ReactionCommandCenter(Message controller) {
        commandList = new HashMap<>();
        this.bot = Main.getBotData();
        this.controller = controller;
    }

    public void addCommand(String unicode, ReactionAction action){
        commandList.put(unicode, action);
        controller.addReaction(unicode).queue();
    }

    public boolean containsCommand(String unicode) {
        return commandList.containsKey(unicode);
    }

    public void parse(GenericMessageReactionEvent event){
        ReactionAction reactionAction = commandList.get(event.getReactionEmote().getAsReactionCode());
        System.out.println(event.getReactionEmote().getAsReactionCode());

        if (reactionAction != null){
            reactionAction.run(event);
        }
    }

    public Message getController() {
        return controller;
    }
}
