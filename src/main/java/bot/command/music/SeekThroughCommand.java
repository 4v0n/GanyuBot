package bot.command.music;

import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.music.MusicManager;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.util.ColorScheme;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
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

        if (!inVC(context, true)) {
            return;
        }
        if (!playerActive(context, true)) {
            return;
        }
        if (!inSameVC(context, true)) {
            return;
        }
        if (!hasPermissions(context, true)) {
            return;
        }
        seekBy(amount, context);
    }

    private void seekBy(int amount, CommandContext context) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
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
        showNowPlaying(context);
    }

    private void showNowPlaying(CommandContext context) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
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
                "Amount of time in seconds to seek by (-/+)", true).setMinValue(Integer.MIN_VALUE).setMaxValue(Integer.MAX_VALUE);

        commandData.addOptions(optionData);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
