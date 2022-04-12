package Base;

import java.util.HashMap;

/**
 * Objects of this class simply hold all available commands under their relevant package.
 */
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
        commands.put("wou", "My shitty 'world of the undead' game that I made for a uni assignment.");
    }

    public HashMap<String, String> getCommands(){
        return commands;
    }
}
