package bot.feature.music;

import bot.command.CommandBranch;
import bot.command.music.admin.MatchSongCommand;
import bot.command.music.admin.ResetMatchCommand;

public class MusicAdminParser extends CommandBranch {

    protected MusicAdminParser(CommandBranch parent, String accessCommand) {
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
