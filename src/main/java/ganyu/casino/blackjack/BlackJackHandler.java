package ganyu.casino.blackjack;


import ganyu.base.Activity;
import ganyu.base.Bot;
import ganyu.base.ColorScheme;
import ganyu.casino.data.CasinoData;
import ganyu.casino.data.CasinoGuildData;
import ganyu.casino.data.UserData;
import ganyu.command.message.CommandHandler;
import ganyu.command.templatemessage.MultiPageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class allows for blackjack commands to be handled
 *
 * @author Aron Navodh Kumarawatta
 * @version 09.06.2022
 */
public class BlackJackHandler extends CommandHandler {
    private final Bot bot;
    private UserData playerData;
    private CasinoGuildData activityData;

    public BlackJackHandler() {
        super(2);
        this.bot = Bot.getINSTANCE();
    }

    @Override
    public void buildCommands() {
        addCommand("play", "starts a round of blackjack. Add a bet to bet credits. Usage: `[prefix] bj play [bet amount]`",
                (event, args) -> {
                    handleData(event);
                    EmbedBuilder embed = new EmbedBuilder();
                    MessageChannel channel = event.getChannel();

                    Activity activity = bot.getActivities().get(event.getAuthor().getId() + event.getChannel().getId());
                    if (activity != null) {
                        embed.setDescription("You are already playing a mini-game in this channel!" +
                                "\nCurrent game: " + activity.getMessage().getJumpUrl());
                        embed.setColor(ColorScheme.ERROR);
                        channel.sendMessageEmbeds(embed.build()).queue();
                        return;
                    }

                    if (args.isEmpty()) {
                        embed.setDescription("The game will have no bet!");
                        embed.setColor(new Color(255, 150, 0));
                        channel.sendMessageEmbeds(embed.build()).queue();
                        startGame(event, 0);
                        return;
                    }

                    int bet;
                    try {
                        bet = Integer.parseInt(args.get(0));
                    } catch (Exception e) {
                        embed.setDescription(args.get(0) + " is not a number!");
                        embed.setColor(ColorScheme.ERROR);
                        channel.sendMessageEmbeds(embed.build()).queue();
                        return;
                    }

                    if (bet > playerData.getCredits()) {
                        embed.setDescription("You don't have enough credits!" +
                                "\nYou currently have " + playerData.getCredits() + " credits.");
                        embed.setColor(ColorScheme.ERROR);
                        channel.sendMessageEmbeds(embed.build()).queue();
                        return;
                    }

                    startGame(event, bet);
                });

        addCommand("profile", "Views your blackjack profile.",
                (event, args) -> {
                    handleData(event);
                    EmbedBuilder embed = new EmbedBuilder();
                    MessageChannel channel = event.getChannel();

                    embed.setTitle("Profile:");
                    embed.setThumbnail(event.getAuthor().getAvatarUrl());
                    embed.setDescription("<@" + playerData.getMemberID() + ">" +
                            "\nCredits: " + playerData.getCredits() +
                            "\nWins :" + playerData.getWins() +
                            "\nLosses :" + playerData.getLosses());

                    embed.setColor(ColorScheme.RESPONSE);
                    channel.sendMessageEmbeds(embed.build()).queue();
                });

        addCommand("leaderboard", "Views a leaderboard of the top 5 users on the server.",
                (event, args) -> {
                    handleData(event);

                    ArrayList<UserData> leaderBoard = activityData.getLeaderBoard();

                    if (leaderBoard == null || leaderBoard.isEmpty()) {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setDescription("No one has played blackjack in this server before!");
                        embed.setColor(ColorScheme.ERROR);
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                        return;
                    }

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


                    mp.sendMessage(event.getChannel());
                });

        addCommand("addcredits", "***Admin only command.*** Adds credits to the tagged user. Usage: `[prefix] bj addcredits @[user] [amount]`",
                (event, args) -> {
                    handleData(event);
                    MessageChannel channel = event.getChannel();

                    if (bot.getAdmins().contains(event.getAuthor().getId()) || verifyPermissions(event.getMember())) {

                        System.out.println(args);
                        try {
                            long amount = Long.parseLong(args.get(1));

                            UserData player = activityData.getPlayer(event.getMember());
                            player.setCredits(player.getCredits() + amount);


                            activityData.save();

                            event.getMessage().addReaction("✅").queue();

                        } catch (Exception e) {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setDescription("The user was not found!");
                            embed.setColor(ColorScheme.ERROR);
                            channel.sendMessageEmbeds(embed.build()).queue();
                        }
                    } else {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setDescription("You are not an admin / mod!");
                        embed.setColor(ColorScheme.ERROR);
                        channel.sendMessageEmbeds(embed.build()).queue();
                    }
                });
    }

    private boolean verifyPermissions(Member member) {
        return (member.hasPermission(Permission.MANAGE_CHANNEL) || member.hasPermission(Permission.ADMINISTRATOR));
    }

    @Override
    public void buildSynonyms() {
        addSynonym("p", "play");
        addSynonym("pf", "profile");
        addSynonym("lb", "leaderboard");
        addSynonym("add", "addcredits");
    }

    private void handleData(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();

        this.activityData = CasinoData.getInstance().getGuildData(event.getGuild());

        this.playerData = activityData.getPlayer(event.getMember());

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

    private void startGame(MessageReceivedEvent event, int bet) {
        MessageChannel channel = event.getChannel();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("The game will be starting soon!");
        embed.setColor(new Color(255, 255, 150));
        channel.sendMessageEmbeds(embed.build()).queue(newMessage -> bot.addActivity(new Game(event, newMessage, bet)));

    }

    private ArrayList<String> splitString(String string) {
        ArrayList<String> stringArray = new ArrayList<>();
        Scanner tokenizer = new Scanner(string);

        while (tokenizer.hasNext()) {
            stringArray.add(tokenizer.next());
        }
        return stringArray;
    }

}

