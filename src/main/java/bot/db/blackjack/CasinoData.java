package bot.db.blackjack;

import bot.Bot;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import dev.morphia.Datastore;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;

/**
 * @author Aron Kumarawatta
 * @version 30.05.2022
 */
public class CasinoData {

    private static CasinoData INSTANCE;

    //GuildID data
    private final HashMap<String, CasinoGuildData> guildData;

    private CasinoData() {
        guildData = new HashMap<>();
        load();
    }

    public static CasinoData getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CasinoData();
        }

        return INSTANCE;
    }

    public CasinoGuildData getGuildData(Guild guild) {
        CasinoGuildData guildData = this.guildData.get(guild.getId());

        if (guildData == null) {
            guildData = new CasinoGuildData(guild.getId());
            this.guildData.put(guild.getId(), guildData);
        }

        return guildData;
    }

    public void load() {
        List<Guild> guilds = Bot.getJDA().getGuilds();
        for (Guild guild : guilds) {
            guildData.put(guild.getId(), new CasinoGuildData(guild));
        }
    }
}
