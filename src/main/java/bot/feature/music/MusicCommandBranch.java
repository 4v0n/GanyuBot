package bot.feature.music;

import bot.command.music.*;
import bot.command.CommandBranch;
import bot.command.music.AdminCommand;

/**
 * This handles music bot commands
 *
 * @author Aron Navodh Kumarawatta
 * @version 28.03.2023
 */
public class MusicCommandBranch extends CommandBranch {

    public MusicCommandBranch(CommandBranch parent) {
        super(parent);
    }

    public MusicCommandBranch(CommandBranch parent, String accessCommand) {
        super(parent, accessCommand);
    }

    /**
     * Builds all commands.
     */
    @Override
    public void buildCommands() {
        // voice channel commands
        addCommand(new JoinCommand());
        addCommand(new StopCommand());

        // song commands
        addCommand(new PlaySongCommand());
        addCommand(new PlayListCommand());

        // queue information commands
        addCommand(new NowPlayingCommand());
        addCommand(new ShowQueueCommand());

        // queue manipulation commands
        addCommand(new EmptyQueueCommand());
        addCommand(new RemoveSongCommand());


        addCommand(new SkipToCommand());
        addCommand(new MoveSongCommand());
        addCommand(new RemoveDuplicatesCommand());

        // player commands
        addCommand(new PauseSongCommand());
        addCommand(new SkipSongCommand());
        addCommand(new ShuffleQueueCommand());
        addCommand(new LoopQueueCommand());
        addCommand(new LoopSongCommand());
        addCommand(new SeekThroughCommand());

        addCommand(new ListCommand());
        addCommand(new AdminCommand());

        addCommand(new LinkSpotifyCommand());
        addCommand(new CheckSpotifyLinkCommand());
        addCommand(new UnlinkSpotifyCommand());

        addHelpMessage("Note that these commands can be directly accessed using `[prefix]m [command]`");
    }

    @Override
    protected void buildChildrenCommandHandlers() {
        addHandler(new PlaylistParser(this, "list"));
        addHandler(new MusicAdminParser(this, "admin"));
    }
}