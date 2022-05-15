package ganyu.casino.blackjack;

import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * This class represents a player in blackjack
 *
 * @author Aron Navodh Kumarawatta
 * @version 15.05.2022
 */
public class Player {
    private User user;
    private final String discordAt;
    private final ArrayList<Card> hand;
    private boolean hasLost;

    public Player(String playerID){
        this.hasLost = false;
        this.discordAt = ("<@" + playerID + ">");
        hand = new ArrayList<>();
    }

    public Player(String playerID, User user){
        this.hasLost = false;
        this.user = user;
        this.discordAt = ("<@" + playerID + ">");
        hand = new ArrayList<>();
    }

    public void addCard(Deck deck){
        boolean done = false;
        while (!done && !deck.isEmpty()) {
            Card card = deck.dealCard();
            if (!hand.contains(card)) {
                hand.add(card);
                done = true;
            }
        }
    }

    public ArrayList<Card> getHand(){
        return hand;
    }

    public String getDiscordAt(){
        return discordAt;
    }

    public int getValueOfHand(){
        ArrayList<Integer> tempValues = new ArrayList<>();
        int value = 0;
        for (Card card : hand){
            tempValues.add(card.getValue());
        }
        tempValues.sort(Comparator.naturalOrder());

        for (int cardValue : tempValues){
            if (((value + cardValue) > 21) && (cardValue == 11)){
                value = value + 1;
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
        return 21 - getValueOfHand();
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
