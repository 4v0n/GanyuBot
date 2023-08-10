package bot.feature.blackjack;

import bot.command.CommandBranch;
import bot.command.blackjack.InGameHitCommand;
import bot.command.blackjack.InGameStandCommand;

/**
 * This allows for in-game blackjack commands to be parsed
 *
 * @author Aron Navodh Kumarawatta
 * @version 28.03.2023
 */
public class BlackJackIngameCommandParser extends CommandBranch {

    public BlackJackIngameCommandParser() {
        super(null);
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
