package bot.command.button;

import bot.command.CommandContext;
import bot.command.CommandExistsException;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.HashMap;
import java.util.List;

public class ButtonCommandCenter {
    private final HashMap<Button, ButtonAction> commandList;
    private final Message controller;

    public ButtonCommandCenter(Message controller) {
        commandList = new HashMap<>();
        this.controller = controller;
    }

    public void addCommand(Button button, ButtonAction action) {
        if (commandList.containsKey(button)) {
            throw new CommandExistsException(button.getLabel());
        }

        commandList.put(button, action);
    }

    public void parse(ButtonInteractionEvent event) {
        ButtonAction buttonAction = commandList.get(event.getButton());
        if (buttonAction != null) {
            buttonAction.run(new CommandContext(event));
        }
    }

    public Message getController() {
        return controller;
    }
}
