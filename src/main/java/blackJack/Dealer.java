package blackJack;

import java.util.Random;

public class Dealer extends Player{
    private int targetNum;

    public Dealer(String playerID) {
        super(playerID);
        this.targetNum = (21 - new Random().nextInt(5));

    }

    public void turn(Game game) {
        while (this.distanceTo21() > (21 - targetNum)){
            this.addCard(game.getDeck().dealCard());
        }
    }
}
