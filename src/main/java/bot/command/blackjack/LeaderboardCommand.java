package bot.command.blackjack;

import bot.command.Command;
import bot.db.blackjack.CasinoData;
import bot.db.blackjack.CasinoGuildData;
import bot.db.blackjack.UserData;
import bot.util.ColorScheme;
import bot.util.message.MultiPageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardCommand implements Command {
    @Override
    public void run(Event event, List<String> args) {
        if (event instanceof MessageReceivedEvent) {

            CasinoGuildData activityData = CasinoData.getInstance().getGuildData(((MessageReceivedEvent) event).getGuild());
            ArrayList<UserData> leaderBoard = activityData.getLeaderBoard();

            if (leaderBoard == null || leaderBoard.isEmpty()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("No one has played blackjack in this server before!");
                embed.setColor(ColorScheme.ERROR);
                ((MessageReceivedEvent) event).getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }

            MultiPageEmbed embed = buildMessage(leaderBoard);
            embed.sendMessage(((MessageReceivedEvent) event).getChannel());

            return;
        }

        if (event instanceof SlashCommandInteractionEvent){

            CasinoGuildData activityData = CasinoData.getInstance().getGuildData(((SlashCommandInteractionEvent) event).getGuild());
            ArrayList<UserData> leaderBoard = activityData.getLeaderBoard();

            if (leaderBoard == null || leaderBoard.isEmpty()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("No one has played blackjack in this server before!");
                embed.setColor(ColorScheme.ERROR);
                ((SlashCommandInteractionEvent) event).replyEmbeds(embed.build()).queue();
                return;
            }

            MultiPageEmbed embed = buildMessage(leaderBoard);
            embed.replyTo((SlashCommandInteractionEvent) event);

            return;
        }
    }

    private MultiPageEmbed buildMessage(ArrayList<UserData> leaderBoard){
        ArrayList<String> stringArray = new ArrayList<>();

        int i = 1;
        for (UserData user : leaderBoard){
            String sb = "- " +
                    i +
                    " - " +
                    "<@" +
                    user.getMemberID() +
                    "> " +
                    user.getCredits() +
                    " credits";

            stringArray.add(sb);
            i++;
        }

        MultiPageEmbed mp = new MultiPageEmbed(stringArray, 5);
        mp.setTitle("Leaderboard");
        mp.setDescription("The top " + leaderBoard.size() + " player(s) on the server: \n");
        mp.setColor(ColorScheme.RESPONSE);

        return mp;
    }

    @Override
    public @NotNull String getCommandWord() {
        return "leaderboard";
    }

    @Override
    public @NotNull String getDescription() {
        return "Views a leaderboard of the top 5 users on the server.";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"lb"};
    }
}
