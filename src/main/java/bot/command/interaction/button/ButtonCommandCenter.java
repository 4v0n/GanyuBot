package bot.command.interaction.button;

import bot.command.CommandExistsException;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.HashMap;

public class ButtonCommandCenter {

    private final HashMap<Button, ButtonAction> commandList;
    private final ArrayList<Button> buttonsInOrder;
    private Message controller;

    public ButtonCommandCenter() {
        commandList = new HashMap<>();
        this.buttonsInOrder = new ArrayList<>();
    }

    public void addCommand(Button button, ButtonAction action) {
        if (commandList.containsKey(button)) {
            throw new CommandExistsException(button.getId());
        }

        commandList.put(button, action);
        buttonsInOrder.add(button);
    }

    public void parse(ButtonInteractionEvent event) {
        ButtonAction buttonAction = commandList.get(event.getButton());

        if (buttonAction != null) {
            buttonAction.run(event);
        }
    }

    public void setController(Message controller) {
        this.controller = controller;
    }

    public Message getController() {
        return controller;
    }

    public ArrayList<Button> getButtons() {
        return buttonsInOrder;
    }
}
