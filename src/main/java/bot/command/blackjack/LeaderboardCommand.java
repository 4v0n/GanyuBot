package bot.command.blackjack;

import bot.command.Command;
import bot.command.CommandContext;
import bot.db.legacy.blackjack.CasinoData;
import bot.db.legacy.blackjack.CasinoGuildData;
import bot.db.legacy.blackjack.UserData;
import bot.util.ColorScheme;
import bot.util.message.MultiPageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.entities.channel.concrete.TextChannelImpl;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        CasinoGuildData activityData = CasinoData.getInstance().getGuildData(context.getGuild());
        ArrayList<UserData> leaderBoard = activityData.getLeaderBoard();

        if (leaderBoard == null || leaderBoard.isEmpty()) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("No one has played blackjack in this server before!");
            embed.setColor(ColorScheme.ERROR);
            context.respondEmbed(embed);
            return;
        }

        MultiPageEmbed embed = buildMessage(leaderBoard);
        context.respondMPE(embed);

    }

    private MultiPageEmbed buildMessage(ArrayList<UserData> leaderBoard) {
        ArrayList<String> stringArray = new ArrayList<>();

        int i = 1;
        for (UserData user : leaderBoard) {
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
