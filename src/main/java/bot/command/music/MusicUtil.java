package bot.command.music;

import bot.Bot;
import bot.command.CommandContext;
import bot.db.legacy.server.ServerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MusicUtil {
    public static void sendErrorEmbed(EmbedBuilder embed, CommandContext context) {
        Event event = context.getEvent();
        if (event instanceof SlashCommandInteractionEvent) {
            ((SlashCommandInteractionEvent) event).getHook().setEphemeral(true);
        }
        context.respondEmbed(embed);
    }

    public static boolean inSameVC(Member user, Member self) {
        AudioChannel userVoiceChannel = user.getVoiceState().getChannel();
        AudioChannel selfVoiceChannel = self.getVoiceState().getChannel();

        if (userVoiceChannel == null || selfVoiceChannel == null) {
            return false;
        }

        return (userVoiceChannel == selfVoiceChannel);
    }

    public static boolean isVCEmpty(Member self) {
        AudioChannel channel = self.getVoiceState().getChannel();

        if (channel == null) {
            return true;
        }

        int members = 0;
        for (Member member : channel.getMembers()) {
            if (member.getUser().isBot()) {
                continue;
            }
            members++;
        }

        return (members == 0);
    }

    public static boolean hasPermissions(Member user) {
        List<Role> roles = user.getRoles();
        ServerData serverData = Bot.getINSTANCE().getGuildData(user.getGuild());

        if (user.isOwner()) return true;

        if (user.hasPermission(Permission.MANAGE_CHANNEL) || user.hasPermission(Permission.ADMINISTRATOR)) return true;

        for (Role role : roles) {
            if (role.getName().equals(serverData.getDJRoleName())) {
                return true;
            }
        }

        return false;
    }

    public static boolean isURL(String link) {
        try {
            new URI(link);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public static String formatTime(long duration) {

        Duration time = Duration.ofMillis(duration);
        long seconds = time.toSeconds();

        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;

        return String.format("%02d:%02d:%02d", HH, MM, SS);
    }

    public static String timeLine(long currentTime, long duration) {
        ArrayList<String> line = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            line.add("=");
        }

        double percent = ((double) currentTime / duration) * 100;
        int index = Math.toIntExact(Math.round(percent) / 5);

        line.set(index, "\uD83D\uDD18");

        StringBuilder sb = new StringBuilder();

        sb.append("[");

        for (String item : line) {
            sb.append(item);
        }

        sb.append("]");

        return sb.toString();
    }

    public static boolean checkIsNumber(String number) {
        try {
            int i = Integer.parseInt(number);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
