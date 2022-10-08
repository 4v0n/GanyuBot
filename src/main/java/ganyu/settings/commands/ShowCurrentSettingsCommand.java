package ganyu.settings.commands;

import ganyu.base.Bot;
import ganyu.base.ColorScheme;
import ganyu.command.message.Command;
import ganyu.data.ServerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static ganyu.command.message.UsefulMethods.sendEmbed;

public class ShowCurrentSettingsCommand implements Command {
    @Override
    public void run(Event event, List<String> args) {
        Guild guild = null;

        if (event instanceof MessageReceivedEvent) {
            guild = ((MessageReceivedEvent) event).getGuild();

        }

        if (event instanceof SlashCommandEvent) {
            guild = ((SlashCommandEvent) event).getGuild();
        }

        ServerData data = Bot.getINSTANCE().getGuildData().get(guild);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Current settings", guild.getIconUrl());
        embed.setDescription(
                "Prefix: " + data.getPrefix() + "\n" +
                        "DJ role name: " + data.getDJRoleName()
        );
        embed.setColor(ColorScheme.RESPONSE);
        sendEmbed(embed, event);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "current";
    }

    @Override
    public @NotNull String getDescription() {
        return "shows the current settings for this server";
    }

    @Override
    public @NotNull CommandData getCommandData() {
        return new CommandData(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}