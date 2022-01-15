package blackJack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Deck {

    private static final String[] suits = new String[]{"Diamonds", "Spades", "clubs", "spades"};
    private static final String[] ranks = new String[]{"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};
    private final ArrayList<Card> cards;

    public Deck(){
        cards = new ArrayList<>();
        buildDeck();
    }

    private void buildDeck(){
        for (String suit : suits){
            for (String rank : ranks){
                cards.add(new Card(suit, rank));
            }
        }
    }

    public Card dealCard(){
        Random random = new Random();
        int choice = random.nextInt((cards.size() - 1));
        Card dealtCard = cards.get(choice);
        cards.remove(choice);
        return dealtCard;
    }

    public boolean isEmpty(){
        return (cards.isEmpty());
    }

    public void printDeck(){
        System.out.println(Arrays.toString(suits));
        System.out.println(Arrays.toString(ranks));
    }

    public String[] getRanks(){
        return ranks;
    }
}
