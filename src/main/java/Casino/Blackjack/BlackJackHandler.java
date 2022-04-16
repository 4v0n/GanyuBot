package Casino.Blackjack;


import Base.Activity;
import Base.Bot;
import Base.ColorScheme;
import Casino.CasinoData;
import Casino.CasinoPlayerData;
import CommandStructure.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

public class BlackJackHandler extends CommandHandler {
    private final Bot bot;
    private CasinoPlayerData playerData;
    private CasinoData activityData;

    public BlackJackHandler(Bot bot) {
        super(2);
        this.bot = bot;
    }

    @Override
    public void buildCommands() {
        getCommandCenter().addCommand("play", "starts a round of blackjack. Add a bet to bet credits. Usage: `play [BET]`",
                (event, args) -> {
                    handleData(event);
                    EmbedBuilder embed = new EmbedBuilder();
                    MessageChannel channel = event.getChannel();

                    if (!args.isEmpty()) {
                        try {
                            int bet = Integer.parseInt(args.get(0));
                            if (bet <= playerData.getCredits()) {

                                Activity activity = bot.getActivities().get(event.getAuthor().getId() + event.getChannel().getId());
                                if (activity != null) {
                                    embed.setDescription("You are already playing a minigame in this channel!" +
                                            "\nCurrent game: " + activity.getMessage().getJumpUrl());
                                    embed.setColor(ColorScheme.ERROR);
                                    channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();

                                } else startGame(event, bet);
                            } else {
                                embed.setDescription("You don't have enough credits!" +
                                        "\nYou currently have " + playerData.getCredits() + " credits.");
                                embed.setColor(ColorScheme.ERROR);
                                channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                            }

                        } catch (Exception e) {
                            embed.setDescription(args.get(0) + " is not a number!");
                            embed.setColor(ColorScheme.ERROR);
                            channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                        }
                    } else {
                        embed.setDescription("The game will have no bet!");
                        embed.setColor(new Color(255, 150, 0));
                        channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                        startGame(event, 0);
                    }
                });

        getCommandCenter().addCommand("profile", "Views your blackjack profile.",
                (event, args) -> {
                    handleData(event);
                    EmbedBuilder embed = new EmbedBuilder();
                    MessageChannel channel = event.getChannel();

                    embed.setTitle("Profile:");
                    embed.setThumbnail(event.getAuthor().getAvatarUrl());
                    embed.setDescription("<@" + playerData.getPlayerID() + ">" +
                            "\nCredits: " + playerData.getCredits() +
                            "\nWins :" + playerData.getWins() +
                            "\nLosses :" + playerData.getLosses());


                    embed.setColor(new Color(0, 255, 150));
                    channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                });

        getCommandCenter().addCommand("leaderboard", "Views a leaderboard of the top 5 users on the server.",
                (event, args) -> {
                    handleData(event);
                    MessageChannel channel = event.getChannel();
                    EmbedBuilder embed = new EmbedBuilder();

                    activityData.updateLeaderBoard();

                    HashMap<Integer, CasinoPlayerData> leaderBoard = activityData.getLeaderBoard();

                    if (leaderBoard != null) {
                        ArrayList<Integer> list = activityData.getPositions();

                        list.sort(Comparator.reverseOrder());

                        String text = "";

                        int range = 5;
                        if (list.size() < 5) {
                            range = list.size();
                        }

                        for (int i = 0; i < range; i++) {
                            text = (text + (i + 1) + ": " + "<@" + leaderBoard.get(list.get(i)).getPlayerID() + "> " + leaderBoard.get(list.get(i)).getCredits() + " credits" + "\n");
                        }

                        if (list.size() > 0) {
                            embed.setTitle("Top " + range);
                            embed.setDescription(text);
                            embed.setColor(ColorScheme.RESPONSE);
                            channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                        } else {
                            embed.setDescription("No one has played blackjack in this server before!");
                            embed.setColor(ColorScheme.ERROR);
                            channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                        }
                    } else {
                        embed.setDescription("No one has played blackjack in this server before!");
                        embed.setColor(ColorScheme.ERROR);
                        channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                    }
                });

        getCommandCenter().addCommand("addcredits", "***Admin only command.*** Adds credits to the tagged user.",
                (event, args) -> {
                    handleData(event);
                    MessageChannel channel = event.getChannel();

                    if (bot.getAdmins().contains(event.getAuthor().getId())) {

                        System.out.println(args);
                        try {
                            String playerID = args.get(0);
                            playerID = playerID.substring(3, playerID.length() - 1);
                            int amount = Integer.parseInt(args.get(1));

                            CasinoPlayerData player = ((CasinoData) bot.getGuildData().get(event.getGuild().getId())
                                    .getActivityData().get("BLACKJACK")).getPlayers().get(playerID);
                            player.setCredits(player.getCredits() + amount);
                        } catch (Exception e) {
                            channel.sendMessage("poo").queue();
                            e.printStackTrace();
                        }
                    } else {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setDescription("You are not an admin / mod!");
                        embed.setColor(ColorScheme.ERROR);
                        channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                    }
                });
    }

    @Override
    public void buildSynonyms() {
        getCommandCenter().addSynonym("p","play");
        getCommandCenter().addSynonym("pf","profile");
        getCommandCenter().addSynonym("lb","leaderboard");
        getCommandCenter().addSynonym("add","addcredits");
    }

    private void handleData(MessageReceivedEvent event) {
        BlackJackCommands command = new BlackJackCommands();
        HashMap<String, String> commands = command.getCommands();
        MessageChannel channel = event.getChannel();
        EmbedBuilder embed = new EmbedBuilder();

        activityData = (CasinoData) bot.getGuildData().get(event.getGuild().getId()).getActivityData().get("BLACKJACK");
        if ((activityData) == null) {
            activityData = new CasinoData();
            bot.getGuildData().get(event.getGuild().getId()).addActivityData(activityData);
        }

        playerData = activityData.getPlayers().get(event.getAuthor().getId());
        if ((playerData) == null) {
            playerData = new CasinoPlayerData(event.getAuthor().getId());
            activityData.getPlayers().put(playerData.getPlayerID(), playerData);
        }

        boolean autoClaimed = playerData.incrementLoop();

        if (autoClaimed) {
            EmbedBuilder claim = new EmbedBuilder();
            claim.setTitle("Credits");
            claim.setDescription("You automatically earned 100 credits!" +
                    "\nYou are now at " + playerData.getCredits());
            claim.setColor(ColorScheme.RESPONSE);
            channel.sendMessageEmbeds(claim.build()).reference(event.getMessage()).queue();
        }
    }

    private void startGame(MessageReceivedEvent event, int bet) {
        MessageChannel channel = event.getChannel();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("The game will be starting soon!");
        embed.setColor(new Color(255, 255, 150));
        channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue(newMessage -> {
            bot.addActivity(new Game(event, bot, newMessage, bet));
        });

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

