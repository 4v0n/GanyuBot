package Base;

import CommandStructure.CommandHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents any playable activity in the bot.
 * This class is solely to be extended.
 */
public abstract class Activity implements Serializable {
    private final MessageChannel channel;
    private final String userID;
    private final CommandHandler parser;
    private final Message message;
    private ArrayList<String> commandList;
    private final Bot bot;
    private boolean isActive;

    /**
     * Constructor class
     * @param event The event that started the activity.
     * @param parser The parser that handles the commands for the respective activity.
     * @param message The message that will be edited during the activity.
     * @param bot The bot data class.
     */
    public Activity(MessageReceivedEvent event, CommandHandler parser, Message message, Bot bot){
        this.channel = event.getChannel();
        this.userID = event.getAuthor().getId();
        this.message = message;
        this.bot = bot;
        //System.out.println(channel);
        //System.out.println(userID);
        this.parser = parser;
        this.isActive = true;
    }

    private void method(){

    }

    /**
     * @return Returns the message to be edited.
     */
    public Message getMessage() {
        return message;
    }

    /**
     *
     * @return The command parser for the activity.
     */
    public CommandHandler getParser() {
        return parser;
    }

    /**
     * This processes a command accepted by the bot.
     * @param event The event that is to be parsed.
     * @param bot The bot data.
     */
    public void parse(MessageReceivedEvent event, Bot bot){
        parser.parse(event);
    }

    /**
     *
     * @return The user ID of the participant of the activity.
     */
    public String getUserID() {
        return userID;
    }

    /**
     *
     * @return The channel that the activity is taking place in.
     */
    public MessageChannel getChannel(){
        return channel;
    }

    /**
     *
     * @return The bot data object.
     */
    public Bot getBot() {
        return bot;
    }

    /**
     * Template method
     * @param event
     * @param bot
     * @param commandWord
     */
    public void parse(MessageReceivedEvent event, Bot bot, String commandWord) {
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean state){
        isActive = state;
    }
}
