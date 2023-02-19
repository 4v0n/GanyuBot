package bot.command.settings;

import bot.command.ICommand;
import bot.feature.root.BaseCommandHandler;
import bot.Bot;
import bot.feature.message.CommandMethods;
import bot.db.server.ServerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ResetDataICommand implements ICommand {
    @Override
    public void run(Event event, List<String> args) {
        Guild guild = null;

        if (event instanceof MessageReceivedEvent) {
            guild = ((MessageReceivedEvent) event).getGuild();

        }

        if (event instanceof SlashCommandEvent) {
            guild = ((SlashCommandEvent) event).getGuild();
        }

        ServerData serverData = new ServerData(guild);
        serverData.setCommandSetVersion(BaseCommandHandler.getINSTANCE().hashCode());

        Bot.getINSTANCE().getGuildData().remove(guild);
        Bot.getINSTANCE().addGuildData(serverData);

        guild.getSelfMember().modifyNickname(guild.getSelfMember().getUser().getName()).queue();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Reset settings to defaults");
        CommandMethods.sendEmbed(embed, event);
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
    public @NotNull CommandData getCommandData() {
        return new CommandData(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
