package bot.command.button;

import bot.listener.ButtonInteractionListener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public abstract class ButtonCommandHandler {
    private final ButtonCommandCenter commandCenter;

    public ButtonCommandHandler() {
        commandCenter = new ButtonCommandCenter();
    }

    public abstract void buildCommands();

    public ButtonCommandCenter getCommandCenter() {
        return commandCenter;
    }

    public Message getController() {
        return commandCenter.getController();
    }

    public void activate(int activeTimeInMins) {
        ButtonInteractionListener listener = ButtonInteractionListener.getINSTANCE();
        listener.addCommandCenter(commandCenter);

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
        ButtonInteractionListener listener = ButtonInteractionListener.getINSTANCE();
        listener.removeCommandCenter(commandCenter);

        Guild guild = getController().getGuild();
        guild.getTextChannelById(getController().getChannel().getId()).retrieveMessageById(getController().getId()).queue(msg -> {
            ArrayList<Button> disabledButtons = new ArrayList<>();
            for (ItemComponent oldButton : getButtons()) {
                disabledButtons.add(((Button) oldButton).asDisabled());
            }
            try {
                msg.editMessage(msg.getContentRaw()).setActionRow(disabledButtons).queue();
            } catch (IllegalArgumentException e) {
                msg.editMessageEmbeds(msg.getEmbeds()).setActionRow(disabledButtons).queue();
            }
        });
    }

    public Collection<? extends ItemComponent> getButtons() {
        return commandCenter.getButtons();
    }

    public void setController(Message message) {
        commandCenter.setController(message);
    }
}
