package bot.command;

import bot.util.message.MultiPageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class CommandContext {

    private final Event event;
    private final Guild guild;
    private final User author;
    private final Member member;
    private final MessageChannel messageChannel;
    private final Member selfMember;

    private boolean responded;


    public CommandContext(SlashCommandInteractionEvent event) {
        this.event = event;
        this.guild = event.getGuild();
        this.member = event.getMember();
        this.messageChannel = event.getMessageChannel();
        this.selfMember = event.getGuild().getSelfMember();
        this.author = event.getUser();
        this.responded = false;

        event.deferReply().queue();
    }

    public CommandContext(MessageReceivedEvent event) {
        this.event = event;
        this.guild = event.getGuild();
        this.member = event.getMember();
        this.messageChannel = event.getChannel().asGuildMessageChannel();
        this.selfMember = event.getGuild().getSelfMember();
        this.author = event.getAuthor();
        this.responded = false;
    }

    public Event getEvent() {
        return event;
    }

    public Guild getGuild() {
        return guild;
    }

    public Member getMember() {
        return member;
    }

    public MessageChannel getMessageChannel() {
        return messageChannel;
    }

    public Member getSelfMember() {
        return selfMember;
    }

    public User getAuthor() {
        return author;
    }

    public void setResponded() {
        this.responded = true;
    }

    public boolean isResponded() {
        return responded;
    }

    public void respondMessage(String text) {
        if (responded) {
            throw new RuntimeException("Command responded to multiple times");
        }

        responded = true;
        if (event instanceof SlashCommandInteractionEvent) {
            SlashCommandInteractionEvent evt = (SlashCommandInteractionEvent) event;
            evt.getHook().sendMessage(text).queue();
        } else {
            messageChannel.sendMessage(text).queue();
        }
    }

    public void respondEmbed(EmbedBuilder embed) {
        if (responded) {
            throw new RuntimeException("Command responded to multiple times");
        }

        responded = true;
        if (event instanceof SlashCommandInteractionEvent) {
            SlashCommandInteractionEvent evt = (SlashCommandInteractionEvent) event;
            evt.getHook().sendMessageEmbeds(embed.build()).queue();
        } else {
            messageChannel.sendMessageEmbeds(embed.build()).queue();
        }
    }

    public void sendMPE(MultiPageEmbed mpe) {
        mpe.sendMessage(messageChannel);
    }

    public void respondMPE(MultiPageEmbed mpe) {
        if (responded) {
            throw new RuntimeException("Command responded to multiple times");
        }

        mpe.respond(this);
    }
}
