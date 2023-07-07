package bot.command.button;

import bot.command.CommandContext;

public interface ButtonAction {
    /**
     * What the command does
     *
     * @param context The context of the event that triggered the action
     */
    void run(CommandContext context);
}
