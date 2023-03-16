package bot.command.blackjack;

import bot.command.ICommand;
import bot.util.ColorScheme;
import bot.db.blackjack.CasinoData;
import bot.db.blackjack.UserData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProfileICommand implements ICommand {
    @Override
    public void run(Event event, List<String> args) {
        if (event instanceof MessageReceivedEvent){
            showProfile((MessageReceivedEvent) event);
            return;
        }

        if (event instanceof SlashCommandEvent){
            showProfile((SlashCommandEvent) event);
        }
    }

    private void showProfile(SlashCommandEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        MessageChannel channel = event.getChannel();
        UserData playerData = CasinoData.getInstance().getGuildData(event.getGuild()).getPlayer(event.getMember());

        embed.setTitle("Profile:");
        embed.setThumbnail(event.getMember().getEffectiveAvatarUrl());
        embed.setDescription("<@" + playerData.getMemberID() + ">" +
                "\nCredits: " + playerData.getCredits() +
                "\nWins :" + playerData.getWins() +
                "\nLosses :" + playerData.getLosses());

        embed.setColor(ColorScheme.RESPONSE);
        event.replyEmbeds(embed.build()).queue();
    }

    private void showProfile(MessageReceivedEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        MessageChannel channel = event.getChannel();
        UserData playerData = CasinoData.getInstance().getGuildData(event.getGuild()).getPlayer(event.getMember());

        embed.setTitle("Profile:");
        embed.setThumbnail(event.getMember().getEffectiveAvatarUrl());
        embed.setDescription("<@" + playerData.getMemberID() + ">" +
                "\nCredits: " + playerData.getCredits() +
                "\nWins :" + playerData.getWins() +
                "\nLosses :" + playerData.getLosses());

        embed.setColor(ColorScheme.RESPONSE);
        channel.sendMessageEmbeds(embed.build()).queue();
    }


    @Override
    public @NotNull String getCommandWord() {
        return "profile";
    }

    @Override
    public @NotNull String getDescription() {
        return "Views your blackjack profile.";
    }

    @Override
    public @NotNull CommandData getCommandData() {
        return new CommandData(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"pf"};
    }
}
