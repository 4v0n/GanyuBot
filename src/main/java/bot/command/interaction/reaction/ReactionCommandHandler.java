package bot.command.interaction.reaction;

import bot.listener.ReactionListener;
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
        ReactionListener reactionListener = ReactionListener.getINSTANCE();
        reactionListener.addCommandCenter(commandCenter);
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
        ReactionListener reactionListener = ReactionListener.getINSTANCE();
        reactionListener.removeCommandCenter(commandCenter);
        getController().clearReactions().queue();
    }
}
