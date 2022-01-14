package Base;

import java.util.HashMap;

public class Command {
    private final HashMap<String, String> commands;

    public Command(){
        // base commands
        commands = new HashMap<>();
        commands.put("help", "Returns a list of commands");
        commands.put("copy", "Copies what you type");
        //commands.put("genshin", "Genshin Impact minigame. Use `>g help genshin` for more info. (Not working at the moment)");
        commands.put("images", "Image commands. Use `>g help images` for more info");
        commands.put("blackjack", "Blackjack minigame. use `>g help blackjack` for more info");
    }

    public HashMap<String, String> getCommands(){
        return commands;
    }
}
