package Database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class GuildData implements Serializable {

    private HashMap<String, ActivityData> activityData;
    private final String guildID;

    public GuildData(String guildID){
        activityData = new HashMap<>();
        this.guildID = guildID;
    }

    public HashMap<String, ActivityData> getActivityData() {
        return activityData;
    }

    public void addActivityData(ActivityData activityData){
        this.activityData.put(activityData.getIdentifier(), activityData);
    }

    public void save() throws IOException {
        String fileName= ("GuildData/"+guildID+".dta");
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.close();
    }
}
