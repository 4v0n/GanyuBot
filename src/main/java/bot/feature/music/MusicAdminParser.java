package bot.feature.music;

import bot.command.CommandHandler;
import bot.command.music.admin.MatchSongCommand;
import bot.command.music.admin.ResetMatchCommand;

public class MusicAdminParser extends CommandHandler {

    protected MusicAdminParser(CommandHandler parent, String accessCommand) {
        super(parent, accessCommand);
    }

    @Override
    protected void buildCommands() {
        addCommand(new MatchSongCommand());
        addCommand(new ResetMatchCommand());
    }

    @Override
    protected void buildChildrenCommandHandlers() {

    }
}
