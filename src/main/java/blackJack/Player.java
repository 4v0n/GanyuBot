package blackJack;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

public class Player {
    private User user;
    private int balance = 1000;
    private String playerID;
    private String discordAt;
    private ArrayList<Card> hand;
    private boolean hasLost;

    public Player(String playerID){
        this.hasLost = false;
        this.playerID = playerID;
        this.discordAt = ("<@" + playerID + ">");
        hand = new ArrayList<>();
    }

    public Player(String playerID, User user){
        this.hasLost = false;
        this.playerID = playerID;
        this.user = user;
        this.discordAt = ("<@" + playerID + ">");
        hand = new ArrayList<>();
    }

    public void addCard(Card card){
        hand.add(card);
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
            if (tempValue >= 10){
                tempValue = 10;
            }
            if (tempValue == 1){
                if (value < 21){
                    tempValue = 11;
                }
            }
            value = value + tempValue;
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

    public void turn(Message message){

    }
}
