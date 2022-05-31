package ganyu.casino.data;

import net.dv8tion.jda.api.entities.Guild;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

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
        File directory = new File("CasinoData");

        if (!directory.isDirectory()) {
            directory.mkdirs();
            return;
        }

        for (String filename : directory.list()) {
            if (filename.endsWith(".json")) {
                try {
                    String guildID = filename.substring(0, filename.length() - 5);

                    JSONParser parser = new JSONParser();
                    JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("CasinoData/" + filename));

                    guildData.put(guildID, new CasinoGuildData(guildID, jsonArray));

                } catch (Exception e) {
                    // ignored
                }
            }
        }
    }
}
