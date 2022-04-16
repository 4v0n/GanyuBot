package Base;

import Database.GuildData;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * This class details a bot object which simply holds all the variables required by the bot.
 */
public class Bot{
    private JDABuilder jda;
    private String userID;
    private String token;
    private String prefix;
    private boolean isAutosaveActive = false;
    private ArrayList<String> admins;

    // userID+channelID activity
    private HashMap<String, Activity> activities;
    private User user;
    private String pfpURL;

    // guildID guildData
    private HashMap<String, GuildData> guildData;

    // shorthand original
    private HashMap<String, String> synonyms;

    /**
     * Constructor method.
     */
    public Bot(){
        this.activities = new HashMap<>();
        this.guildData = new HashMap<>();
        this.admins = new ArrayList<>();
    }

    public void addAdmin(String userID){
        admins.add(userID);
    }

    public ArrayList<String> getAdmins() {
        return admins;
    }

    /**
     * Sets the jda being used by the bot,
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
     *
     * @return The bot token.
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the bot's bot token.
     * @param token The token as a string.
     */
    public void setToken(String token) {
        this.token = token;
    }


    /**
     *
     * @return The user ID of the bot.
     */
    public String getUserID() {
        return userID;
    }

    /**
     * sets the user ID
     * @param userID the user ID (Application ID) of the bot.
     */
    public void setUserID(String userID) {
        this.userID = userID;
        user = User.fromId(userID);
    }

    /**
     * This is to be deprecated.
     *
     * @return the prefix used by the bot.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * This is to be deprecated.
     * Sets the prefix of the bot.
     * @param prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * this returns the hashmap of all currently executed activities.
     * @return
     */
    public HashMap<String, Activity> getActivities() {
        return activities;
    }

    /**
     * this returns the key of an activity.
     * @param activity
     * @return
     */
    private String getKey(Activity activity){
        return (activity.getUserID() + activity.getChannel().getId());
    }

    /**
     * This returns the bot's user object.
     * @return
     */
    public User getUser() {
        return user;
    }

    /**
     * this adds an activity to the bot.
     * @param activity
     */
    public void addActivity(Activity activity){
        activities.put(getKey(activity), activity);
    }

    /**
     * This removes an activity from the bot.
     * @param activity
     */
    public void removeActivity(Activity activity){
        activities.remove(getKey(activity), activity);
    }

    /**
     * This returns an activity if the user from the event is taking part in one.
     * @param event The event to be processed.
     * @return The activity (if any) that the player is taking part in.
     */
    public Activity getRelevantActivity(MessageReceivedEvent event){
        String key = (event.getAuthor().getId() + event.getChannel().getId());
        return activities.get(key);
    }

    /**
     *
     * @return The url of the pfp of the bot.
     */
    public String getPfpURL() {
        return pfpURL;
    }

    /**
     * Sets the pfp url of the bot.
     * @param pfpURL
     */
    public void setPfpURL(String pfpURL) {
        this.pfpURL = pfpURL;
    }

    /**
     *
     * @return HashMap of all guildData.
     */
    public HashMap<String, GuildData> getGuildData() {
        return guildData;
    }

    /**
     * Adds a new guild data object to the class.
     * @param key The identifier of the guild (GuildID).
     * @param value The GuildData object.
     */
    public void addGuildData(String key, GuildData value){
        guildData.put(key, value);
    }

    public void setAutosaveActive(boolean value) {
        isAutosaveActive = value;
    }

    /**
     * Autosaves all guild data held by the bot every 5 seconds.
     */
    public void botLoop() {
        while (isAutosaveActive) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (GuildData data : guildData.values()) {
                try {
                    data.save();
                    //System.out.println("saved");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public HashMap<String, String> getSynonyms() {
        return synonyms;
    }

    public boolean inActivity(){
        return false;
    }
}
