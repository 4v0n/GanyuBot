package blackJack;

import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Comparator;

public class Player {
    private User user;
    private int balance = 1000;
    private String playerID;
    private String discordAt;
    private ArrayList<Card> hand;
    private boolean hasLost;
    private ArrayList<Card> fullAces;

    public Player(String playerID){
        this.hasLost = false;
        this.playerID = playerID;
        this.discordAt = ("<@" + playerID + ">");
        this.fullAces = new ArrayList<>();
        hand = new ArrayList<>();
    }

    public Player(String playerID, User user){
        this.hasLost = false;
        this.playerID = playerID;
        this.user = user;
        this.discordAt = ("<@" + playerID + ">");
        this.fullAces = new ArrayList<>();
        hand = new ArrayList<>();
    }

    public void addCard(Deck deck){
        boolean done = false;
        while (!done && !deck.isEmpty()) {
            Card card = deck.dealCard();
            if (!hand.contains(card)) {
                hand.add(card);
                if (card.getValue() == 11) {
                    fullAces.add(card);
                }
                done = true;
            }
        }
    }

    public ArrayList<Card> getHand(){
        return hand;
    }

    public String getPlayerID(){
        return playerID;
    }

    public String getDiscordAt(){
        return discordAt;
    }

    public void emptyHand(){
        for (Card card : hand){
            hand.remove(card);
        }
    }

    public int getValueOfHand(){
        ArrayList<Integer> tempValues = new ArrayList<>();
        int value = 0;
        for (Card card : hand){
            tempValues.add(card.getValue());
        }
        tempValues.sort(Comparator.naturalOrder());

        for (int cardValue : tempValues){
            if (((value + cardValue)> 21) && (cardValue == 11)){
                value++;
            } else {
                value = value + cardValue;
            }
        }
        return value;
    }

    public boolean hasLost() {
        return hasLost;
    }

    public int distanceTo21(){
        return 21-getValueOfHand();
    }

    public void setLost(boolean hasLost) {
        this.hasLost = hasLost;
    }

    public String showCards(){
        StringBuilder newString = new StringBuilder();
        for (Card card : hand){
            newString.append(card.showCard()).append(" \n");
        }
        return newString.toString();
    }

    public User getUser() {
        return user;
    }
}
