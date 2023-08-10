package bot.feature.booru;

import bot.command.CommandBranch;
import bot.command.booru.R34Command;
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
public class ImageCommandBranch extends CommandBranch {


    public ImageCommandBranch(CommandBranch parent) {
        super(parent);
        ImageBoard.setUserAgent("http.agent");
    }

    public ImageCommandBranch(CommandBranch parent, String accessCommand) {
        super(parent, accessCommand);
        ImageBoard.setUserAgent("http.agent");
    }

    @Override
    public void buildCommands() {
        addCommand(new SFWICommand());

        addCommand(new R34Command());
    }

    @Override
    protected void buildChildrenCommandHandlers() {

    }
}