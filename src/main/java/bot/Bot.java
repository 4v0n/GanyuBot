package bot;

import bot.activity.Activity;
import bot.db.server.ServerData;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * This class details a bot object which simply holds all the variables required by the bot.
 *

 * @author Aron Navodh Kumarawatta
 * @version 29.05.2022
 */
public class Bot {

    private static JDA JDA;

    private static Bot INSTANCE;

    private MongoDatabase DB;

    private String token;
    private String prefix;
    private final ArrayList<String> admins;

    // userID+channelID activity
    private final HashMap<String, Activity> activities;

    // guild guildData
    private final HashMap<Guild, ServerData> guildData;

    /**
     * Constructor method.
     * Singleton so class cannot be constructed
     */
    private Bot() {
        this.activities = new HashMap<>();
        this.admins = new ArrayList<>();
        this.guildData = new HashMap<>();
    }

    public static Bot getINSTANCE() {

        if (INSTANCE == null) {
            INSTANCE = new Bot();
        }

        return INSTANCE;
    }

    public void loadGuildData(Guild guild) {

        if (guildData.containsKey(guild)) {
            return;
        }

        File file = new File("ServerData");

        if (!file.isDirectory()) {
            file.mkdirs();
            return;
        }

        ArrayList<String> fileNames = new ArrayList<>(List.of(file.list()));

        if (!fileNames.contains(guild.getId() + ".json")) {
            return;
        }

        String path = "ServerData/" + guild.getId() + ".json";

        try {
            File serverFile = new File(path);
            ServerData serverData = new ServerData(guild, serverFile);
            guildData.put(guild, serverData);

        } catch (Exception e) {
            // ignore
        }
    }

    public void addAdmin(String userID) {
        admins.add(userID);
    }

    public ArrayList<String> getAdmins() {
        return admins;
    }

    /**
     * @return The bot token.
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the bot token.
     *
     * @param token The token as a string.
     */
    public void setToken(String token) {
        this.token = token;
    }

    public String getGlobalPrefix() {
        return prefix;
    }

    /**
     * This is to be deprecated.
     *
     * @return the prefix that the bot will listen to on the server.
     */
    public String getPrefix(Guild guild) {
        ServerData guildData = this.guildData.get(guild);

        if (guildData == null) {
            return prefix;
        } else {
            return guildData.getPrefix();
        }
    }

    /**
     * This is to be deprecated.
     * Sets the prefix of the bot.
     *
     * @param prefix The bot prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * this returns the hashmap of all currently executed activities.
     *
     * @return
     */
    public HashMap<String, Activity> getActivities() {
        return activities;
    }

    /**
     * this returns the key of an activity.
     *
     * @param activity
     * @return
     */
    private String getKey(Activity activity) {
        return (activity.getUserID() + activity.getChannel().getId());
    }

    /**
     * this adds an activity to the bot.
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        activities.put(getKey(activity), activity);
    }

    /**
     * This removes an activity from the bot.
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        activities.remove(getKey(activity), activity);
    }

    /**
     * This returns an activity if the user from the event is taking part in one.
     *
     * @param event The event to be processed.
     * @return The activity (if any) that the player is taking part in.
     */
    public Activity getRelevantActivity(MessageReceivedEvent event) {
        String key = (event.getAuthor().getId() + event.getChannel().getId());
        return activities.get(key);
    }

    public HashMap<Guild, ServerData> getGuildData() {
        return guildData;
    }

    public void addGuildData(ServerData guildData) {
        this.guildData.put(guildData.getGuild(), guildData);
    }

    public void setJDA(JDA jda) {
        JDA = jda;
    }

    public static net.dv8tion.jda.api.JDA getJDA() {
        return JDA;
    }

    public ServerData getGuildData(Guild guild) {
        ServerData serverData = guildData.get(guild);

        if (serverData == null) {
            serverData = new ServerData(guild);
            addGuildData(serverData);
        }

        return serverData;
    }

    public MongoDatabase getDB() {
        return DB;
    }

    public void setDB(MongoDatabase DB) {
        this.DB = DB;
    }
}
