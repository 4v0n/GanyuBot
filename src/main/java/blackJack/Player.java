package blackJack;

import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;

public class Player {
    private int balance = 1000;
    private String playerID;
    private String discordAt;
    private ArrayList<Card> hand;

    public Player(String playerID){
        this.playerID = playerID;
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
            value = value + card.getValue();
        }
        return value;
    }

    public void turn(Message message){

    }
}
