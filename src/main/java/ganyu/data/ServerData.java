package ganyu.data;

import ganyu.base.Bot;
import net.dv8tion.jda.api.entities.Guild;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Holds server data
 *
 * @author Aron Kumarawatta
 * @version 29.05.2022
 */
public class ServerData {

    private final Guild guild;
    private String prefix;

    public ServerData(Guild guild) {
        this.prefix = Bot.getINSTANCE().getGlobalPrefix();
        this.guild = guild;
    }

    public ServerData(Guild guild, File file) throws IOException, ParseException {
        this.guild = guild;

        JSONParser jsonParser = new JSONParser();
        JSONObject json = (JSONObject) jsonParser.parse(new FileReader(file.getAbsolutePath()));

        this.prefix = String.valueOf(json.get("prefix"));

    }

    public void save() throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("prefix", prefix);


        FileWriter fw = new FileWriter(("ServerData/" + guild.getId() + ".json"));
        fw.write(jsonObject.toJSONString());
        fw.flush();
    }

    private String[] getArray(String string){
        string = string.replaceAll("\\s", "");

        StringBuilder sb = new StringBuilder(string);
        if (string.equals("[]")){
            return new String[0];
        }

        sb.deleteCharAt(string.length() - 1);
        sb.deleteCharAt(0);

        String newString = sb.toString();

        return newString.split(",");
    }

    public Guild getGuild() {
        return guild;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
