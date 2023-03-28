package bot.feature.blackjack;


import bot.command.blackjack.AddCreditsICommand;
import bot.command.blackjack.LeaderboardICommand;
import bot.command.blackjack.PlayICommand;
import bot.command.blackjack.ProfileICommand;
import bot.db.blackjack.CasinoData;
import bot.db.blackjack.CasinoGuildData;
import bot.db.blackjack.UserData;
import bot.command.CommandHandler;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

/**
 * This class allows for blackjack commands to be handled
 *
 * @author Aron Navodh Kumarawatta
 * @version 28.03.2023
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
        addCommand(new PlayICommand());

        addCommand(new ProfileICommand());

        addCommand(new LeaderboardICommand());

        addCommand(new AddCreditsICommand());
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

        if (event instanceof SlashCommandInteractionEvent) {
            guild = ((SlashCommandInteractionEvent) event).getGuild();
            channel = ((SlashCommandInteractionEvent) event).getChannel();
            member = ((SlashCommandInteractionEvent) event).getMember();
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

