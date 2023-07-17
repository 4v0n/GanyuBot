package bot.feature.root;

import bot.command.directMessage.DMCommandHandler;

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

    }

    @Override
    protected void buildChildrenCommandHandlers() {

    }
}
