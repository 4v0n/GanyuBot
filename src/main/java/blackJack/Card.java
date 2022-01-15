package blackJack;

import org.apache.commons.lang3.ArrayUtils;

public class Card {
    private String suit;
    private String rank;

    public Card(String suit, String rank){
        this.suit = suit;
        this.rank = rank;
    }

    public String getSuit(){
        return suit;
    }

    public int getValue(){
        String[] referenceRanks = new Deck().getRanks();
        int[] values = new int[]{11, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10};

        return values[ArrayUtils.indexOf(referenceRanks, rank)];

    }

    public String showCard(){
        return (rank + " of " + suit);
    }
}
