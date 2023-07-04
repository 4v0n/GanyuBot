package bot.command.music;

import bot.Bot;
import bot.command.Command;
import bot.command.CommandContext;
import bot.db.legacy.server.ServerData;
import bot.feature.music.MusicManager;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.util.ColorScheme;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static bot.command.music.MusicUtil.*;

public class SeekThroughCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        Member user = context.getMember();
        Member self = context.getSelfMember();
        Guild guild = context.getGuild();
        int amount = 0;
        Event event = context.getEvent();

        if (event instanceof MessageReceivedEvent) {
            if (args.isEmpty()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("You haven't specified how much time to seek by!");
                sendErrorEmbed(embed, context);
                return;
            }

            if (!checkIsNumber(args.get(0))) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription(args.get(0) + " isn't a number!");
                sendErrorEmbed(embed, context);
                return;
            }

            amount = Integer.parseInt(args.get(0));
        }

        if (event instanceof SlashCommandInteractionEvent) {
            amount = Integer.parseInt(((SlashCommandInteractionEvent) event).getOption("position").getAsString());
        }

        if (inSameVC(user, self)) {
            if (hasPermissions(user) || isVCEmpty(self)) {
                seekBy(amount, guild, context);

            } else {
                ServerData data = Bot.getINSTANCE().getGuildData(guild);
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("You don't have the permissions to use this command!");
                embed.setFooter("This command requires the `" + data.getDJRoleName() + "` (case sensitive) role or a role with the 'Manage Channels' permission to use.");
                sendErrorEmbed(embed, context);
            }

        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You are not in a VC with the bot!");
            sendErrorEmbed(embed, context);
        }
    }

    private void seekBy(int amount, Guild guild, CommandContext context) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        AudioTrack playingTrack = musicManager.getAudioPlayer().getPlayingTrack();

        if (amount < 0 && playingTrack.getPosition() < Math.abs(amount)) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("The amount u have provided is out of range!");
            embed.setFooter("The current track is at: " + formatTime(playingTrack.getPosition()));
            sendErrorEmbed(embed, context);
            return;
        }

        if (amount > 0 && (playingTrack.getDuration() - playingTrack.getPosition()) < Math.abs(amount)) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("The amount u have provided is out of range!");
            embed.setFooter("The current track is at: " + formatTime(playingTrack.getPosition()));
            sendErrorEmbed(embed, context);
            return;
        }

        playingTrack.setPosition(playingTrack.getPosition() + TimeUnit.SECONDS.toMillis(amount));
        showNowPlaying(guild, context);
    }

    private void showNowPlaying(Guild guild, CommandContext context) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        AudioTrack track = musicManager.getAudioPlayer().getPlayingTrack();

        if (track == null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("There is nothing playing at the moment!");
            embed.setFooter("Queue something up before using this command.");
            sendErrorEmbed(embed, context);

        } else {
            String string =
                    "`" + timeLine(track.getPosition(), track.getDuration()) + "`\n" +
                            "`" + formatTime(track.getPosition()) + " / " + formatTime(track.getDuration()) + "`";

            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor("Now playing");
            embed.setTitle(track.getInfo().author + " - " + track.getInfo().title, track.getInfo().uri);
            embed.setDescription(string);

            Member userData = (Member) track.getUserData();
            String id = userData.getEffectiveName();

            embed.setFooter(("Requested by: " + id), userData.getEffectiveAvatarUrl());
            embed.setColor(ColorScheme.RESPONSE);
            embed.setThumbnail("http://img.youtube.com/vi/" + track.getInfo().identifier + "/0.jpg");

            context.respondEmbed(embed);
        }
    }

    @Override
    public @NotNull String getCommandWord() {
        return "seek";
    }

    @Override
    public @NotNull String getDescription() {
        return "Allows you to seek backwards or forwards by a certain amount (negative to seek back). " +
                "Usage: `[prefix] mp seek (-)[time in seconds]`";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl(getCommandWord(),"Allows you to seek backwards or forwards by a certain amount");

        OptionData optionData = new OptionData(OptionType.INTEGER, "amount",
                "Amount of time in seconds to seek by (-/+)").setMinValue(Integer.MIN_VALUE).setMaxValue(Integer.MAX_VALUE);

        commandData.addOptions(optionData);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
