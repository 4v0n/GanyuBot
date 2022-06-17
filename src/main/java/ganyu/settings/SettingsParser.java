package ganyu.settings;

import ganyu.command.message.CommandHandler;
import ganyu.settings.commands.ChangeDJRoleCommand;
import ganyu.settings.commands.ChangePrefixCommand;
import ganyu.settings.commands.ResetDataCommand;
import ganyu.settings.commands.ShowCurrentSettingsCommand;

/**
 * @author Aron Kumarawatta
 * @version 09.06.2022
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
