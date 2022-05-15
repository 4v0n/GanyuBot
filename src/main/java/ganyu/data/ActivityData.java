package ganyu.data;

import java.io.Serializable;

/**
 * This class stores data to do with activities
 *
 * @author Aron Navodh Kumarawatta
 * @version 15.05.2022
 */
public abstract class ActivityData implements Serializable {
    private final String identifier;

    public ActivityData(String identifier){
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
