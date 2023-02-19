package bot.feature.reaction;

import bot.listener.Reaction;
import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.TimeUnit;

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
    }

    public abstract void buildCommands();

    public ReactionCommandCenter getCommandCenter() {
        return commandCenter;
    }

    public Message getController() {
        return commandCenter.getController();
    }

    public void activate(int activeTimeInMins) {
        Reaction reactionParser = Reaction.getINSTANCE();
        reactionParser.addCommandCenter(commandCenter);
        buildCommands();

        Thread waitThread = new Thread(() -> {
            try {
                Thread.sleep(TimeUnit.MINUTES.toMillis(activeTimeInMins));
                deactivate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        waitThread.start();
    }

    public void deactivate() {
        Reaction reactionParser = Reaction.getINSTANCE();
        reactionParser.removeCommandCenter(commandCenter);
        getController().clearReactions().queue();
    }
}
