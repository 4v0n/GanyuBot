package bot.feature.booru;

import bot.command.CommandHandler;
import bot.command.booru.R34ICommand;
import bot.command.booru.SFWICommand;
import net.kodehawa.lib.imageboards.ImageBoard;


/**
 * This class handles all image related tasks requested by
 * a guild member.
 * <p>
 * Added to fulfil feature request
 *
 * @author Aron Navodh Kumarawatta
 * @version 28.03.2023
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
        addCommand(new SFWICommand());

        addCommand(new R34ICommand());
    }

    @Override
    protected void buildChildrenCommandHandlers() {

    }
}