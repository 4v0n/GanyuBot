package ganyu.image;

import ganyu.command.message.CommandHandler;
import ganyu.image.commands.R34Command;
import ganyu.image.commands.SFWCommand;
import net.kodehawa.lib.imageboards.ImageBoard;


/**
 * This class handles all image related tasks requested by
 * a guild member.
 * <p>
 * Added to fulfil feature request
 *
 * @author Aron Navodh Kumarawatta
 * @version 09.06.2022
 */
public class ImageHandler extends CommandHandler {


    public ImageHandler(CommandHandler parent) {
        super(parent);
        ImageBoard.setUserAgent("http.agent");
    }

    public ImageHandler(CommandHandler parent, String accessCommand) {
        super(parent, accessCommand);
        ImageBoard.setUserAgent("http.agent");
    }

    @Override
    public void buildCommands() {
        addCommand(new SFWCommand());

        addCommand(new R34Command());
    }

    @Override
    protected void buildChildrenCommandHandlers() {

    }
}