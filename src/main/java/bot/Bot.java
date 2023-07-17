package bot;

import bot.activity.Activity;
import bot.db.legacy.server.ServerData;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;


/**
 * This class details a bot object which simply holds all the variables required by the bot.
 *

 * @author Aron Navodh Kumarawatta
 * @version 29.05.2022
 */
public class Bot {

    private static JDA JDA;
    private static Bot INSTANCE;
    private String token;
    private String prefix;
    // userID+channelID activity
    private final HashMap<String, Activity> activities;
    private Datastore datastore;

    /**
     * Constructor method.
     * Singleton so class cannot be constructed
     */
    private Bot() {
        this.activities = new HashMap<>();
    }

    public static Bot getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new Bot();
        }

        return INSTANCE;
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
        ServerData guildData = this.getGuildData(guild);

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


    public void addGuildData(ServerData guildData) {
        datastore.save(guildData);
    }

    public void setJDA(JDA jda) {
        JDA = jda;
    }

    public static net.dv8tion.jda.api.JDA getJDA() {
        return JDA;
    }

    public ServerData getGuildData(Guild guild) {
        ServerData serverData = datastore.find(ServerData.class)
                    .filter(Filters.eq("guildID", guild.getId()))
                    .iterator()
                    .tryNext();

        if (serverData == null) {
            serverData = new ServerData(guild);
            addGuildData(serverData);
        }

        return serverData;
    }

    public void setDatastore(Datastore datastore) {
        this.datastore = datastore;
    }

    public Datastore getDatastore() {
        return datastore;
    }
}
