package ganyu.base.listener;

import ganyu.music.MusicManager;
import ganyu.music.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class allows the bot to listen to whenever a voice channel has been left
 *
 * @author Aron Navodh Kumarawatta
 * @version 29.05.2022
 */
public class ChannelJoin extends ListenerAdapter {

    public ChannelJoin() {
    }

    /**
     * run when channel is left
     *
     * @param event
     */
    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        if (inVC(event)) {
            AudioChannel audioChannel = event.getChannelLeft();
            System.out.println("Heard channel leave");

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
                            MusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
                            musicManager.getScheduler().getSongQueue().clear();
                            musicManager.getAudioPlayer().destroy();
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

    /**
     * run when user moves to different channel
     *
     * @param event
     */
    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
        if (inVC(event)) {
            AudioChannel audioChannel = event.getChannelLeft();
            System.out.println("Heard channel leave");

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
                            MusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
                            musicManager.getScheduler().getSongQueue().clear();
                            musicManager.getAudioPlayer().stopTrack();
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

    private boolean inVC(GuildVoiceMoveEvent event) {
        GuildVoiceState voiceState = event.getGuild().getSelfMember().getVoiceState();

        if (voiceState == null) {
            return false;
        }

        return voiceState.inAudioChannel();
    }

    private boolean inVC(GuildVoiceLeaveEvent event) {
        GuildVoiceState voiceState = event.getGuild().getSelfMember().getVoiceState();

        if (voiceState == null) {
            return false;
        }

        return voiceState.inAudioChannel();
    }
}
