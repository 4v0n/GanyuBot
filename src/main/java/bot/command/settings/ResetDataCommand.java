package bot.command.settings;

import bot.Bot;
import bot.command.Command;
import bot.command.CommandContext;
import bot.db.legacy.server.ServerData;
import bot.command.CommandMethods;
import bot.feature.root.BaseCommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ResetDataCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        Guild guild = context.getGuild();

        ServerData serverData = new ServerData(guild);
        serverData.setCommandSetVersion(BaseCommandHandler.getINSTANCE().hashCode());

        Bot.getINSTANCE().addGuildData(serverData);

        guild.getSelfMember().modifyNickname(guild.getSelfMember().getUser().getName()).queue();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Reset settings to defaults");
        context.respondEmbed(embed);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "reset";
    }

    @Override
    public @NotNull String getDescription() {
        return "Resets all bot settings on this server to defaults";
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
