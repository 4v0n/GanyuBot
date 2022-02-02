package Base;

import Database.GuildData;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.HashMap;

public class Bot{
    private JDABuilder jda;
    private String userID;
    private String token;
    private String prefix;

    // userID+channelID activity
    private HashMap<String, Activity> activities;
    private User user;
    private String pfpURL;

    // guildID guildData
    private HashMap<String, GuildData> guildData;

    public Bot(){
        activities = new HashMap<>();
        guildData = new HashMap<>();
    }

    public void setJda(JDABuilder jda) {
        this.jda = jda;
    }

    public JDABuilder getJda() {
        return jda;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserID() {
        return userID;
    }


    public void setUserID(String userID) {
        this.userID = userID;
        user = User.fromId(userID);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public HashMap<String, Activity> getActivities() {
        return activities;
    }

    private String getKey(Activity activity){
        return (activity.getUserID() + activity.getChannel().getId());
    }

    public User getUser() {
        return user;
    }

    public void addActivity(Activity activity){
        activities.put(getKey(activity), activity);
    }

    public void removeActivity(Activity activity){
        activities.remove(getKey(activity), activity);
    }

    public Activity getRelevantActivity(MessageReceivedEvent event){
        String key = (event.getAuthor().getId() + event.getChannel().getId());
        return activities.get(key);
    }

    public String getPfpURL() {
        return pfpURL;
    }

    public void setPfpURL(String pfpURL) {
        this.pfpURL = pfpURL;
    }

    public HashMap<String, GuildData> getGuildData() {
        return guildData;
    }

    public void addGuildData(String key, GuildData value){
        guildData.put(key, value);
    }

    public void botLoop() {
        while (true) {
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
}
