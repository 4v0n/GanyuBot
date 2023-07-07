package bot.feature.music;

import bot.command.CommandHandler;
import bot.command.music.playlist.SaveToPlaylistCommand;

public class PlaylistParser extends CommandHandler {

    public PlaylistParser(CommandHandler parent) {
        super(parent);
    }

    public PlaylistParser(CommandHandler parent, String accessCommand) {
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
