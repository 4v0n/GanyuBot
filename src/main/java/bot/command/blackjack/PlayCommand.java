package bot.command.blackjack;

import bot.Bot;
import bot.activity.Activity;
import bot.activity.blackjack.Game;
import bot.command.Command;
import bot.db.blackjack.CasinoData;
import bot.db.blackjack.UserData;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayCommand implements Command {

    @Override
    public void run(Event event, List<String> args) {
        if (event instanceof MessageReceivedEvent) {
            handleMessageReceivedEvent((MessageReceivedEvent) event, args);
            return;
        }

        if (event instanceof SlashCommandInteractionEvent) {
            handleSlashCommandEvent((SlashCommandInteractionEvent) event, args);
            return;
        }
    }

    private void handleMessageReceivedEvent(MessageReceivedEvent event, List<String> args) {
        EmbedBuilder embed = new EmbedBuilder();
        MessageChannel channel = event.getChannel();
        Bot bot = Bot.getINSTANCE();
        UserData playerData = CasinoData.getInstance().getGuildData(event.getGuild()).getPlayer(event.getMember());

        Activity activity = bot.getActivities().get(event.getAuthor().getId() + event.getChannel().getId());
        if (activity != null) {
            embed.setDescription("You are already playing a mini-game in this channel!" +
                    "\nCurrent game: " + activity.getMessage().getJumpUrl());
            embed.setColor(ColorScheme.ERROR);
            channel.sendMessageEmbeds(embed.build()).queue();
            return;
        }

        if (args.isEmpty()) {
            embed.setDescription("The game will have no bet!");
            embed.setColor(ColorScheme.INFO);
            channel.sendMessageEmbeds(embed.build()).queue();
            startGame(event, 0);
            return;
        }

        int bet;
        try {
            bet = Integer.parseInt(args.get(0));
        } catch (Exception e) {
            embed.setDescription(args.get(0) + " is not a number!");
            embed.setColor(ColorScheme.ERROR);
            channel.sendMessageEmbeds(embed.build()).queue();
            return;
        }

        if (bet > playerData.getCredits()) {
            embed.setDescription("You don't have enough credits!" +
                    "\nYou currently have " + playerData.getCredits() + " credits.");
            embed.setColor(ColorScheme.ERROR);
            channel.sendMessageEmbeds(embed.build()).queue();
            return;
        }

        startGame(event, bet);
    }

    private void handleSlashCommandEvent(SlashCommandInteractionEvent event, List<String> args) {
        Bot bot = Bot.getINSTANCE();
        EmbedBuilder embed = new EmbedBuilder();

        Activity activity = bot.getActivities().get(event.getUser().getId() + event.getChannel().getId());
        if (activity != null) {
            embed.setDescription("You are already playing a mini-game in this channel!" +
                    "\nCurrent game: " + activity.getMessage().getJumpUrl());
            embed.setColor(ColorScheme.ERROR);
            event.replyEmbeds(embed.build()).queue();
            return;
        }

        OptionMapping betOption = event.getOption("bet");
        int bet;

        if (betOption == null) {
            embed.setDescription("The game will have no bet!");
            embed.setColor(ColorScheme.INFO);
            event.replyEmbeds(embed.build()).queue();
            startGame(event, 0);
            return;

        } else {
            bet = Integer.parseInt(betOption.getAsString());

            UserData playerData = CasinoData.getInstance().getGuildData(event.getGuild()).getPlayer(event.getMember());

            if (bet > playerData.getCredits()) {
                embed.setDescription("You don't have enough credits!" +
                        "\nYou currently have " + playerData.getCredits() + " credits.");
                embed.setColor(ColorScheme.ERROR);
                event.replyEmbeds(embed.build()).queue();
                return;
            }
        }

        startGame(event, bet);
    }

    private void startGame(MessageReceivedEvent event, int bet) {
        Bot bot = Bot.getINSTANCE();
        MessageChannel channel = event.getChannel();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("The game will be starting soon!");
        embed.setColor(ColorScheme.INFO);
        channel.sendMessageEmbeds(embed.build()).queue(newMessage -> bot.addActivity(new Game(event, newMessage, bet)));
    }

    private void startGame(SlashCommandInteractionEvent event, int bet) {
        Bot bot = Bot.getINSTANCE();
        MessageChannel channel = event.getChannel();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("The game will be starting soon!");
        embed.setColor(ColorScheme.INFO);
        event.deferReply().queue();
        event.getHook().sendMessageEmbeds(embed.build()).queue(newMessage -> bot.addActivity(new Game(event, newMessage, bet)));
    }

    @Override
    public @NotNull String getCommandWord() {
        return "play";
    }

    @Override
    public @NotNull String getDescription() {
        return "starts a round of blackjack. Add a bet to bet credits. Usage: `[prefix] bj play [bet amount]`";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl(getCommandWord(), getDescription());
        commandData.addOption(OptionType.INTEGER, "bet", "The amount of credits you would like to bet", false);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"p"};
    }
}