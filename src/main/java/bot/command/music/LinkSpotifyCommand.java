package bot.command.music;

import bot.Bot;
import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.music.spotify.SpotifyManager;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LinkSpotifyCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.INFO)
                .setDescription("Check your DMs for more info on how to get started!");
        context.respondEmbed(embed);

        Bot bot = Bot.getInstance();
        User user = context.getAuthor();

        String authURL = SpotifyManager.getInstance(user).getauthCodeUri().toString();

        EmbedBuilder linkEmbed = new EmbedBuilder();
        linkEmbed.setColor(ColorScheme.INFO)
                .setTitle("Link your spotify account")
                .setDescription("Click the button below to link your spotify account:" +
                        "\nAfter confirming the link, you will be taken to a blank page / non existent page. " +
                        "Copy the URL of this page and use it in this command in this DM ***(not the server)***:" +
                        "\n`" + bot.getGlobalPrefix() + " linkspotify [URL]`" +
                        "\n### DO NOT SHARE THE LINK YOU GET FROM SPOTIFY WITH ANYONE ELSE!")
                .setFooter("This method is kinda scuffed but its all that is possible for now." +
                        "\nContact @4v0n with your spotify account's email to add you to the access list (temp)");

        user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessageEmbeds(linkEmbed.build())
                .addActionRow(Button.link(authURL, "Authenticate"))
                .queue());
    }

    @Override
    public @NotNull String getCommandWord() {
        return "linkspotify";
    }

    @Override
    public @NotNull String getDescription() {
        return "Starts the process of linking your spotify account to the bot";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"link", "ls"};
    }
}
