package ganyu.settings.commands;

import ganyu.base.Bot;
import ganyu.base.ColorScheme;
import ganyu.command.message.Command;
import ganyu.data.ServerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static ganyu.command.message.UsefulMethods.sendEmbed;
import static ganyu.command.message.UsefulMethods.sendEphemeralEmbed;

public class ChangePrefixCommand implements Command {
    @Override
    public void run(Event event, List<String> args) {
        Member self = null;
        Guild guild = null;
        String newPrefix = null;

        if (event instanceof MessageReceivedEvent) {
            self = ((MessageReceivedEvent) event).getGuild().getSelfMember();
            guild = ((MessageReceivedEvent) event).getGuild();

            if (args.get(0) == null || args.get(0).equals("")) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("You need to provide a new prefix!");
                embed.setColor(ColorScheme.ERROR);
                sendEphemeralEmbed(embed, event);
                return;
            }

            newPrefix = args.get(0);
        }

        if (event instanceof SlashCommandEvent) {
            self = ((SlashCommandEvent) event).getGuild().getSelfMember();
            guild = ((SlashCommandEvent) event).getGuild();

            newPrefix = ((SlashCommandEvent) event).getOption("prefix").getAsString();
        }

        ServerData data = Bot.getINSTANCE().getGuildData().get(guild);

        data.setPrefix(newPrefix);

        self.modifyNickname("(" + data.getPrefix() + ") " + self.getUser().getName()).queue();

        try {
            data.save();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("prefix changed to: " + args.get(0));
            embed.setColor(ColorScheme.RESPONSE);
            sendEmbed(embed, event);
        }
    }

    @Override
    public @NotNull String getCommandWord() {
        return "prefix";
    }

    @Override
    public @NotNull String getDescription() {
        return "changes the prefix the bot will listen to on this server. Usage: `[prefix] settings prefix [new prefix]`";
    }

    @Override
    public @NotNull CommandData getCommandData() {
        CommandData commandData = new CommandData(getCommandWord(), "changes the prefix the bot will listen to on this server.");
        commandData.addOption(OptionType.STRING, "prefix", "the new prefix that the bot will listen to", true);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
