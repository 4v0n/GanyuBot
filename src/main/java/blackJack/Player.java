package blackJack;

import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

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

    public void addCard(Card card){
        hand.add(card);
        if (card.getValue() == 11){
            fullAces.add(card);
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
        int value = 0;
        for (Card card : hand){
            int tempValue = card.getValue();
            value = value + tempValue;
        }
        int count = 0;
        ArrayList<Card> aces = fullAces;
        while ((value > 21) && !aces.isEmpty()){
            value = value - 10;
            aces.remove(count);
            count++;
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
