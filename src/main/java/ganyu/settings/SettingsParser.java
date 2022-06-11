package ganyu.settings;

import ganyu.base.Bot;
import ganyu.base.ColorScheme;
import ganyu.command.message.CommandHandler;
import ganyu.data.ServerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.io.IOException;

/**
 * @author Aron Kumarawatta
 * @version 09.06.2022
 */
public class SettingsParser extends CommandHandler {

    private final ServerData data;

    public SettingsParser(ServerData data) {
        super(2);
        this.data = data;
    }

    @Override
    public void buildCommands() {
        addCommand("prefix", "changes the prefix the bot will listen to on this server. Usage: `[prefix] settings prefix [new prefix]`", (event, args) -> {
            if (args.get(0) == null || args.get(0).equals("")) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("You need to provide a new prefix!");
                embed.setColor(ColorScheme.ERROR);
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }

            data.setPrefix(args.get(0));

            Member self = event.getGuild().getSelfMember();

            self.modifyNickname("(" + data.getPrefix() + ") " + self.getUser().getName()).queue();

            try {
                data.save();

            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("prefix changed to: " + args.get(0));
                embed.setColor(ColorScheme.RESPONSE);
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            }
        });

        addCommand("changedjrole", "changes the name of the DJ role for the music player. " +
                "Usage: [prefix] settings changedjrole [new role name] (This is case sensitive!)", (event, args) -> {

            if (args.get(0) == null || args.get(0).equals("")) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("You need to provide a role name!");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }

            data.setDJRoleName(args.get(0));

            try {
                data.save();

            } catch (IOException e){
                e.printStackTrace();

            } finally {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("DJ role changed to: " + args.get(0));
                embed.setColor(ColorScheme.RESPONSE);
                event.getChannel().sendMessageEmbeds(embed.build()).queue();

            }
        } );

        addCommand("current", "shows the current settings for this server", (event, args) -> {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Current settings", event.getGuild().getIconUrl());
            embed.setDescription(
                    "Prefix: " + data.getPrefix() + "\n" +
                            "DJ role name: " + data.getDJRoleName()
            );
            embed.setColor(ColorScheme.RESPONSE);
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        });

        addCommand("reset", "resets the server settings to default", (event, args) -> {
            Bot bot = Bot.getINSTANCE();
            ServerData serverData = new ServerData(event.getGuild());
            bot.addGuildData(serverData);

            try {
                serverData.save();

            } catch (IOException ignored) {
            }

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Current settings", event.getGuild().getIconUrl());
            embed.setDescription(
                    "reset guild settings"
            );
            embed.setColor(ColorScheme.RESPONSE);
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        });
    }

    @Override
    public void buildSynonyms() {

    }
}
