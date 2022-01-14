package imageComponent;

import java.util.HashMap;

/**
 * This class simply hold all commands that
 * guild members may use.
 */
public class imageCommands {
    private HashMap<String, String> commands;

    /**
     * The constructor creates an object of the class.
     */
    public imageCommands(){
        // base commands
        commands = new HashMap<>();
        commands.put("sfw", "Searches safebooru.org for an image with the supplied tags. Usage: `>g images sfw tag1 tag2 ...`");
        commands.put("nsfw", "Searches Rule34 for an image with the supplied tags. Usage: `>g images sfw tag1 tag2 ...` Only works in an NSFW channel.");
    }

    /**
     * @return A hashmap of commands. key - command word | value - command description
     */
    public HashMap<String, String> getCommands(){
        return commands;
    }
}
