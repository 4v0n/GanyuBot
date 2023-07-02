package bot.listener;

import bot.feature.root.BaseCommandHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() != null){
            BaseCommandHandler.getINSTANCE().parse(event);
            System.out.println(event.getCommandString());
        }
    }
}
