package bot.feature.settings;

import bot.command.CommandHandler;
import bot.command.settings.ChangeDJRoleCommand;
import bot.command.settings.ChangePrefixCommand;
import bot.command.settings.ResetDataCommand;
import bot.command.settings.ShowCurrentSettingsCommand;

/**
 * @author Aron Kumarawatta
 * @version 28.03.2023
 */
public class SettingsParser extends CommandHandler {

    public SettingsParser(CommandHandler parent) {
        super(parent);
    }

    public SettingsParser(CommandHandler parent, String accessCommand) {
        super(parent, accessCommand);
    }

    @Override
    public void buildCommands() {
        addCommand(new ChangePrefixCommand());

        addCommand(new ChangeDJRoleCommand());

        addCommand(new ShowCurrentSettingsCommand());

        addCommand(new ResetDataCommand());
    }

    @Override
    protected void buildChildrenCommandHandlers() {

    }
}
