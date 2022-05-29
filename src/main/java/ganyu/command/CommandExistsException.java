package ganyu.command;

/**
 * @author Aron Kumarawatta
 * @version 29.05.2022
 */
public class CommandExistsException extends RuntimeException {

    public CommandExistsException(String commandName) {
        super("The '" + commandName + "' command already exists");
    }
}
