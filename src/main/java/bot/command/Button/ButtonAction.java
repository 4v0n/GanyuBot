package bot.command.Button;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface ButtonAction {
    /**
     * What the command does
     *
     * @param event The event that triggered the action
     */
    void run(ButtonInteractionEvent event);
}
