package bot.activity;

import bot.feature.message.CommandHandler;
import bot.Bot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.Serializable;

/**
 * Represents any playable activity in the bot.
 * This class is solely to be extended.
 *
 * @author Aron Navodh Kumarawatta
 * @version 29.05.2022
 */
public abstract class Activity implements Serializable {
    private final MessageChannel channel;
    private final String userID;
    private final CommandHandler parser;
    private final Message message;
    private final Bot bot;
    private boolean isActive;

    /**
     * Constructor class
     *
     * @param event   The event that started the activity.
     * @param parser  The parser that handles the commands for the respective activity.
     * @param message The message that will be edited during the activity.
     */
    public Activity(MessageReceivedEvent event, CommandHandler parser, Message message) {
        this.channel = event.getChannel();
        this.userID = event.getAuthor().getId();
        this.message = message;
        this.bot = Bot.getINSTANCE();
        this.parser = parser;
        this.isActive = true;
    }

    public Activity(SlashCommandEvent event, CommandHandler parser, Message message) {
        this.channel = event.getChannel();
        this.userID = event.getUser().getId();
        this.message = message;
        this.bot = Bot.getINSTANCE();
        this.parser = parser;
        this.isActive = true;
    }

    /**
     * @return Returns the message to be edited.
     */
    public Message getMessage() {
        return message;
    }

    /**
     * @return The command parser for the activity.
     */
    public CommandHandler getParser() {
        return parser;
    }

    /**
     * This processes a command accepted by the bot.
     *
     * @param event The event that is to be parsed.
     */
    public void parse(MessageReceivedEvent event) {
        parser.parse(event);
    }

    /**
     * @return The user ID of the participant of the activity.
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @return The channel that the activity is taking place in.
     */
    public MessageChannel getChannel() {
        return channel;
    }

    /**
     * @return The bot data object.
     */
    public Bot getBot() {
        return bot;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean state) {
        isActive = state;
    }
}
