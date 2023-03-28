package bot.listener;

import bot.Bot;
import bot.feature.root.BaseCommandHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() != null){
            Bot.getINSTANCE().loadGuildData(event.getGuild());
            BaseCommandHandler.getINSTANCE().parse(event);
        }
    }
}
