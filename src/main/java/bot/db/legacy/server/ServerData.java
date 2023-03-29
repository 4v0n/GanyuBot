package bot.db.legacy.server;

import bot.Bot;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import net.dv8tion.jda.api.entities.Guild;

/**
 * Holds server data
 *
 * @author Aron Kumarawatta
 * @version 28.03.2023
 */

@Entity("ServerData")
public class ServerData {

    @Id
    private String guildID;
    private String prefix;
    private String DJRoleName;
    private long commandSetVersion;

    public ServerData(){}
    public ServerData(Guild guild) {
        this.prefix = Bot.getINSTANCE().getGlobalPrefix();
        this.DJRoleName = "DJ";
        this.guildID = guild.getId();
        commandSetVersion = 0;
    }

    public Guild getGuild() {
        return Bot.getJDA().getGuildById(guildID);
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
