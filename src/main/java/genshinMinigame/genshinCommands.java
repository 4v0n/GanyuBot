package genshinMinigame;

import java.util.HashMap;

public class genshinCommands {
    HashMap<String, String> commands;

    public genshinCommands(){
        // base commands
        commands = new HashMap<>();
    }

    public HashMap<String, String> getCommands(){
        return commands;
    }
}
