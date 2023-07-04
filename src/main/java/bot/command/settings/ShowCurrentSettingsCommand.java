package bot.command.settings;

import bot.Bot;
import bot.command.Command;
import bot.command.CommandContext;
import bot.db.legacy.server.ServerData;
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

public class ShowCurrentSettingsCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        Guild guild = context.getGuild();

        ServerData data = Bot.getINSTANCE().getGuildData(guild);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Current settings", guild.getIconUrl());
        embed.setDescription(
                "Prefix: " + data.getPrefix() + "\n" +
                        "DJ role name: " + data.getDJRoleName()
        );
        embed.setColor(ColorScheme.RESPONSE);
        context.respondEmbed(embed);
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
