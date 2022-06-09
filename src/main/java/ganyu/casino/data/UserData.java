package ganyu.casino.data;

import org.joda.time.DateTime;
import org.json.simple.JSONObject;

/**
 * @author Aron Kumarawatta
 * @version 09.06.2022
 */
public class UserData {

    private final String memberID;
    private int hour;
    private long losses;
    private long wins;
    private long credits;

    public UserData(String memberID) {
        this.memberID = memberID;
        this.credits = 1000;
        this.wins = 0;
        this.losses = 0;
        DateTime dt = new DateTime();
        this.hour = dt.getHourOfDay();
    }

    public UserData(JSONObject json) {
        this.memberID = json.get("memberID").toString();
        this.credits = Integer.parseInt(json.get("credits").toString());
        this.wins = Integer.parseInt(json.get("wins").toString());
        this.losses = Integer.parseInt(json.get("losses").toString());
        this.hour = Integer.parseInt(json.get("hour").toString());
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

    public JSONObject getJSON() {
        JSONObject json = new JSONObject();
        json.put("memberID", memberID);
        json.put("hour", hour);
        json.put("wins", wins);
        json.put("losses", losses);
        json.put("credits", credits);

        return json;
    }
}
