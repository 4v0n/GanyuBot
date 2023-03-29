package bot.command.blackjack;

import bot.Bot;
import bot.command.Command;
import bot.db.legacy.blackjack.CasinoData;
import bot.db.legacy.blackjack.CasinoGuildData;
import bot.db.legacy.blackjack.UserData;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static bot.command.CommandMethods.checkIsInt;
import static bot.command.CommandMethods.checkIsLong;

public class AddCreditsCommand implements Command {
    @Override
    public void run(Event event, List<String> args) {
        if (event instanceof MessageReceivedEvent){
            parseMessage((MessageReceivedEvent) event, args);
            return;
        }

        if (event instanceof SlashCommandInteractionEvent){
            parseSlash((SlashCommandInteractionEvent) event, args);
        }
    }

    private void parseMessage(MessageReceivedEvent event, List<String> args) {
        MessageChannel channel = event.getChannel();
        Bot bot = Bot.getINSTANCE();

        if (args.size() < 2){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("You need to provide 2 arguments!");
            embed.setColor(ColorScheme.ERROR);
            channel.sendMessageEmbeds(embed.build()).queue();
        }

        if (!checkIsInt(args.get(1))){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("That isn't a number!");
            embed.setColor(ColorScheme.ERROR);
            channel.sendMessageEmbeds(embed.build()).queue();
        }

        String userID = args.get(0).substring(2, args.get(0).length()-1);

        if (!checkIsLong(userID)){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("You haven't correctly tagged a user!");
            embed.setColor(ColorScheme.ERROR);
            channel.sendMessageEmbeds(embed.build()).queue();
        }

        if (bot.getAdmins().contains(event.getAuthor().getId()) || verifyPermissions(event.getMember())) {

            System.out.println(args);
            try {
                CasinoGuildData activityData = CasinoData.getInstance().getGuildData(event.getGuild());
                long amount = Long.parseLong(args.get(1));

                UserData player = activityData.getPlayer(userID);

                if (player == null){
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setDescription("There is no data for this user!");
                    embed.setColor(ColorScheme.ERROR);
                    channel.sendMessageEmbeds(embed.build()).queue();
                    return;
                }

                player.setCredits(player.getCredits() + amount);

                activityData.save();

                event.getMessage().addReaction(Emoji.fromUnicode("✅")).queue();

            } catch (Exception e) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("The user was not found! / The amount you entered is invalid!");
                embed.setColor(ColorScheme.ERROR);
                channel.sendMessageEmbeds(embed.build()).queue();
            }
        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("You are not an admin / mod!");
            embed.setColor(ColorScheme.ERROR);
            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }

    private void parseSlash(SlashCommandInteractionEvent event, List<String> args) {
        Bot bot = Bot.getINSTANCE();

        Member user = event.getOption("user").getAsMember();
        if (user == null){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("The user was not found!");
            embed.setColor(ColorScheme.ERROR);
            event.replyEmbeds(embed.build()).queue();
            return;
        }

        long amount = event.getOption("amount").getAsLong();


        CasinoGuildData activityData = CasinoData.getInstance().getGuildData(event.getGuild());
        UserData player = activityData.getPlayer(user);
        player.setCredits(player.getCredits() + amount);

        activityData.save();

        event.reply("✅").setEphemeral(true).queue();
    }

    private boolean verifyPermissions(Member member) {
        return (member.hasPermission(Permission.MANAGE_CHANNEL) || member.hasPermission(Permission.ADMINISTRATOR));
    }

    @Override
    public @NotNull String getCommandWord() {
        return "addcredits";
    }

    @Override
    public @NotNull String getDescription() {
        return "***Admin only command.*** Adds credits to the tagged user. Usage: `[prefix] bj addcredits @[user] [amount]`";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl(getCommandWord(), "Adds credits to the tagged user");
        commandData.addOption(OptionType.USER, "user", "The user you wish to add credits to", true);
        commandData.addOption(OptionType.INTEGER, "amount", "The amount of credits that you would like to add", true);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"add"};
    }
}
