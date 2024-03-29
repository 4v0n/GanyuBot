package bot.activity.blackjack;

import bot.Bot;
import bot.activity.Activity;
import bot.db.legacy.blackjack.CasinoData;
import bot.db.legacy.blackjack.CasinoGuildData;
import bot.db.legacy.blackjack.UserData;
import bot.feature.blackjack.BlackJackIngameCommandParser;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

/**
 * This class allows for a game of blackjack to be played
 *
 * @author Aron Navodh Kumarawatta
 * @version 28.03.2023
 */
public class Game extends Activity {
    private final Deck deck;
    private final MessageChannel channel;
    private final Player player;
    private final UserData playerData;
    private final Dealer dealer;
    private final long bet;
    private final String messageID;
    private final EmbedBuilder embed;
    private final Member selfMember;
    private String embedFooter = "";
    private String embedDescription = "";
    private final CasinoGuildData activityData;

    /**
     * Creates a game class
     *
     * @param event   The event that starts the game
     * @param message The parent message of the game
     * @param bet     The amount of credits that has been wagered
     */
    public Game(MessageReceivedEvent event, Message message, long bet) {
        super(event, new BlackJackIngameCommandParser(), message);
        this.dealer = new Dealer(event.getGuild().getSelfMember().getId());
        this.embed = new EmbedBuilder();
        this.channel = event.getChannel();
        this.messageID = message.getId();
        this.bet = bet;

        this.activityData = CasinoData.getInstance().getGuildData(event.getGuild());

        this.player = new Player(event.getAuthor().getId(), event.getAuthor());
        this.deck = new Deck();
        this.player.addCard(deck);
        this.dealer.addCard(deck);
        this.player.addCard(deck);
        this.dealer.addCard(deck);
        this.selfMember = event.getGuild().getSelfMember();

        this.playerData = activityData.getPlayer(event.getMember());
        this.playerData.setCredits(this.playerData.getCredits() - bet);

        this.embed.setTitle("Blackjack");
        this.embed.setColor(ColorScheme.ACTIVITY);
        addToEmbedDescription(player.getValueOfHand() + "\n" + this.player.showCards());


        addToFooter("You have bet " + bet + " credits." +
                "\nHere is a list of commands:" +
                "\n '" + Bot.getInstance().getPrefix(event.getGuild()) + " hit' Deals you another card" +
                "\n '" + Bot.getInstance().getPrefix(event.getGuild()) + " stand' Finishes your turn");
        channel.editMessageEmbedsById(messageID, this.embed.build()).queue();
    }

    public Game(SlashCommandInteractionEvent event, Message message, int bet) {
        super(event, new BlackJackIngameCommandParser(), message);

        this.dealer = new Dealer(event.getGuild().getSelfMember().getId());
        this.embed = new EmbedBuilder();
        this.channel = event.getChannel();
        this.messageID = message.getId();
        this.bet = bet;


        this.activityData = CasinoData.getInstance().getGuildData(event.getGuild());

        this.player = new Player(event.getUser().getId(), event.getUser());
        this.deck = new Deck();
        this.player.addCard(deck);
        this.dealer.addCard(deck);
        this.player.addCard(deck);
        this.dealer.addCard(deck);
        this.selfMember = event.getGuild().getSelfMember();


        this.playerData = activityData.getPlayer(event.getMember());

        this.playerData.setCredits(this.playerData.getCredits() - bet);

        this.embed.setTitle("Blackjack");
        this.embed.setColor(ColorScheme.ACTIVITY);
        addToEmbedDescription(player.getValueOfHand() + "\n" + this.player.showCards());


        addToFooter("You have bet " + bet + " credits." +
                "\nHere is a list of commands:" +
                "\n '" + Bot.getInstance().getPrefix(event.getGuild()) + " hit' Deals you another card" +
                "\n '" + Bot.getInstance().getPrefix(event.getGuild()) + " stand' Finishes your turn");
        channel.editMessageEmbedsById(messageID, this.embed.build()).queue();
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
        setActive(player.getValueOfHand() <= 21);
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
            playerData.setCredits(playerData.getCredits() + (bet * 2));
            e.setColor(ColorScheme.ACTIVITY_WIN);

            e.setDescription(player.getDiscordAt() + "\nYou have won!" +
                    "\nYou win " + bet * 2 + " credits!" +
                    "\nYou are at " + playerData.getCredits() + " credits.");
            playerData.incrementWins();

        } else if (player.hasLost() == dealer.hasLost()) {
            playerData.setCredits(playerData.getCredits() + bet);
            e.setDescription("You and the dealer have drawn." +
                    "\nYou have not lost any credits.");
            e.setColor(ColorScheme.INFO);

        } else {
            String avatarUrl = selfMember.getEffectiveAvatarUrl();

            e.setThumbnail(avatarUrl);
            e.setColor(ColorScheme.ACTIVITY_LOSS);
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

        try {
            activityData.save();

        } catch (Exception ignored) {
            // ignore
        }
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
        newEmbed.setColor(ColorScheme.INFO);
        newEmbed.setDescription(dealer.getValueOfHand() + "\n" + dealer.showCards());

        channel.sendMessageEmbeds(newEmbed.build()).queue(message -> dealer.turn(this, message, newEmbed));
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
