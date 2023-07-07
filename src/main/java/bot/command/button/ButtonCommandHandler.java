package bot.command.button;

import bot.listener.ButtonListener;
import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.TimeUnit;

public abstract class ButtonCommandHandler {
    private final ButtonCommandCenter commandCenter;

    public ButtonCommandHandler(Message controller) {
        commandCenter = new ButtonCommandCenter(controller);
    }

    public abstract void buildCommands();

    public ButtonCommandCenter getCommandCenter() {
        return commandCenter;
    }

    public Message getController() {
        return commandCenter.getController();
    }

    public void activate(int activeTimeinMins) {
        ButtonListener buttonListener = ButtonListener.getINSTANCE();
        buttonListener.addCommandCenter(commandCenter);
        buildCommands();

        Thread waitThread = new Thread(() -> {
            try {
                Thread.sleep(TimeUnit.MINUTES.toMillis(activeTimeinMins));
                deactivate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        waitThread.start();
    }

    public void deactivate() {
        ButtonListener buttonListener = ButtonListener.getINSTANCE();
        buttonListener.removeCommandCenter(commandCenter);
        getController().getButtons().clear();
    }
}
