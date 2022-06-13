package ganyu.casino.blackjack;


import ganyu.base.ColorScheme;
import ganyu.casino.blackjack.commands.AddCreditsCommand;
import ganyu.casino.blackjack.commands.LeaderboardCommand;
import ganyu.casino.blackjack.commands.PlayCommand;
import ganyu.casino.blackjack.commands.ProfileCommand;
import ganyu.casino.data.CasinoData;
import ganyu.casino.data.CasinoGuildData;
import ganyu.casino.data.UserData;
import ganyu.command.message.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

/**
 * This class allows for blackjack commands to be handled
 *
 * @author Aron Navodh Kumarawatta
 * @version 09.06.2022
 */
public class BlackJackHandler extends CommandHandler {

    public BlackJackHandler(CommandHandler parent) {
        super(parent);
    }

    public BlackJackHandler(CommandHandler parent, String accessCommand) {
        super(parent, accessCommand);
    }


    @Override
    public void buildCommands() {
        addCommand(new PlayCommand());

        addCommand(new ProfileCommand());

        addCommand(new LeaderboardCommand());

        addCommand(new AddCreditsCommand());
    }

    @Override
    protected void buildChildrenCommandHandlers() {

    }

    @Override
    public void parse(Event event) {
        handleData(event);
        super.parse(event);
    }

    private void handleData(Event event) {

        Guild guild = null;
        MessageChannel channel = null;
        Member member = null;

        if (event instanceof MessageReceivedEvent){
            guild = ((MessageReceivedEvent) event).getGuild();
            channel = ((MessageReceivedEvent) event).getChannel();
            member = ((MessageReceivedEvent) event).getMember();
        }

        if (event instanceof SlashCommandEvent) {
            guild = ((SlashCommandEvent) event).getGuild();
            channel = ((SlashCommandEvent) event).getChannel();
            member = ((SlashCommandEvent) event).getMember();
        }

        if (guild != null && member != null) {
            CasinoGuildData activityData = CasinoData.getInstance().getGuildData(guild);

            UserData playerData = activityData.getPlayer(member);

            boolean autoClaimed = playerData.incrementLoop();

            if (autoClaimed) {
                EmbedBuilder claim = new EmbedBuilder();
                claim.setTitle("Credits");
                claim.setDescription("You automatically earned 100 credits!" +
                        "\nYou are now at " + playerData.getCredits());
                claim.setColor(ColorScheme.RESPONSE);
                channel.sendMessageEmbeds(claim.build()).queue();

                try {
                    activityData.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

