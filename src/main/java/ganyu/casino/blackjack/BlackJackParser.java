package ganyu.casino.blackjack;

import ganyu.base.Bot;
import ganyu.command.message.CommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * This allows for in-game blackjack commands to be parsed
 *
 * @author Aron Navodh Kumarawatta
 * @version 09.06.2022
 */
public class BlackJackParser extends CommandHandler {

    private final Bot bot;

    public BlackJackParser() {
        super(1);
        this.bot = Bot.getINSTANCE();
    }

    @Override
    public void buildCommands() {
        addCommand("hit", "Gives you one more card.",
                (event, args) -> {
                    Game game = getGame(event);

                    game.getPlayer().addCard(game.getDeck());
                    game.update();
                });

        addCommand("stand", "End your round",
                (event, args) -> getGame(event).finish());
    }

    @Override
    public void buildSynonyms() {
        addSynonym("h", "hit");
        addSynonym("s", "stand");
    }

    private Game getGame(MessageReceivedEvent event) {
        return (Game) bot.getActivities().get(event.getAuthor().getId() + event.getChannel().getId());
    }
}
