package bot.listener;

import bot.command.button.ButtonCommandCenter;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class ButtonInteractionListener extends ListenerAdapter {
    private static ButtonInteractionListener INSTANCE;

    //messageid || parser
    private final HashMap<String, ButtonCommandCenter> controllers;

    private ButtonInteractionListener() {
        controllers = new HashMap<>();
    }

    public static ButtonInteractionListener getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ButtonInteractionListener();
        }

        return INSTANCE;
    }

    public void addCommandCenter(ButtonCommandCenter commandCenter) {
        controllers.put(commandCenter.getController().getId(), commandCenter);
    }

    public void removeCommandCenter(ButtonCommandCenter commandCenter) {
        controllers.remove(commandCenter.getController().getId());
    }

    /**
     * run when a button is pressed
     *
     * @param event
     */
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (Objects.requireNonNull(event.getUser()).isBot()) {
            return;
        }

        event.deferReply().queue();
        if (controllers.containsKey(event.getMessageId())) {
            controllers.get(event.getMessageId()).parse(event);
        } else {
            event.getHook().setEphemeral(true).sendMessage("This button is no longer active!").queue();
        }
    }
}
