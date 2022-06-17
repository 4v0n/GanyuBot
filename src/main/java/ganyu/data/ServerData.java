package ganyu.data;

import ganyu.base.BaseCommandHandler;
import ganyu.base.Bot;
import ganyu.command.message.UsefulMethods;
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
    private String DJRoleName;
    private long commandSetVersion;

    public ServerData(Guild guild) {
        this.prefix = Bot.getINSTANCE().getGlobalPrefix();
        this.DJRoleName = "DJ";
        this.guild = guild;
        commandSetVersion = 0;
    }

    public ServerData(Guild guild, File file) throws IOException, ParseException {
        this.guild = guild;

        JSONParser jsonParser = new JSONParser();
        JSONObject json = (JSONObject) jsonParser.parse(new FileReader(file.getAbsolutePath()));

        this.prefix = String.valueOf(json.get("prefix"));
        this.DJRoleName = String.valueOf(json.get("DJRole"));

        String commandsetVer = String.valueOf(json.get("commandsetVer"));

        if (UsefulMethods.checkIsLong(commandsetVer)){
            this.commandSetVersion = Long.parseLong(commandsetVer);
        } else {
            this.commandSetVersion = BaseCommandHandler.getINSTANCE().hashCode();
        }
    }

    public void save() throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("prefix", prefix);
        jsonObject.put("DJRole", DJRoleName);
        jsonObject.put("commandsetVer", commandSetVersion);

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

    public String getDJRoleName() {
        if (DJRoleName == null || DJRoleName.equals("null")){
            return "DJ";
        } else {
            return DJRoleName;
        }
    }

    public void setDJRoleName(String DJRoleName) {
        this.DJRoleName = DJRoleName;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public long getCommandSetVersion() {
        return commandSetVersion;
    }

    public void setCommandSetVersion(long commandSetVersion) {
        this.commandSetVersion = commandSetVersion;
    }
}
