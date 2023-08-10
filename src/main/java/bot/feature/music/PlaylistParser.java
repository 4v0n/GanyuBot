package bot.feature.music;

import bot.command.CommandBranch;
import bot.command.music.playlist.SaveToPlaylistCommand;

public class PlaylistParser extends CommandBranch {

    public PlaylistParser(CommandBranch parent, String accessCommand) {
        super(parent, accessCommand);
    }

    @Override
    protected void buildCommands() {
        addCommand(new SaveToPlaylistCommand());
    }

    @Override
    protected void buildChildrenCommandHandlers() {

    }
}
