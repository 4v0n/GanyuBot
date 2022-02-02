package blackJack;

import org.joda.time.DateTime;

import java.io.Serializable;

public class BlackjackPlayerData implements Serializable {
    private int hour;
    private int losses;
    private int wins;
    private int tieOffset;
    private String playerID;
    private int credits;



    public BlackjackPlayerData(String playerID){
        this.playerID = playerID;
        this.credits = 1000;
        this.tieOffset = 0;
        this.wins = 0;
        this.losses = 0;
        DateTime dt = new DateTime();
        this.hour = dt.getHourOfDay();
    }

    public void incrementWins(){
        wins++;
    }

    public int getWins() {
        return wins;
    }

    public void incrementLosses(){
        losses++;
    }

    public int getLosses() {
        return losses;
    }

    public int getTieOffset() {
        return tieOffset;
    }

    public int getCredits() {
        return credits;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public void setTieOffset(int offset){
        this.tieOffset = offset;
    }

    public boolean incrementLoop() {
        DateTime dt = new DateTime();
        int newHour = dt.getHourOfDay();
        //System.out.println(hour);
        //System.out.println(newHour);

        if (newHour > hour) {
            this.credits = (credits + 100);
            this.hour = newHour;
            return true;
        } else {
            return false;
        }
    }
}
