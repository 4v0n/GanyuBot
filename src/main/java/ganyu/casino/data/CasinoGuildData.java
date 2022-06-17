package ganyu.casino.data;

import net.dv8tion.jda.api.entities.Member;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * @author Aron Kumarawatta
 * @version 09.06.2022
 */
public class CasinoGuildData {

    private final String guildID;

    // memberID - userData
    private final HashMap<String, UserData> players;

    public CasinoGuildData(String guildID) {
        this.players = new HashMap<>();
        this.guildID = guildID;
    }

    public CasinoGuildData(String guildID, JSONArray jsonArray) {
        this.players = new HashMap<>();
        jsonArray.forEach(emp -> parseUser((JSONObject) emp));
        this.guildID = guildID;
    }

    private void parseUser(JSONObject json) {
        JSONObject userJSON = (JSONObject) json.get("user");

        if (userJSON != null) {
            UserData userData = new UserData(userJSON);
            players.put(userData.getMemberID(), userData);
        }
    }

    public ArrayList<UserData> getLeaderBoard() {
        ArrayList<UserData> leaderBoard = new ArrayList<>(players.values());

        leaderBoard.sort(Comparator.comparingLong(UserData::getCredits).reversed());

        return leaderBoard;
    }

    public void save() throws IOException {
        JSONArray jsonArray = new JSONArray();

        for (UserData user : players.values()) {
            JSONObject userJSON = new JSONObject();
            userJSON.put("user", user.getJSON());
            jsonArray.add(userJSON);
        }

        FileWriter fw = new FileWriter(("CasinoData/" + guildID + ".json"));
        fw.write(jsonArray.toJSONString());
        fw.flush();
    }

    public UserData getPlayer(String id){
        return players.get(id);
    }

    public UserData getPlayer(Member member) {
        UserData userData = players.get(member.getId());

        if (userData == null) {
            userData = new UserData(member.getId());
            players.put(member.getId(), userData);
        }

        return userData;
    }
}
