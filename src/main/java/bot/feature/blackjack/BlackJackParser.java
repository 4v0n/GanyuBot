package bot.feature.blackjack;

import bot.Bot;
import bot.command.blackjack.InGameHitICommand;
import bot.feature.message.CommandHandler;
import bot.command.blackjack.InGameStandICommand;

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
        addCommand(new InGameHitICommand());

        addCommand(new InGameStandICommand());
    }

    @Override
    protected void buildChildrenCommandHandlers() {

    }
}
