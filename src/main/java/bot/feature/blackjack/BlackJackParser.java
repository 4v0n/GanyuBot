package bot.feature.blackjack;

import bot.Bot;
import bot.command.blackjack.InGameHitCommand;
import bot.command.CommandHandler;
import bot.command.blackjack.InGameStandCommand;

/**
 * This allows for in-game blackjack commands to be parsed
 *
 * @author Aron Navodh Kumarawatta
 * @version 28.03.2023
 */
public class BlackJackParser extends CommandHandler {

    private final Bot bot;

    public BlackJackParser() {
        super(null);
        this.bot = Bot.getInstance();
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
