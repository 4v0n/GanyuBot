package bot.command.settings;

import bot.Bot;
import bot.command.ICommand;
import bot.db.server.ServerData;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static bot.command.CommandMethods.sendEmbed;

public class ShowCurrentSettingsICommand implements ICommand {
    @Override
    public void run(Event event, List<String> args) {
        Guild guild = null;

        if (event instanceof MessageReceivedEvent) {
            guild = ((MessageReceivedEvent) event).getGuild();

        }

        if (event instanceof SlashCommandInteractionEvent) {
            guild = ((SlashCommandInteractionEvent) event).getGuild();
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
    public @NotNull CommandDataImpl getCommandData() {
        return new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
