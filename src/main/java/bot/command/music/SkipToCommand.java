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
import java.util.concurrent.BlockingQueue;

import static bot.command.music.MusicUtil.*;

public class SkipToCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        int target = 0;
        Event event = context.getEvent();

        if (event instanceof MessageReceivedEvent) {
            if (args.isEmpty()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("You haven't specified which song to skip!");
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
            target = Integer.parseInt(args.get(0));
        }

        if (event instanceof SlashCommandInteractionEvent) {
            target = Integer.parseInt(((SlashCommandInteractionEvent) event).getOption("position").getAsString());
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
        skiptoSong(target, context);
    }

    private void skiptoSong(int target, CommandContext context) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        BlockingQueue<AudioTrack> songQueue = musicManager.getScheduler().getSongQueue();

        if (songQueue.size() < target){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("The number you have entered is out of range!");
            embed.setFooter("The number you have entered is larger than the length of the list");
            sendErrorEmbed(embed, context);
            return;
        }

        for (int i = target; i > 0; i--) {
            musicManager.getAudioPlayer().stopTrack();

            if (!musicManager.getScheduler().getSongQueue().isEmpty()) {
                musicManager.getScheduler().nextTrack();
            }
        }

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
        return "skipto";
    }

    @Override
    public @NotNull String getDescription() {
        return "skips all songs until a chosen point. Usage: `[prefix] mp skipto [number]`";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl(getCommandWord(), "skips all songs until a chosen point");
        OptionData optionData = new OptionData(OptionType.INTEGER, "position", "the position of the song to skip to", true)
                .setMinValue(1);
        commandData.addOptions(optionData);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"st"};
    }
}
