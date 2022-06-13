package ganyu.base;

import ganyu.base.commands.*;
import ganyu.casino.blackjack.BlackJackHandler;
import ganyu.command.message.CommandHandler;
import ganyu.image.ImageHandler;
import ganyu.music.MusicParser;
import ganyu.settings.SettingsParser;

public class BaseCommandHandler extends CommandHandler {

    private static BaseCommandHandler INSTANCE;

    private BaseCommandHandler() {
        super(null);
    }

    public static BaseCommandHandler getINSTANCE(){
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
    }

    @Override
    protected void buildChildrenCommandHandlers() {
        addHandler(new SettingsParser(this, "settings"));
        addHandler(new MusicParser(this, "musicplayer"));
        addHandler(new BlackJackHandler(this, "blackjack"));
        addHandler(new ImageHandler(this, "images"));
    }
}
