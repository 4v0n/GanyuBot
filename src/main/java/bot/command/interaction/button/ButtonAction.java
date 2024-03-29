package bot.command.interaction.button;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * Action performed when a Button is pressed by a user.
 */
public interface ButtonAction {
    /**
     * What the command does
     *
     * @param event The event that triggered the action
     */
    void run(ButtonInteractionEvent event);
}
