package bot.command.settings;

import bot.command.ICommand;
import bot.Bot;
import bot.util.ColorScheme;
import bot.db.server.ServerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static bot.feature.message.CommandMethods.sendEmbed;
import static bot.feature.message.CommandMethods.sendEphemeralEmbed;

public class ChangeDJRoleICommand implements ICommand {
    @Override
    public void run(Event event, List<String> args) {
        Guild guild = null;
        String newRoleName = null;

        if (event instanceof MessageReceivedEvent) {
            guild = ((MessageReceivedEvent) event).getGuild();

            if (args.get(0) == null || args.get(0).equals("")) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("You need to provide a role name!");
                sendEphemeralEmbed(embed, event);
                return;
            }

            newRoleName = args.get(0);
        }

        if (event instanceof SlashCommandEvent) {
            guild = ((SlashCommandEvent) event).getGuild();

            newRoleName = ((SlashCommandEvent) event).getOption("role").getAsRole().getName();
        }

        ServerData data = Bot.getINSTANCE().getGuildData().get(guild);

        data.setDJRoleName(newRoleName);

        try {
            data.save();

        } catch (IOException e){
            e.printStackTrace();

        } finally {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("DJ role changed to: " + args.get(0));
            embed.setColor(ColorScheme.RESPONSE);
            sendEmbed(embed, event);
        }
    }

    @Override
    public @NotNull String getCommandWord() {
        return "changedjrole";
    }

    @Override
    public @NotNull String getDescription() {
        return "changes the name of the DJ role for the music player." +
                "Usage: `[prefix] settings changedjrole [new role name]` (This is case sensitive!)";
    }

    @Override
    public @NotNull CommandData getCommandData() {
        CommandData commandData = new CommandData(getCommandWord(), "changes the name of the DJ role for the music player");
        commandData.addOption(OptionType.ROLE, "role", "The new DJ role", true);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
