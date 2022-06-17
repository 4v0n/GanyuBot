package ganyu.base;

import ganyu.base.commands.*;
import ganyu.casino.blackjack.BlackJackHandler;
import ganyu.command.message.Command;
import ganyu.command.message.CommandHandler;
import ganyu.image.ImageHandler;
import ganyu.music.MusicParser;
import ganyu.settings.SettingsParser;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BaseCommandHandler extends CommandHandler {

    private static BaseCommandHandler INSTANCE;

    private BaseCommandHandler() {
        super(null);
    }

    public static BaseCommandHandler getINSTANCE(){
        if (INSTANCE == null){
            INSTANCE = new BaseCommandHandler();
        }

        return INSTANCE;
    }

    @Override
    protected void buildCommands() {
        addCommand(new CopyCommand());

        addCommand(new ImagesCommand());

        addCommand(new BlackjackCommand());

        addCommand(new MusicPlayerCommand());

        addCommand(new SettingsCommand());

        addCommand(new InfoCommand());

        addCommand(new Command() {
            @Override
            public void run(Event thing, List<String> args) {
                if (thing instanceof MessageReceivedEvent){
                    MessageReceivedEvent event = (MessageReceivedEvent) thing;

                    List<MessageEmbed> embeds = event.getMessage().getReferencedMessage().getEmbeds();

                    event.getChannel().sendMessage("done").queue();
                }
            }

            @Override
            public @NotNull String getCommandWord() {
                return "grabembed";
            }

            @Override
            public @NotNull String getDescription() {
                return "grab embed";
            }

            @Override
            public @NotNull CommandData getCommandData() {
                return new CommandData(getCommandWord(), getDescription());
            }

            @Override
            public String[] getSynonyms() {
                return new String[0];
            }
        });
    }

    @Override
    protected void buildChildrenCommandHandlers() {
        addHandler(new SettingsParser(this, "settings"));
        addHandler(new MusicParser(this, "musicplayer"));
        addHandler(new BlackJackHandler(this, "blackjack"));
        addHandler(new ImageHandler(this, "images"));
    }
}
