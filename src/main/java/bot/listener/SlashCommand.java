package bot.listener;

import bot.feature.root.BaseCommandHandler;
import bot.Bot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommand extends ListenerAdapter {

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {

        if (event.getGuild() != null){
            Bot.getINSTANCE().loadGuildData(event.getGuild());
            BaseCommandHandler.getINSTANCE().parse(event);
        }
    }
}