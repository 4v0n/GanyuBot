package bot.command.music;

import bot.Bot;
import bot.command.Command;
import bot.db.server.ServerData;
import bot.feature.music.MusicManager;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.util.ColorScheme;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static bot.command.music.MusicUtil.*;

public class RemoveSongCommand implements Command {
    @Override
    public void run(Event event, List<String> args) {
        Member user = null;
        Member self = null;
        Guild guild = null;
        int position = 0;

        if (event instanceof MessageReceivedEvent) {
            user = ((MessageReceivedEvent) event).getMember();
            self = ((MessageReceivedEvent) event).getGuild().getSelfMember();
            guild = ((MessageReceivedEvent) event).getGuild();

            if (args.isEmpty()){
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("You haven't specified which song to remove!");
                sendErrorEmbed(embed, event);
                return;
            }

            if (!checkIsNumber(args.get(0))){
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription(args.get(0) + " isn't a number!");
                sendErrorEmbed(embed, event);
                return;
            }

            position = Integer.parseInt(args.get(0));
        }

        if (event instanceof SlashCommandInteractionEvent) {
            user = ((SlashCommandInteractionEvent) event).getMember();
            self = ((SlashCommandInteractionEvent) event).getGuild().getSelfMember();
            guild = ((SlashCommandInteractionEvent) event).getGuild();

            position = Integer.parseInt(((SlashCommandInteractionEvent) event).getOption("position").getAsString());
        }

        position--;

        if (inSameVC(user, self)){
            if (hasPermissions(user) || isVCEmpty(self)){
                removeSong(position, guild, event);

            } else {
                ServerData data = Bot.getINSTANCE().getGuildData().get(guild);
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("You don't have the permissions to use this command!");
                embed.setFooter("This command requires the `"+ data.getDJRoleName() +"` (case sensitive) role or a role with the 'Manage Channels' permission to use.");
                sendErrorEmbed(embed, event);
            }

        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You are not in a VC with the bot!");
            sendErrorEmbed(embed, event);
        }

    }

    private void removeSong(int choice, Guild guild, Event event) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        BlockingQueue<AudioTrack> songQueue = musicManager.getScheduler().getSongQueue();


        final List<AudioTrack> trackList = new ArrayList<>(songQueue);

        AudioTrackInfo removedSong;

        try {
            removedSong = trackList.get(choice).getInfo();

        } catch (Exception e) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("That number is out of bounds!");
            embed.setFooter("There is no song in that position in the song queue");
            sendErrorEmbed(embed, event);
            return;
        }

        trackList.remove(choice);

        songQueue.clear();

        for (AudioTrack track : trackList) {
            musicManager.getScheduler().queue(track);
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);
        embed.setDescription("Removed `" + removedSong.title + "` by `" + removedSong.author + "`");
        sendEmbed(embed, event);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "remove";
    }

    @Override
    public @NotNull String getDescription() {
        return "removes a song from the queue by number";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl(getCommandWord(), getDescription());

        OptionData optionData = new OptionData(OptionType.INTEGER, "position",
                "The position of the song you wish to remove", true)
                .setMinValue(1).setMaxValue(Integer.MAX_VALUE);

        commandData.addOptions(optionData);
        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"rm"};
    }
}
