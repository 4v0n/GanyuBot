package bot.command.settings;

import bot.Bot;
import bot.command.Command;
import bot.command.CommandContext;
import bot.db.legacy.server.ServerData;
import bot.util.ColorScheme;
import dev.morphia.Datastore;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static bot.command.CommandMethods.sendEphemeralEmbed;

public class ChangeDJRoleCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        Guild guild = context.getGuild();
        String newRoleName = null;
        Event event = context.getEvent();

        if (event instanceof MessageReceivedEvent) {
            if (args.get(0) == null || args.get(0).equals("")) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("You need to provide a role name!");
                sendEphemeralEmbed(embed, event);
                return;
            }
            newRoleName = args.get(0);
        }

        if (event instanceof SlashCommandInteractionEvent) {
            newRoleName = ((SlashCommandInteractionEvent) event).getOption("role").getAsRole().getName();
        }

        ServerData data = Bot.getInstance().getGuildData(guild);

        data.setDJRoleName(newRoleName);

        try {
            Datastore datastore = Bot.getInstance().getDatastore();
            datastore.save(data);

        } catch (Exception e){
            e.printStackTrace();

        } finally {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("DJ role changed to: " + args.get(0));
            embed.setColor(ColorScheme.RESPONSE);
            context.respondEmbed(embed);
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
    public @NotNull CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl(getCommandWord(), "changes the name of the DJ role for the music player");
        commandData.addOption(OptionType.ROLE, "role", "The new DJ role", true);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
