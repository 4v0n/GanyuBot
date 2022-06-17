package ganyu.base.listener;

import ganyu.base.BaseCommandHandler;
import ganyu.base.Bot;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class GuildJoin extends ListenerAdapter {
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        Bot.getINSTANCE().loadGuildData(event.getGuild());
        BaseCommandHandler.getINSTANCE().upsertCommands(event.getGuild());
    }
}
