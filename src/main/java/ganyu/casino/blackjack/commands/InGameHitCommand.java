package ganyu.casino.blackjack.commands;

import ganyu.base.Bot;
import ganyu.casino.blackjack.Game;
import ganyu.command.message.Command;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InGameHitCommand implements Command {

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
        Game game = getGame(event);
        game.getPlayer().addCard(game.getDeck());
        game.update();
    }

    private void parseSlash(SlashCommandEvent event) {
        Game game = getGame(event);
        game.getPlayer().addCard(game.getDeck());
        game.update();
    }

    @Override
    public @NotNull String getCommandWord() {
        return "hit";
    }

    @Override
    public @NotNull String getDescription() {
        return "Gives you one more card.";
    }

    @Override
    public @NotNull CommandData getCommandData() {
        return new CommandData(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"h"};
    }

    private Game getGame(MessageReceivedEvent event) {
        return (Game) Bot.getINSTANCE().getActivities().get(event.getAuthor().getId() + event.getChannel().getId());
    }

    private Game getGame(SlashCommandEvent event) {
        return (Game) Bot.getINSTANCE().getActivities().get(event.getUser().getId() + event.getChannel().getId());
    }
}
