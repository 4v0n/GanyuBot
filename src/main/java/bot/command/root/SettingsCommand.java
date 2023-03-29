package bot.command.root;

import bot.command.Command;
import bot.feature.root.BaseCommandHandler;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SettingsCommand implements Command {

    private final CommandDataImpl commandData;

    public SettingsCommand() {
        this.commandData = new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public void run(Event event, List<String> args) {

        if (event instanceof MessageReceivedEvent){
            Member member = ((MessageReceivedEvent) event).getMember();

            boolean hasPermission = member.hasPermission(Permission.ADMINISTRATOR);

            if (!hasPermission) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Insufficient permissions!");
                embed.setDescription("You need to have the `Administrator` permission to use this set of commands");
                embed.setColor(ColorScheme.ERROR);
                ((MessageReceivedEvent) event).getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }
        }

        if (event instanceof SlashCommandInteractionEvent) {
            Member member = ((SlashCommandInteractionEvent) event).getMember();

            boolean hasPermission = member.hasPermission(Permission.ADMINISTRATOR);

            if (!hasPermission) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Insufficient permissions!");
                embed.setDescription("You need to have the `Administrator` permission to use this set of commands");
                embed.setColor(ColorScheme.ERROR);
                ((SlashCommandInteractionEvent) event).replyEmbeds(embed.build()).queue();
                return;
            }
        }

        BaseCommandHandler.getINSTANCE().getChildren().get(getCommandWord()).parse(event);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "settings";
    }

    @Override
    public @NotNull String getDescription() {
        return "Allows to access the bot settings";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
