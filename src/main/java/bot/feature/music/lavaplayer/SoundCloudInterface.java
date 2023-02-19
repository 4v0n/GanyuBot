package bot.feature.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.source.soundcloud.*;

public class SoundCloudInterface {
    public static SoundCloudAudioSourceManager getManager(){
        return new SoundCloudAudioSourceManager(false, createDataReader(), createHTMLLoader(), createFormatHandler(), createPlayListLoader());
    }

    private static SoundCloudPlaylistLoader createPlayListLoader() {
        return new DefaultSoundCloudPlaylistLoader(createHTMLLoader(), createDataReader(), createFormatHandler());
    }

    private static SoundCloudFormatHandler createFormatHandler() {
        return new DefaultSoundCloudFormatHandler();
    }

    private static SoundCloudHtmlDataLoader createHTMLLoader() {
        return new DefaultSoundCloudHtmlDataLoader();
    }

    private static SoundCloudDataReader createDataReader() {
        return new DefaultSoundCloudDataReader();
    }
}
