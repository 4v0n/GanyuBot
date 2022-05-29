package ganyu.casino.blackjack;


import ganyu.base.Activity;
import ganyu.base.Bot;
import ganyu.base.ColorScheme;
import ganyu.casino.data.CasinoData;
import ganyu.casino.data.CasinoGuildData;
import ganyu.casino.data.UserData;
import ganyu.command.message.CommandHandler;
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
 * @version 29.05.2022
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
        getCommandCenter().addCommand("play", "starts a round of blackjack. Add a bet to bet credits. Usage: `[prefix] play [bet amount]`",
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

                    if (args.isEmpty()){
                        embed.setDescription("The game will have no bet!");
                        embed.setColor(new Color(255, 150, 0));
                        channel.sendMessageEmbeds(embed.build()).queue();
                        startGame(event, 0);
                        return;
                    }

                    int bet;
                    try {
                        bet = Integer.parseInt(args.get(0));
                    } catch (Exception e){
                        embed.setDescription(args.get(0) + " is not a number!");
                        embed.setColor(ColorScheme.ERROR);
                        channel.sendMessageEmbeds(embed.build()).queue();
                        return;
                    }

                    if (bet <= playerData.getCredits()) {
                        embed.setDescription("You don't have enough credits!" +
                                "\nYou currently have " + playerData.getCredits() + " credits.");
                        embed.setColor(ColorScheme.ERROR);
                        channel.sendMessageEmbeds(embed.build()).queue();
                        return;
                    }

                    startGame(event, bet);
                });

        getCommandCenter().addCommand("profile", "Views your blackjack profile.",
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

                    embed.setColor(new Color(0, 255, 150));
                    channel.sendMessageEmbeds(embed.build()).queue();
                });

        getCommandCenter().addCommand("leaderboard", "Views a leaderboard of the top 5 users on the server.",
                (event, args) -> {
                    handleData(event);
                    MessageChannel channel = event.getChannel();
                    EmbedBuilder embed = new EmbedBuilder();

                    ArrayList<UserData> leaderBoard = activityData.getLeaderBoard();

                    if (leaderBoard != null) {
                        StringBuilder text = new StringBuilder();

                        int range = Math.min(leaderBoard.size(), 5);

                        for (int i = 0; i < range; i++) {
                            text.append(i + 1)
                                    .append(": ")
                                    .append("<@")
                                    .append(leaderBoard.get(i).getMemberID())
                                    .append("> ")
                                    .append(leaderBoard.get(i).getCredits())
                                    .append(" credits")
                                    .append("\n");
                        }

                        if (leaderBoard.size() > 0) {
                            embed.setTitle("Top " + range);
                            embed.setDescription(text.toString());
                            embed.setColor(ColorScheme.RESPONSE);
                            channel.sendMessageEmbeds(embed.build()).queue();
                        } else {
                            embed.setDescription("No one has played blackjack in this server before!");
                            embed.setColor(ColorScheme.ERROR);
                            channel.sendMessageEmbeds(embed.build()).queue();
                        }
                    } else {
                        embed.setDescription("No one has played blackjack in this server before!");
                        embed.setColor(ColorScheme.ERROR);
                        channel.sendMessageEmbeds(embed.build()).queue();
                    }
                });

        getCommandCenter().addCommand("addcredits", "***Admin only command.*** Adds credits to the tagged user. Usage: `[prefix] addcredits @[user] [amount]`",
                (event, args) -> {
                    handleData(event);
                    MessageChannel channel = event.getChannel();

                    if (bot.getAdmins().contains(event.getAuthor().getId()) || verifyPermissions(event.getMember())) {

                        System.out.println(args);
                        try {
                            int amount = Integer.parseInt(args.get(1));

                            UserData player = activityData.getPlayer(event.getMember());
                            player.setCredits(player.getCredits() + amount);


                            activityData.save();

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
        for (Permission permission : member.getPermissions()) {
            if (permission.getName().equals("Manage Channels") || permission.getName().equals("Administrator")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void buildSynonyms() {
        getCommandCenter().addSynonym("p", "play");
        getCommandCenter().addSynonym("pf", "profile");
        getCommandCenter().addSynonym("lb", "leaderboard");
        getCommandCenter().addSynonym("add", "addcredits");
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
        }

        try {
            activityData.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
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

