package bot.feature.root;

import bot.command.CommandBranch;
import bot.command.root.*;
import bot.feature.blackjack.BlackJackCommandBranch;
import bot.feature.booru.ImageCommandBranch;
import bot.feature.music.MusicCommandBranch;
import bot.feature.settings.SettingsCommandBranch;

public class BaseCommandBranch extends CommandBranch {

    private static BaseCommandBranch INSTANCE;

    private BaseCommandBranch() {
        super(null);
    }

    public static BaseCommandBranch getInstance(){
        if (INSTANCE == null){
            INSTANCE = new BaseCommandBranch();
        }

        return INSTANCE;
    }

    @Override
    protected void buildCommands() {
        addCommand(new CopyCommand());

        addCommand(new ImagesCommand());

        addCommand(new BlackjackCommand());

        addCommand(new MusicPlayerCommand());

        addCommand(new SettingsCommand());

        addCommand(new InfoCommand());

        addCommand(new PurgeCommand());
    }

    @Override
    protected void buildChildrenCommandHandlers() {
        addHandler(new SettingsCommandBranch(this, "settings"));
        addHandler(new MusicCommandBranch(this, "musicplayer"));
        addHandler(new BlackJackCommandBranch(this, "blackjack"));
        addHandler(new ImageCommandBranch(this, "images"));
    }
}
