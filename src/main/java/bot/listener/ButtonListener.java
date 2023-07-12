package bot.listener;

import bot.command.interaction.button.ButtonCommandCenter;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class ButtonListener extends ListenerAdapter {
    private static ButtonListener INSTANCE;

    private final HashMap<String, ButtonCommandCenter> controllers;

    private ButtonListener() {
        controllers = new HashMap<>();
    }

    public static ButtonListener getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ButtonListener();
        }
        return INSTANCE;
    }

    public void addCommandCenter(ButtonCommandCenter commandCenter) {
        controllers.put(commandCenter.getController().getId(), commandCenter);
    }

    public void removeCommandCenter(ButtonCommandCenter commandCenter) {
        controllers.remove(commandCenter.getController().getId());
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (Objects.requireNonNull(event.getUser()).isBot()) {
            return;
        }

        if (controllers.containsKey(event.getMessageId())) {
            controllers.get(event.getMessageId()).parse(event);
        }
    }
}
