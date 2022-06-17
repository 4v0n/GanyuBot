package ganyu.casino.blackjack;

import ganyu.base.Bot;
import ganyu.casino.blackjack.commands.InGameHitCommand;
import ganyu.casino.blackjack.commands.InGameStandCommand;
import ganyu.command.message.CommandHandler;

/**
 * This allows for in-game blackjack commands to be parsed
 *
 * @author Aron Navodh Kumarawatta
 * @version 09.06.2022
 */
public class BlackJackParser extends CommandHandler {

    private final Bot bot;

    public BlackJackParser() {
        super(null);
        this.bot = Bot.getINSTANCE();
    }

    @Override
    public void buildCommands() {
        addCommand(new InGameHitCommand());

        addCommand(new InGameStandCommand());
    }

    @Override
    protected void buildChildrenCommandHandlers() {

    }
}
