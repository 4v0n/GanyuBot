package bot.command.root;

import bot.command.Command;
import bot.command.CommandContext;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CopyCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        Event event = context.getEvent();

        if (event instanceof MessageReceivedEvent) {
            Message message = ((MessageReceivedEvent) event).getMessage();
            String content = message.getContentRaw();

            MessageChannel channel = ((MessageReceivedEvent) event).getChannel();
            if (content.length() > 8) {
                content = content.substring(8);
                channel.sendMessage(content).queue();
            } else {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("You haven't typed anything to copy!");
                embed.setColor(ColorScheme.ERROR);
                channel.sendMessageEmbeds(embed.build()).queue();
            }
            return;
        }

        if (event instanceof SlashCommandInteractionEvent){
            OptionMapping optionMapping = ((SlashCommandInteractionEvent) event).getOption("text");

            if (optionMapping == null){
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("You haven't typed anything to copy!");
                embed.setColor(ColorScheme.ERROR);
                ((SlashCommandInteractionEvent) event).replyEmbeds(embed.build()).queue();
                return;
            }

            String content = optionMapping.getAsString();

            ((SlashCommandInteractionEvent) event).reply(content).queue();
        }
    }

    @Override
    public @NotNull String getCommandWord() {
        return "copy";
    }

    @Override
    public @NotNull String getDescription() {
        return "Copies what you type";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl(getCommandWord(), getDescription());
        commandData.addOption(OptionType.STRING, "text", "The text to be copied", true);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
