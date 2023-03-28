package bot.feature.settings;

import bot.command.CommandHandler;
import bot.command.settings.ChangeDJRoleICommand;
import bot.command.settings.ChangePrefixICommand;
import bot.command.settings.ResetDataICommand;
import bot.command.settings.ShowCurrentSettingsICommand;

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
        addCommand(new ChangePrefixICommand());

        addCommand(new ChangeDJRoleICommand());

        addCommand(new ShowCurrentSettingsICommand());

        addCommand(new ResetDataICommand());
    }

    @Override
    protected void buildChildrenCommandHandlers() {

    }
}
