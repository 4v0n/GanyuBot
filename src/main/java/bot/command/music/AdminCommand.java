package bot.command.music;

import bot.command.Command;
import bot.command.CommandContext;
import bot.db.Admin;
import bot.feature.root.BaseCommandHandler;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdminCommand implements Command {

    private final CommandDataImpl commandData;

    public AdminCommand() {
        commandData = new CommandDataImpl(getCommandWord(), "Music admin commands");
    }

    @Override
    public void run(CommandContext context, List<String> args) {

        if (!Admin.isAdmin(context.getAuthor())) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("You don't have permissions to use this command!");
            embed.setDescription("Only bot admins have access to these commands");
            embed.setColor(ColorScheme.ERROR);
            context.respondEmbed(embed);
            return;
        }

        BaseCommandHandler.getInstance().getChildren().get("musicplayer").getChildren().get(getCommandWord()).parse(context.getEvent());
    }

    @Override
    public @NotNull String getCommandWord() {
        return "admin";
    }

    @Override
    public @NotNull String getDescription() {
        return "Music bot admin commands. ***Only Bot admins can use these commands*** use `[prefix] mp a help` for more info. These commands can be directly accessed by using `[prefix]m l ...`";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"a"};
    }
}
