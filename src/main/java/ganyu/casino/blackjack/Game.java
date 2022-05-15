package ganyu.casino.blackjack;

import ganyu.base.Activity;
import ganyu.base.Bot;
import ganyu.casino.CasinoData;
import ganyu.casino.CasinoPlayerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * This class allows for a game of blackjack to be played
 *
 * @author Aron Navodh Kumarawatta
 * @version 15.05.2022
 */
public class Game extends Activity {
    private final Deck deck;
    private final MessageChannel channel;
    private final Player player;
    private CasinoPlayerData playerData;
    private final Dealer dealer;
    private final int bet;
    private String messageID;
    private final EmbedBuilder embed;
    private String embedFooter = "";
    private String embedDescription = "";
    private CasinoData activityData;

    /**
     * Creates a game class
     *
     * @param event   The event that starts the game
     * @param bot     bot data
     * @param message The parent message of the game
     * @param bet     The amount of credits that has been wagered
     */
    public Game(MessageReceivedEvent event, Bot bot, Message message, int bet) {
        super(event, new BlackJackParser(), message, bot);
        this.dealer = new Dealer(bot.getUserID());
        this.embed = new EmbedBuilder();
        this.channel = event.getChannel();
        this.messageID = message.getId();
        this.bet = bet;


        this.activityData = (CasinoData) getBot().getGuildData().get(event.getGuild().getId()).getActivityData().get("BLACKJACK");
        if ((this.activityData) == null) {
            this.activityData = new CasinoData();
            getBot().getGuildData().get(event.getGuild().getId()).addActivityData(this.activityData);
        }

        this.player = new Player(event.getAuthor().getId(), event.getAuthor());
        this.deck = new Deck();
        this.player.addCard(deck);
        this.dealer.addCard(deck);
        this.player.addCard(deck);
        this.dealer.addCard(deck);


        this.playerData = activityData.getPlayers().get(player.getUser().getId());
        if ((this.playerData) == null) {
            this.playerData = new CasinoPlayerData(player.getUser().getId());
            this.activityData.getPlayers().put(playerData.getPlayerID(), playerData);
        }

        this.playerData.setCredits(this.playerData.getCredits() - bet);

        this.embed.setTitle("Blackjack");
        this.embed.setColor(new Color(100, 255, 255));
        addToEmbedDescription(player.getValueOfHand() + "\n" + this.player.showCards());
        addToFooter("You have bet " + bet + " credits." +
                "\nHere is a list of commands:" +
                "\n '" + bot.getPrefix() + " hit' Deals you another card" +
                "\n '" + bot.getPrefix() + " stand' Finishes your turn");
        channel.editMessageEmbedsById(messageID, this.embed.build()).queue();
    }

    public void edit(String newText) {
        embed.setDescription(newText);
        channel.editMessageEmbedsById(messageID, embed.build()).queue();
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessageID() {
        return messageID;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public EmbedBuilder getEmbed() {
        return embed;
    }

    public Deck getDeck() {
        return deck;
    }

    public Player getPlayer() {
        return player;
    }

    public void addToEmbedDescription(String text) {
        embedDescription = embedDescription + text;
        embed.setDescription(embedDescription);
    }

    public void addToFooter(String text) {
        embedFooter = embedFooter + text;
        embed.setFooter(embedFooter);
    }

    private void checkGameState(Player player) {
        if (player.getValueOfHand() > 21) {
            setActive(false);

        } else {
            setActive(true);
        }
    }

    private void evaluatePlayer(Player player) {
        if (player.getValueOfHand() > 21) {
            player.setLost(true);
        }
    }

    private void comparePlayers(Player player1, Player player2) {
        if (player1.hasLost() == player2.hasLost()) {
            if (player1.distanceTo21() > player2.distanceTo21()) {
                player1.setLost(true);
            } else if (player1.distanceTo21() < player2.distanceTo21()) {
                player2.setLost(true);
            }
        }
    }

    protected void getAndShowWinner() {
        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("Winner:");

        evaluatePlayer(player);
        evaluatePlayer(dealer);
        comparePlayers(player, dealer);
        resolveDraw(player, dealer);

        if (!player.hasLost() && dealer.hasLost()) {
            e.setThumbnail(player.getUser().getAvatarUrl());
            playerData.setCredits(playerData.getCredits() + bet * 2);
            e.setColor(new Color(50, 255, 150));

            e.setDescription(player.getDiscordAt() + "\nYou have won!" +
                    "\nYou win " + bet * 2 + " credits!" +
                    "\nYou are at " + playerData.getCredits() + " credits.");
            playerData.incrementWins();

        } else if (player.hasLost() == dealer.hasLost()) {
            e.setDescription("You and the dealer have drawn." +
                    "\nYou have not lost any credits.");
            e.setColor(new Color(255, 150, 100));

        } else {
            e.setThumbnail(getBot().getPfpURL());
            e.setColor(new Color(255, 100, 100));
            playerData.setCredits(playerData.getCredits() - bet);
            e.setDescription("The dealer has won!" +
                    "\nYou have lost " + bet + " credits!" +
                    "\nYou are at " + playerData.getCredits() + " credits.");
            playerData.incrementLosses();

        }

        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        channel.sendMessageEmbeds(e.build()).queue();
    }

    private void resolveDraw(Player player1, Player player2) {
        if (player1.hasLost() == player2.hasLost()) {
            if (player1.getHand().size() > player2.getHand().size()) {
                player1.setLost(true);
            } else if (player1.getHand().size() < player2.getHand().size()) {
                player2.setLost(true);
            }
        }
    }

    public void finish() {
        channel.sendMessage(player.getDiscordAt()).queue();
        EmbedBuilder newEmbed = new EmbedBuilder();
        newEmbed.setTitle("Dealer:");
        newEmbed.setColor(new Color(200, 255, 155));
        ArrayList<Card> cards = dealer.getHand();
        newEmbed.setDescription(dealer.getValueOfHand() + "\n" + dealer.showCards());

        channel.sendMessageEmbeds(newEmbed.build()).queue(message -> {
            dealer.turn(this, message, newEmbed);
        });
    }

    public void update() {
        embed.setDescription(player.getValueOfHand() + "\n" + player.showCards());
        channel.editMessageEmbedsById(messageID, this.embed.build()).queue();
        checkGameState(player);
        if (!isActive()) {
            finish();
        }
    }
}
