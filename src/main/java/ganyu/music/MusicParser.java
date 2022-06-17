package ganyu.music;

import ganyu.command.message.CommandHandler;
import ganyu.music.commands.*;

/**
 * This handles music bot commands
 *
 * @author Aron Navodh Kumarawatta
 * @version 09.06.2022
 */
public class MusicParser extends CommandHandler {

    public MusicParser(CommandHandler parent) {
        super(parent);
    }

    public MusicParser(CommandHandler parent, String accessCommand) {
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



        addHelpMessage("Note that these commands can be directly accessed using `[prefix]m [command]`");
    }

    @Override
    protected void buildChildrenCommandHandlers() {

    }
}