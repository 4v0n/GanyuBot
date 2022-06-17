package ganyu.base.commands;

import ganyu.base.BaseCommandHandler;
import ganyu.base.ColorScheme;
import ganyu.command.message.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SettingsCommand implements Command {

    private final CommandData commandData;

    public SettingsCommand() {
        this.commandData = new CommandData(getCommandWord(), getDescription());
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

        if (event instanceof SlashCommandEvent) {
            Member member = ((SlashCommandEvent) event).getMember();

            boolean hasPermission = member.hasPermission(Permission.ADMINISTRATOR);

            if (!hasPermission) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Insufficient permissions!");
                embed.setDescription("You need to have the `Administrator` permission to use this set of commands");
                embed.setColor(ColorScheme.ERROR);
                ((SlashCommandEvent) event).replyEmbeds(embed.build()).queue();
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
    public @NotNull CommandData getCommandData() {
        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
