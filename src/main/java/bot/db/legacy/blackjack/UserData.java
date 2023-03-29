package bot.db.legacy.blackjack;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.joda.time.DateTime;

/**
 * @author Aron Kumarawatta
 * @version 09.06.2022
 */
@Entity("CasinoUserData")
public class UserData {

    private String idField;

    private String memberID;
    private int hour;
    private long losses;
    private long wins;
    private long credits;

    @Id
    private String guildID;

    public UserData(){}

    public UserData(String memberID, String guildID) {
        this.memberID = memberID;
        this.credits = 1000;
        this.wins = 0;
        this.losses = 0;
        DateTime dt = new DateTime();
        this.hour = dt.getHourOfDay();
        this.guildID = guildID;

        this.idField = memberID + guildID;
    }

    public void incrementWins() {
        this.wins++;
    }

    public long getWins() {
        return wins;
    }

    public void incrementLosses() {
        this.losses++;
    }

    public long getLosses() {
        return losses;
    }

    public long getCredits() {
        return credits;
    }

    public void setCredits(long credits) {
        this.credits = credits;
    }

    public boolean incrementLoop() {
        DateTime dt = new DateTime();
        int newHour = dt.getHourOfDay();

        if (newHour != hour) {
            this.credits = (credits + 100);
            this.hour = newHour;
            return true;
        } else {
            return false;
        }
    }

    public String getMemberID() {
        return memberID;
    }
}
