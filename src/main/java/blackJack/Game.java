package blackJack;

import Base.Activity;
import Base.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;

public class Game extends Activity {
    private MessageChannel channel;
    private ArrayList<Player> players;
    private Bot bot;
    private Dealer dealer;
    private String messageID;
    private EmbedBuilder embed;

    public Game(MessageReceivedEvent event, Bot bot){
        super(event);
        //Make this read from database or some other method instead of hard coding
        this.dealer = new Dealer("926821629324046407");
        this.bot = bot;
        this.embed = new EmbedBuilder();
        this.channel = event.getChannel();
        this.messageID = null;


        EmbedBuilder tempEmbed = new EmbedBuilder();
        tempEmbed.setDescription("Reply to the game message to interact with the game!" +
                "\nThis will be temporary ");
        tempEmbed.setColor(new Color(255, 255, 150));
        channel.sendMessageEmbeds(tempEmbed.build()).queue();

        embed.setTitle("Blackjack");
        embed.setColor(new Color(100,255, 255));
        channel.sendMessageEmbeds(embed.build()).queue();
    }

    public Game(MessageReceivedEvent event, Bot bot, String messageID){
        super(event);
        this.dealer = new Dealer(bot.getUserID());
        this.bot = bot;
        this.embed = new EmbedBuilder();
        this.channel = event.getChannel();
        this.messageID = messageID;

        embed.setTitle("Blackjack");
        embed.setColor(new Color(100,255, 255));
        channel.editMessageEmbedsById(messageID, embed.build()).queue();
    }

    public void edit(String newText){
        embed.setDescription(newText);
        channel.editMessageEmbedsById(messageID, embed.build()).queue();
    }

    public void addPlayer(String playerID){
        players.add(new Player(playerID));
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
}
