package Database;

import java.io.Serializable;

public class ActivityData implements Serializable {
    private final String identifier;

    public ActivityData(String identifier){
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
