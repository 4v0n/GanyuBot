package Casino.Blackjack;

import Base.Bot;
import Base.Main;
import CommandStructure.CommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BlackJackParser extends CommandHandler {

    private Bot bot;

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
