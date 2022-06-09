package ganyu.settings;

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
        addCommand("prefix", "changes the prefix the bot will listen to on this server. Usage: `[prefix] prefix [new prefix]`", (event, args) -> {
            if (args.get(0) == null || args.get(0).equals("")) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("You need to provide a new prefix!");
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
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            }
        });
    }

    @Override
    public void buildSynonyms() {

    }
}
