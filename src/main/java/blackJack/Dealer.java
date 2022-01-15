package blackJack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Dealer extends Player{
    private final int targetNum;

    public Dealer(String playerID) {
        super(playerID);
        this.targetNum = (21 - new Random().nextInt(5));

    }

    public void turn(Game game, Message message, EmbedBuilder newEmbed) {
        while (this.distanceTo21() > (21 - targetNum)){
            this.addCard(game.getDeck());
            newEmbed.setDescription(getValueOfHand() + "\n" + showCards());
            game.getChannel().editMessageEmbedsById(message.getId(), newEmbed.build()).queueAfter(500, TimeUnit.MILLISECONDS);
        }
        game.getAndShowWinner();
        game.getBot().removeActivity(game);
    }
}
