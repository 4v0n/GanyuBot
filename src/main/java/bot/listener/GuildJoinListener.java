package bot.listener;

import bot.feature.root.BaseCommandBranch;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class GuildJoinListener extends ListenerAdapter {
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        BaseCommandBranch.getInstance().upsertCommands(event.getGuild());
    }
}
