package bot.command.music;

import bot.Bot;
import bot.command.CommandContext;
import bot.db.legacy.server.ServerData;
import bot.feature.music.MusicManager;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static bot.command.music.MusicUtil.hasPermissions;

public class MusicUtil {

    public static boolean inVC(CommandContext context, boolean desiredResult) {
        Member user = context.getMember();

        if (!user.getVoiceState().inAudioChannel()) {
            if (desiredResult) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("You are not in a voice channel!");
                embed.setFooter("You need to be in a voice channel in order to use this command!");
                sendErrorEmbed(embed, context);
            }
            return false;
        }

        if (!desiredResult) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You are in a voice channel!");
            embed.setFooter("You must not be in a voice channel in order to use this command!");
            sendErrorEmbed(embed, context);
        }
        return true;
    }

    public static boolean inSameVC(CommandContext context, boolean desiredResult) {
        Member user = context.getMember();
        Member self = context.getSelfMember();
        AudioChannel userVoiceChannel = user.getVoiceState().getChannel();
        AudioChannel selfVoiceChannel = self.getVoiceState().getChannel();

        if (userVoiceChannel == null || selfVoiceChannel == null) {
            if (desiredResult) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("Neither you nor the bot are in a VC / same VC!");
                embed.setFooter("You need to be in the same VC to use this command!");
                sendErrorEmbed(embed, context);
            }
            return false;
        }

        if (userVoiceChannel != selfVoiceChannel) {
            if (desiredResult) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("The musicplayer is currently in a different VC!");
                embed.setFooter("You need to be in the same VC to use this command!");
                sendErrorEmbed(embed, context);
            }
            return false;
        }

        if (!desiredResult) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You are currently in the same VC!");
            embed.setFooter("You need to be in a different VC to use this command!");
            sendErrorEmbed(embed, context);
        }

        return true;
    }

    public static boolean playerActive(CommandContext context, boolean announce) {
        if (context.getSelfMember().getVoiceState().inAudioChannel()) {
            return true;
        }

        if (announce) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("The music player inactive!");
            embed.setColor(ColorScheme.ERROR);
            sendErrorEmbed(embed, context);
        }
        return false;
    }

    public static boolean hasPermissions(CommandContext context, boolean announce) {
        Member user = context.getMember();
        List<Role> roles = user.getRoles();
        ServerData serverData = Bot.getINSTANCE().getGuildData(user.getGuild());

        if (user.isOwner()) {
            return true;
        }

        if (user.hasPermission(Permission.MANAGE_CHANNEL) || user.hasPermission(Permission.ADMINISTRATOR)) return true;

        for (Role role : roles) {
            if (role.getName().equals(serverData.getDJRoleName())) {
                return true;
            }
        }

        if (isVCEmpty(context, announce)) {
            return true;
        }

        if (announce) {
            EmbedBuilder embed = new EmbedBuilder();
            ServerData data = Bot.getINSTANCE().getGuildData(context.getGuild());
            embed.setDescription("You don't have the permissions to use this command!");
            embed.setFooter("This command requires the `" + data.getDJRoleName() + "` (case sensitive) role or a role with the 'Manage Channels' permission to use.");
            sendErrorEmbed(embed, context);
        }
        return false;
    }

    public static boolean isVCEmpty(CommandContext context, boolean desiredResult) {
        Member self = context.getSelfMember();
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

        if (members == 0) {
            return true;
        } else {
            if (desiredResult) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("The bot is already in another VC that isn't empty!");
                embed.setFooter("Join VC: `" + self.getVoiceState().getChannel().getName() + "` or wait for the users to finish");
                sendErrorEmbed(embed, context);
            }
            return false;
        }
    }

    public static void sendErrorEmbed(EmbedBuilder embed, CommandContext context) {
        Event event = context.getEvent();
        if (event instanceof SlashCommandInteractionEvent) {
            ((SlashCommandInteractionEvent) event).getHook().setEphemeral(true);
        }
        context.respondEmbed(embed);
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
