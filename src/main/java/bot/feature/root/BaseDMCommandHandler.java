package bot.feature.root;

import bot.command.directMessage.DMCommandHandler;
import bot.command.music.authSpotifyDMCommand;

public class BaseDMCommandHandler extends DMCommandHandler {

    private static BaseDMCommandHandler INSTANCE;

    protected BaseDMCommandHandler() {
        super(null);
    }

    public static BaseDMCommandHandler getInstance() {
        if (INSTANCE == null){
            INSTANCE = new BaseDMCommandHandler();
        }

        return INSTANCE;
    }

    @Override
    protected void buildCommands() {
        addCommand(new authSpotifyDMCommand());
    }

    @Override
    protected void buildChildrenCommandHandlers() {

    }
}
