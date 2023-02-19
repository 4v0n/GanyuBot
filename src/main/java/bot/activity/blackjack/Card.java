package bot.activity.blackjack;

import org.apache.commons.lang3.ArrayUtils;

/**
 * This class represents cards to be used
 * in the blackjack mini-game
 *
 * @author Aron Navodh Kumarawatta
 * @version 15.05.2022
 */
public class Card {
    private final String suit;
    private final String rank;

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public int getValue() {
        String[] referenceRanks = new Deck().getRanks();
        // ace has value 11 in blackjack
        int[] values = new int[]{11, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10};

        return values[ArrayUtils.indexOf(referenceRanks, rank)];

    }

    public String showCard() {
        return (rank + " of " + suit);
    }
}
