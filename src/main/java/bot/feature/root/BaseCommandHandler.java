package bot.feature.root;

import bot.command.CommandHandler;
import bot.command.root.*;
import bot.feature.blackjack.BlackJackHandler;
import bot.feature.booru.ImageHandler;
import bot.feature.music.MusicParser;
import bot.feature.settings.SettingsParser;

public class BaseCommandHandler extends CommandHandler {

    private static BaseCommandHandler INSTANCE;

    private BaseCommandHandler() {
        super(null);
    }

    public static BaseCommandHandler getInstance(){
        if (INSTANCE == null){
            INSTANCE = new BaseCommandHandler();
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
        addHandler(new SettingsParser(this, "settings"));
        addHandler(new MusicParser(this, "musicplayer"));
        addHandler(new BlackJackHandler(this, "blackjack"));
        addHandler(new ImageHandler(this, "images"));
    }
}
