package bot.util.message.vote;

import bot.feature.reaction.ReactionCommandHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

/**
 * @author Aron Kumarawatta
 * @version 29.05.2022
 */
public class VoteMessage extends ReactionCommandHandler {

    private final IVoteAction action;
    private int votes;
    private int threshold;

    public VoteMessage(Message controller, int threshold, IVoteAction action) {
        super(controller);
        this.votes = 0;
        this.threshold = threshold;
        this.action = action;
    }

    @Override
    public void buildCommands() {
        getCommandCenter().addCommand("⏭", this::updateVotes);
    }

    private void updateVotes(GenericMessageReactionEvent event) {

        if (event instanceof MessageReactionRemoveEvent){
            this.votes--;
        } else {
            this.votes++;
        }

        System.out.println(votes);

        if (this.votes >= this.threshold){
            action.action();
            deactivate();
        }
    }
}
