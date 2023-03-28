package bot.feature.music;

import bot.command.music.*;
import bot.command.CommandHandler;

/**
 * This handles music bot commands
 *
 * @author Aron Navodh Kumarawatta
 * @version 28.03.2023
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
        addCommand(new JoinICommand());
        addCommand(new StopICommand());

        // song commands
        addCommand(new PlaySongICommand());
        addCommand(new PlayListICommand());

        // queue information commands
        addCommand(new NowPlayingICommand());
        addCommand(new ShowQueueICommand());

        // queue manipulation commands
        addCommand(new EmptyQueueICommand());
        addCommand(new RemoveSongICommand());


        addCommand(new SkipToICommand());
        addCommand(new MoveSongICommand());
        addCommand(new RemoveDuplicatesICommand());

        // player commands
        addCommand(new PauseSongICommand());
        addCommand(new SkipSongICommand());
        addCommand(new ShuffleQueueICommand());
        addCommand(new LoopQueueICommand());
        addCommand(new LoopSongICommand());
        addCommand(new SeekThroughICommand());



        addHelpMessage("Note that these commands can be directly accessed using `[prefix]m [command]`");
    }

    @Override
    protected void buildChildrenCommandHandlers() {

    }
}