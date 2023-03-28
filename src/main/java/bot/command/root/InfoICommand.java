package bot.command.root;

import bot.command.ICommand;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InfoICommand implements ICommand {
    @Override
    public void run(Event event, List<String> args) {

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Info:");
        embed.setDescription("A bot programmed by <@195929905857429504> using Java 11.0.15 and JDA 5.0.0-beta.6 \n" +
                "Last updated: 28/03/2023 \n");
        embed.setColor(ColorScheme.RESPONSE);

        if (event instanceof MessageReceivedEvent){
            ((MessageReceivedEvent) event).getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        if (event instanceof SlashCommandInteractionEvent){
            ((SlashCommandInteractionEvent) event).replyEmbeds(embed.build()).queue();
        }
    }

    @Override
    public @NotNull String getCommandWord() {
        return "info";
    }

    @Override
    public @NotNull String getDescription() {
        return "Shows info about the bot";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
