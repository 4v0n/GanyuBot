package blackJack;

import Base.Activity;
import Base.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

public class Game extends Activity {
    private Deck deck;
    private MessageChannel channel;
    private Player player;
    private Bot bot;
    private Dealer dealer;
    private String messageID;
    private EmbedBuilder embed;
    private String embedFooter = "";
    private String embedDescription = "";
    private boolean isActive;

    public Game(MessageReceivedEvent event, Bot bot, String messageID){
        super(event, new BlackJackParser(bot));
        this.dealer = new Dealer(bot.getUserID());
        this.bot = bot;
        this.embed = new EmbedBuilder();
        this.channel = event.getChannel();
        this.messageID = messageID;

        this.player = new Player(event.getAuthor().getId(), event.getAuthor());
        this.deck = new Deck();
        this.player.addCard(deck.dealCard());
        this.dealer.addCard(deck.dealCard());
        this.player.addCard(deck.dealCard());
        this.dealer.addCard(deck.dealCard());
        this.dealer.turn(this);

        this.embed.setTitle("Blackjack");
        this.embed.setColor(new Color(100,255, 255));
        addToEmbedDescription(player.getValueOfHand() + "\n" + this.player.showCards());
        addToFooter("Here is a list of commands:" +
                "\n '>g hit' Deals you another card" +
                "\n '>g stand' Finishes your turn");
        channel.editMessageEmbedsById(messageID, this.embed.build()).queue();

        this.isActive = true;
    }

    public void edit(String newText){
        embed.setDescription(newText);
        channel.editMessageEmbedsById(messageID, embed.build()).queue();
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessageID(){
        return messageID;
    }
    public MessageChannel getChannel(){
        return channel;
    }
    public EmbedBuilder getEmbed(){
        return embed;
    }
    public Deck getDeck() {
        return deck;
    }

    public Player getPlayer() {
        return player;
    }

    public void addToEmbedDescription(String text){
        embedDescription = embedDescription + text;
        embed.setDescription(embedDescription);
    }
    public void addToFooter(String text){
        embedFooter = embedFooter + text;
        embed.setFooter(embedFooter);
    }

    public boolean isActive() {
        return isActive;
    }

    private void checkGameState(Player player){
        if (player.getValueOfHand() > 21) {
            isActive = false;

        } else {
            isActive = true;
        }
    }

    private void evaluatePlayer(Player player){
        if (player.getValueOfHand() > 21) {
            player.setLost(true);

        }
        if (player.getValueOfHand() == 21){
            player.setLost(false);
        }
    }

    private void comparePlayers(Player player1, Player player2){
        if (player1.hasLost() == player2.hasLost()) {
            if (player1.distanceTo21() > player2.distanceTo21()) {
                player1.setLost(true);
            } else if (player1.distanceTo21() < player2.distanceTo21()) {
                player2.setLost(true);
            }
        }
    }

    private void getAndShowWinner(){
        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("Winner:");

        evaluatePlayer(player);
        evaluatePlayer(dealer);
        comparePlayers(player, dealer);

        if (!player.hasLost() && dealer.hasLost()){
            e.setThumbnail(player.getUser().getAvatarUrl());
            e.setDescription(player.getDiscordAt() + "\nYou have won!");
            e.setColor(new Color(50,255, 150));
        } else if (player.hasLost() == dealer.hasLost()) {
            e.setDescription("You and the dealer have drawn");
            e.setColor(new Color(255, 150, 100));
        } else {
            e.setThumbnail(bot.getPfpURL());
            e.setDescription("The dealer has won!");
            e.setColor(new Color(255, 100, 100));
        }
        channel.sendMessageEmbeds(e.build()).queue();
    }

    public void finish(){
        channel.sendMessage(player.getDiscordAt()).queue();
        EmbedBuilder newEmbed = new EmbedBuilder();
        newEmbed.setTitle("Dealer:");
        newEmbed.setColor(new Color(200,255, 155));
        newEmbed.setDescription(dealer.getValueOfHand() + "\n" + dealer.showCards());
        channel.sendMessageEmbeds(newEmbed.build()).queue();
        getAndShowWinner();
        bot.removeActivity(this);
    }

    public void update() {
        embed.setDescription(player.getValueOfHand() + "\n" + player.showCards());
        channel.editMessageEmbedsById(messageID, this.embed.build()).queue();
        checkGameState(player);
        if (!isActive){
            finish();
        }
    }
}
