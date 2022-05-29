package ganyu.base;

import ganyu.data.ServerData;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
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

    private static Bot INSTANCE;

    private JDABuilder jda;
    private String userID;
    private String token;
    private String prefix;
    private final ArrayList<String> admins;

    // userID+channelID activity
    private final HashMap<String, Activity> activities;
    private User user;
    private String pfpURL;

    // guild guildData
    private final HashMap<Guild, ServerData> guildData;

    /**
     * Constructor method.
     */
    public Bot() {
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
     * Sets the jda being used by the bot,
     *
     * @param jda The JDA.
     */
    public void setJda(JDABuilder jda) {
        this.jda = jda;
    }

    /**
     * @return Return the jda.
     */
    public JDABuilder getJda() {
        return jda;
    }

    /**
     * @return The bot token.
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the bot's bot token.
     *
     * @param token The token as a string.
     */
    public void setToken(String token) {
        this.token = token;
    }


    /**
     * @return The user ID of the bot.
     */
    public String getUserID() {
        return userID;
    }

    /**
     * sets the user ID
     *
     * @param userID the user ID (Application ID) of the bot.
     */
    public void setUserID(String userID) {
        this.userID = userID;
        user = User.fromId(userID);
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

    /**
     * @return The url of the pfp of the bot.
     */
    public String getPfpURL() {
        return pfpURL;
    }

    /**
     * Sets the pfp url of the bot.
     *
     * @param pfpURL
     */
    public void setPfpURL(String pfpURL) {
        this.pfpURL = pfpURL;
    }

    public HashMap<Guild, ServerData> getGuildData() {
        return guildData;
    }

    public void addGuildData(ServerData guildData) {
        this.guildData.put(guildData.getGuild(), guildData);
    }
}
