package bot.command.blackjack;

import bot.Bot;
import bot.activity.blackjack.Game;
import bot.command.Command;
import bot.command.CommandContext;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InGameHitCommand implements Command {

    @Override
    public void run(CommandContext context, List<String> args) {
        Game game = (Game) Bot.getINSTANCE().getActivities().get(context.getAuthor().getId() + context.getMessageChannel().getId());
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
    public @NotNull CommandDataImpl getCommandData() {
        return new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"h"};
    }
}
