package bot.command.music;

import bot.command.ICommand;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import bot.Bot;
import bot.util.ColorScheme;
import bot.db.server.ServerData;
import bot.feature.music.MusicManager;
import bot.feature.music.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static bot.command.music.MusicMethods.*;
import static bot.command.music.MusicMethods.sendErrorEmbed;

public class MoveSongICommand implements ICommand {
    @Override
    public void run(Event event, List<String> args) {
        Member user = null;
        Member self = null;
        Guild guild = null;
        int oldPos = 0;
        int newPos = 1;

        if (event instanceof MessageReceivedEvent) {
            user = ((MessageReceivedEvent) event).getMember();
            self = ((MessageReceivedEvent) event).getGuild().getSelfMember();
            guild = ((MessageReceivedEvent) event).getGuild();

            if (args.size() < 2) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("This command requires 2 arguments!");
                sendErrorEmbed(embed, event);
                return;
            }

            if (!checkIsNumber(args.get(0))) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription(args.get(0) + " isn't a number!");
                sendErrorEmbed(embed, event);
                return;
            }

            if (!checkIsNumber(args.get(1))) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription(args.get(1) + " isn't a number!");
                sendErrorEmbed(embed, event);
                return;
            }

            oldPos = Integer.parseInt(args.get(0));
            newPos = Integer.parseInt(args.get(1));
        }

        if (event instanceof SlashCommandEvent) {
            user = ((SlashCommandEvent) event).getMember();
            self = ((SlashCommandEvent) event).getGuild().getSelfMember();
            guild = ((SlashCommandEvent) event).getGuild();

            oldPos = Integer.parseInt(((SlashCommandEvent) event).getOption("old_position").getAsString());
            newPos = Integer.parseInt(((SlashCommandEvent) event).getOption("new_position").getAsString());
        }

        if (inSameVC(user, self)) {
            if (hasPermissions(user) || isVCEmpty(self)) {
                moveSong(oldPos, newPos, guild, event);

            } else {
                ServerData data = Bot.getINSTANCE().getGuildData().get(guild);
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("You don't have the permissions to use this command!");
                embed.setFooter("This command requires the `" + data.getDJRoleName() + "` (case sensitive) role or a role with the 'Manage Channels' permission to use.");
                sendErrorEmbed(embed, event);
            }

        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You are not in a VC with the bot!");
            sendErrorEmbed(embed, event);
        }
    }

    private void moveSong(int oldPos, int newPos, Guild guild, Event event) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        BlockingQueue<AudioTrack> songQueue = musicManager.getScheduler().getSongQueue();

        if (songQueue.size() < 2){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("There aren't enough songs to move around!");
            sendErrorEmbed(embed, event);
            return;
        }

        oldPos--;
        newPos--;

        final List<AudioTrack> trackList = new ArrayList<>(songQueue);

        AudioTrack movedSong;

        try {
            movedSong = trackList.get(oldPos);

            trackList.remove(oldPos);
            trackList.add(newPos, movedSong);

        } catch (Exception e) {

            return;
        }

        songQueue.clear();

        for (AudioTrack track : trackList) {
            musicManager.getScheduler().queue(track);
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);
        embed.setDescription("Moved `" + movedSong.getInfo().title + "` to position `" + (newPos + 1) + "`");
        sendEmbed(embed, event);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "move";
    }

    @Override
    public @NotNull String getDescription() {
        return "moves a track from one position to another. Usage: `[prefix] mp move [oldpos] [newpos]`";
    }

    @Override
    public @NotNull CommandData getCommandData() {
        CommandData commandData = new CommandData(getCommandWord(), "moves a track from one position to another");

        OptionData oldPosOptionData = new OptionData(OptionType.INTEGER, "old_position", "The position of the song you want to move", true).setMinValue(2);
        OptionData newPosOptionData = new OptionData(OptionType.INTEGER, "new_position", "The position you want to move the song to", true).setMinValue(1);

        commandData.addOptions(oldPosOptionData, newPosOptionData);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"mv"};
    }
}