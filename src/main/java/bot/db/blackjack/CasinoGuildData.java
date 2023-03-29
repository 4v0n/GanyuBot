package bot.db.blackjack;

import bot.Bot;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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

        loadFromDB();
    }


    public CasinoGuildData(Guild guild) {
        this.players = new HashMap<>();
        this.guildID = guild.getId();

        loadFromDB();
    }

    private void loadFromDB() {
        Datastore datastore = Bot.getINSTANCE().getDatastore();

        List<UserData> userDataList = datastore.find(UserData.class)
                .filter(Filters.eq("guildID", guildID))
                .iterator()
                .toList();

        for (UserData user : userDataList) {
            players.put(user.getMemberID(), user);
        }
    }

    public ArrayList<UserData> getLeaderBoard() {
        ArrayList<UserData> leaderBoard = new ArrayList<>(players.values());

        leaderBoard.sort(Comparator.comparingLong(UserData::getCredits).reversed());

        return leaderBoard;
    }

    public void save() {
        Datastore ds = Bot.getINSTANCE().getDatastore();
        for (UserData user : players.values()) {
            ds.save(user);
        }
    }

    public UserData getPlayer(String id){
        return players.get(id);
    }

    public UserData getPlayer(Member member) {
        UserData userData = players.get(member.getId());

        if (userData == null) {
            userData = new UserData(member.getId(), member.getGuild().getId());
            players.put(member.getId(), userData);
        }

        return userData;
    }
}
