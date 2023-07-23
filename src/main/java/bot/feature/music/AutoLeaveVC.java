package bot.feature.music;

import bot.Bot;
import bot.feature.music.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class allows the bot to auto disconnect from a voice channel
 * when there are no members in it.
 *
 * @author Aron Navodh Kumarawatta
 * @version 28.03.2023
 */
public class AutoLeaveVC extends ListenerAdapter {

    public AutoLeaveVC() {
    }

    /**
     * run when channel is left
     *
     * @param event
     */
    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if (inVC(event)) {
            Bot bot = Bot.getInstance();

            AudioChannel audioChannel = findChannelByGuild(event.getGuild());

            List<Member> members = audioChannel.getMembers();

            int memberCount = 0;
            for (Member member : members) {
                if (!member.getUser().isBot()) {
                    memberCount++;
                }
            }

            if (memberCount == 0) {
                Thread.UncaughtExceptionHandler exceptionHandler = (t, e) -> {
                    // ignore
                };

                Thread waitThread = new Thread(() -> {
                    try {
                        Thread.sleep(TimeUnit.MINUTES.toMillis(1));

                        List<Member> members1 = audioChannel.getMembers();

                        int memberCount1 = 0;
                        for (Member member : members1) {
                            if (!member.getUser().isBot()) {
                                memberCount1++;
                            }
                        }

                        if (memberCount1 == 0) {
                            Guild guild = event.getGuild();
                            guild.getAudioManager().closeAudioConnection();
                            PlayerManager.getInstance().getMusicManager(guild).getAudioPlayer().destroy();
                            PlayerManager.getInstance().getMusicManager(guild).getScheduler().getSongQueue().clear();
                            PlayerManager.getInstance().removeMusicManager(guild);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                waitThread.setUncaughtExceptionHandler(exceptionHandler);
                waitThread.start();
            }
        }
    }

    private boolean inVC(GenericGuildVoiceEvent event) {
        GuildVoiceState voiceState = event.getGuild().getSelfMember().getVoiceState();

        if (voiceState == null) {
            return false;
        }

        return voiceState.inAudioChannel();
    }

    private AudioChannel findChannelByGuild(Guild guild) {
        List<VoiceChannel> voiceChannels = guild.getVoiceChannels();
        Member selfMember = guild.getSelfMember();

        for (VoiceChannel vc : voiceChannels) {
            if (vc.getGuild() == guild && vc.getMembers().contains(selfMember)) {
                return vc;
            }
        }
        return null;
    }
}
