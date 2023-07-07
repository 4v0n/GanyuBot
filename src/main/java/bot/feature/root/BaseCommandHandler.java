package bot.feature.root;

import bot.command.Command;
import bot.command.CommandContext;
import bot.command.button.ButtonAction;
import bot.command.button.ButtonCommandHandler;
import bot.command.root.*;
import bot.feature.blackjack.BlackJackHandler;
import bot.command.CommandHandler;
import bot.feature.booru.ImageHandler;
import bot.feature.music.MusicParser;
import bot.feature.settings.SettingsParser;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
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

        addCommand(new PurgeCommand());

        addCommand(new Command() {
            @Override
            public void run(CommandContext context, List<String> args) {
                context.getMessageChannel().sendMessage("lol").queue(message -> {
                    ButtonCommandHandler bch = new ButtonCommandHandler(message) {
                        @Override
                        public void buildCommands() {
                            getCommandCenter().addCommand(Button.primary("id", "test"), context1 -> context1.getMessageChannel().sendMessage("button clicked").queue());
                        }
                    };
                });
            }

            @Override
            public @NotNull String getCommandWord() {
                return "test";
            }

            @Override
            public @NotNull String getDescription() {
                return "test buttons";
            }

            @Override
            public @NotNull CommandDataImpl getCommandData() {
                return new CommandDataImpl(getCommandWord(), getDescription());
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
