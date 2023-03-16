package bot.activity.blackjack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * This class represents the dealer in a game of blackjack
 *
 * @author Aron Navodh Kumarawatta
 * @version 29.05.2022
 */
public class Dealer extends Player {

    private final int targetNum;

    public Dealer(String playerID) {
        super(playerID);
        this.targetNum = (21 - new Random().nextInt(5));

    }

    public void turn(Game game, Message message, EmbedBuilder newEmbed) {



        while ((this.getValueOfHand() < targetNum) && (this.getValueOfHand() <= game.getPlayer().getValueOfHand()) && (game.getPlayer().getValueOfHand() <= 21)) {
            this.addCard(game.getDeck());
            newEmbed.setDescription(getValueOfHand() + "\n" + showCards());

            try {
                TimeUnit.MILLISECONDS.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            game.getChannel().editMessageEmbedsById(message.getId(), newEmbed.build()).queue();
        }

        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        game.getAndShowWinner();
        game.getBot().removeActivity(game);
    }
}
