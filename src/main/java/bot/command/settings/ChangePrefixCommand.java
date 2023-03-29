package bot.command.settings;

import bot.Bot;
import bot.command.Command;
import bot.db.legacy.server.ServerData;
import bot.util.ColorScheme;
import dev.morphia.Datastore;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static bot.command.CommandMethods.sendEmbed;
import static bot.command.CommandMethods.sendEphemeralEmbed;

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

        if (event instanceof SlashCommandInteractionEvent) {
            self = ((SlashCommandInteractionEvent) event).getGuild().getSelfMember();
            guild = ((SlashCommandInteractionEvent) event).getGuild();

            newPrefix = ((SlashCommandInteractionEvent) event).getOption("prefix").getAsString();
        }

        ServerData data = Bot.getINSTANCE().getGuildData(guild);

        data.setPrefix(newPrefix);

        self.modifyNickname("(" + data.getPrefix() + ") " + self.getUser().getName()).queue();

        try {
            Datastore datastore = Bot.getINSTANCE().getDatastore();
            datastore.save(data);

        } catch (Exception e) {
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
    public @NotNull CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl(getCommandWord(), "changes the prefix the bot will listen to on this server.");
        commandData.addOption(OptionType.STRING, "prefix", "the new prefix that the bot will listen to", true);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
