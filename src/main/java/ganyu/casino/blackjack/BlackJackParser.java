package ganyu.casino.blackjack;

import ganyu.base.Bot;
import ganyu.base.Main;
import ganyu.command.message.CommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * This allows for ingame blackjack commands to be parsed
 *
 * @author Aron Navodh Kumarawatta
 * @version 15.05.2022
 */
public class BlackJackParser extends CommandHandler {

    private final Bot bot;

    public BlackJackParser() {
        super(1);
        this.bot = Main.getBotData();
    }

    @Override
    public void buildCommands() {
        getCommandCenter().addCommand("hit", "Gives you one more card.",
                (event, args) -> {
                    Game game = getGame(event);

                    game.getPlayer().addCard(game.getDeck());
                    game.update();
                });

        getCommandCenter().addCommand("stand", "End your round",
                (event, args) -> {
                    getGame(event).finish();
                });
    }

    @Override
    public void buildSynonyms() {
        getCommandCenter().addSynonym("h","hit");
        getCommandCenter().addSynonym("s","stand");
    }

    private Game getGame(MessageReceivedEvent event){
        return (Game) bot.getActivities().get(event.getAuthor().getId() + event.getChannel().getId());
    }
}
