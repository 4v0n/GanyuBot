package Base;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;

public class Bot {
    private JDABuilder jda;
    private String userID;
    private String token;
    private String prefix;
    private HashMap<String, Activity> activities;

    public Bot(){
        activities = new HashMap<>();
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
}
