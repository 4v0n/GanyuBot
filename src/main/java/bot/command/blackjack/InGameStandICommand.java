package bot.command.blackjack;

import bot.command.ICommand;
import bot.Bot;
import bot.activity.blackjack.Game;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InGameStandICommand implements ICommand {
    @Override
    public void run(Event event, List<String> args) {
        if (event instanceof MessageReceivedEvent){
            parseMessage((MessageReceivedEvent) event);
            return;
        }

        if (event instanceof SlashCommandEvent){
            parseSlash((SlashCommandEvent) event);
        }
    }

    private void parseMessage(MessageReceivedEvent event) {
        getGame(event).finish();
    }

    private void parseSlash(SlashCommandEvent event) {
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
    public @NotNull CommandData getCommandData() {
        return new CommandData(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"s", "stop"};
    }

    private Game getGame(MessageReceivedEvent event) {
        return (Game) Bot.getINSTANCE().getActivities().get(event.getAuthor().getId() + event.getChannel().getId());
    }

    private Game getGame(SlashCommandEvent event) {
        return (Game) Bot.getINSTANCE().getActivities().get(event.getUser().getId() + event.getChannel().getId());
    }
}
