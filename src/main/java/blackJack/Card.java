package blackJack;

import java.util.ArrayList;
import java.util.Arrays;

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
        String[] ranks = new String[]{"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};
        ArrayList<String> tempRanks = new ArrayList<>(Arrays.asList(ranks));

        return tempRanks.indexOf(rank);
    }
}
