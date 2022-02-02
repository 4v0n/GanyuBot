package blackJack;


import Base.Activity;
import Base.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.*;

public class BlackJackHandler{
    private Bot bot;

    public BlackJackHandler(Bot bot) {
        this.bot = bot;
    }

    public void parse(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String content = message.getContentRaw();
        ArrayList<String> words = splitString(content);


        //remove the prefix and old command word and get the new command word
        String commandWord = null;
        words.remove(0);
        words.remove(0);
        if (words.size() > 0) {
            commandWord = words.get(0);
        }

        BlackJackCommands command = new BlackJackCommands();
        HashMap<String, String> commands = command.getCommands();
        MessageChannel channel = event.getChannel();
        EmbedBuilder embed = new EmbedBuilder();

        BlackJackData activityData = (BlackJackData) bot.getGuildData().get(event.getGuild().getId()).getActivityData().get("BLACKJACK");
        if ((activityData) == null){
            activityData = new BlackJackData();
            bot.getGuildData().get(event.getGuild().getId()).addActivityData(activityData);
        }

        BlackjackPlayerData playerData = activityData.getPlayers().get(event.getAuthor().getId());
        if ((playerData) == null){
            playerData = new BlackjackPlayerData(event.getAuthor().getId());
            activityData.getPlayers().put(playerData.getPlayerID(), playerData);
        }

        boolean autoClaimed = playerData.incrementLoop();

        if (autoClaimed){
            EmbedBuilder claim = new EmbedBuilder();
            claim.setTitle("Credits");
            claim.setDescription("You automatically earned 100 credits!" +
                    "\nYou are now at " + playerData.getCredits());
            claim.setColor(new Color(0, 255, 150));
            channel.sendMessageEmbeds(claim.build()).reference(event.getMessage()).queue();
        }


        if (commandWord != null) {
            // if there is a commandWord
            String tags;
            switch (commandWord) {
                case "play":
                    words.remove(0);
                    if (!words.isEmpty()) {
                        try {
                            int bet = Integer.parseInt(words.get(0));
                            if (bet <= playerData.getCredits()) {

                                Activity activity = bot.getActivities().get(event.getAuthor().getId() + event.getChannel().getId());
                                if (activity != null) {
                                    embed.setDescription("You are already playing a minigame in this channel!" +
                                            "\nCurrent game: " + activity.getMessage().getJumpUrl());
                                    embed.setColor(new Color(255, 0, 0));
                                    channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();

                                } else startGame(event, bet);
                            } else {
                                embed.setDescription("You don't have enough credits!" +
                                        "\nYou currently have " + playerData.getCredits() + " credits.");
                                embed.setColor(new Color(255, 0, 0));
                                channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                            }

                        } catch (Exception e){
                            embed.setDescription(words.get(0) + " is not a number!");
                            embed.setColor(new Color(255, 0, 0));
                            channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                        }
                    } else {
                        embed.setDescription("You haven't placed a bet!" +
                                "\nYou may find your balance using the `profile` command.");
                        embed.setColor(new Color(255, 0, 0));
                        channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                    }
                    break;

                case "help":
                    BlackJackHelp help = new BlackJackHelp();
                    help.help(bot.getPrefix(), event);
                    break;

                case "profile":
                    embed.setTitle("Profile:");
                    embed.setThumbnail(event.getAuthor().getAvatarUrl());
                    embed.setDescription("<@" + playerData.getPlayerID() + ">" +
                            "\nCredits: " + playerData.getCredits() +
                            "\nWins :" + playerData.getWins() +
                            "\nLosses :" + playerData.getLosses());


                    embed.setColor(new Color(0, 255, 150));
                    channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                    break;

                case "leaderboard":
                    activityData.updateLeaderBoard();

                    HashMap<Integer, BlackjackPlayerData> leaderBoard = activityData.getLeaderBoard();

                    if(leaderBoard != null) {
                        ArrayList<Integer> list = activityData.getPositions();

                        list.sort(Comparator.reverseOrder());

                        String text = "";

                        int range = 5;
                        if (list.size() < 5) {
                            range = list.size();
                        }

                        for (int i = 0; i < range; i++) {
                            text = (text + (i + 1) + ": " + "<@" + leaderBoard.get(list.get(i)).getPlayerID()+ "> " + leaderBoard.get(list.get(i)).getCredits() + " credits" + "\n");
                        }

                        if (list.size() > 0) {
                            embed.setTitle("Top " + range);
                            embed.setDescription(text);
                            embed.setColor(new Color(0, 255, 150));
                            channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                        } else {
                            embed.setDescription("No one has played blackjack in this server before!");
                            embed.setColor(new Color(255, 0, 0));
                            channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                        }
                    } else {
                        embed.setDescription("No one has played blackjack in this server before!");
                        embed.setColor(new Color(255, 0, 0));
                        channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                    }

                    break;

                default:
                    embed.setDescription("There is no ' " + commandWord + "' command!" +
                            "\nUse the 'help' command to get a list of usable commands." +
                            "\nAll commands are also lower case.");
                    embed.setColor(new Color(255, 0, 0));
                    channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
            }
        } else {
            embed.setDescription("You haven't provided a BlackJack command!" +
                    "\nUse the 'help' command to get a list of usable commands." +
                    "\nAll commands are also lower case.");
            embed.setColor(new Color(255, 0, 0));

            channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
        }
    }

    private void startGame(MessageReceivedEvent event, int bet){
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

