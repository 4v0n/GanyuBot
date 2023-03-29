package bot.command.blackjack;

import bot.Bot;
import bot.activity.blackjack.Game;
import bot.command.Command;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InGameStandCommand implements Command {
    @Override
    public void run(Event event, List<String> args) {
        if (event instanceof MessageReceivedEvent){
            parseMessage((MessageReceivedEvent) event);
            return;
        }

        if (event instanceof SlashCommandInteractionEvent){
            parseSlash((SlashCommandInteractionEvent) event);
        }
    }

    private void parseMessage(MessageReceivedEvent event) {
        getGame(event).finish();
    }

    private void parseSlash(SlashCommandInteractionEvent event) {
        getGame(event).finish();
    }

    @Override
    public @NotNull String getCommandWord() {
        return "stand";
    }

    @Override
    public @NotNull String getDescription() {
        return "End your round";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"s", "stop"};
    }

    private Game getGame(MessageReceivedEvent event) {
        return (Game) Bot.getINSTANCE().getActivities().get(event.getAuthor().getId() + event.getChannel().getId());
    }

    private Game getGame(SlashCommandInteractionEvent event) {
        return (Game) Bot.getINSTANCE().getActivities().get(event.getUser().getId() + event.getChannel().getId());
    }
}
