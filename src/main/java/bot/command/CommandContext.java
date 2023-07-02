package bot.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandContext {

    private final Event event;
    private final Guild guild;
    private final Member member;
    private final MessageChannel messageChannel;
    private final Member selfMember;


    public CommandContext(SlashCommandInteractionEvent event) {
        this.event = event;
        this.guild = event.getGuild();
        this.member = event.getMember();
        this.messageChannel = event.getMessageChannel();
        this.selfMember = event.getGuild().getSelfMember();
    }

    public CommandContext(MessageReceivedEvent event) {
        this.event = event;
        this.guild = event.getGuild();
        this.member = event.getMember();
        this.messageChannel = event.getChannel().asGuildMessageChannel();
        this.selfMember = event.getGuild().getSelfMember();
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
}
