package ganyu.casino.blackjack.commands;

import ganyu.base.Bot;
import ganyu.base.ColorScheme;
import ganyu.casino.data.CasinoData;
import ganyu.casino.data.CasinoGuildData;
import ganyu.casino.data.UserData;
import ganyu.command.message.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static ganyu.command.message.UsefulMethods.checkIsInt;
import static ganyu.command.message.UsefulMethods.checkIsLong;

public class AddCreditsCommand implements Command {
    @Override
    public void run(Event event, List<String> args) {
        if (event instanceof MessageReceivedEvent){
            parseMessage((MessageReceivedEvent) event, args);
            return;
        }

        if (event instanceof SlashCommandEvent){
            parseSlash((SlashCommandEvent) event, args);
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

                event.getMessage().addReaction("✅").queue();

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

    private void parseSlash(SlashCommandEvent event, List<String> args) {
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

        try {
            activityData.save();
        } catch (IOException ignored) {
        }

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
    public @NotNull CommandData getCommandData() {
        CommandData commandData = new CommandData(getCommandWord(), "Adds credits to the tagged user");
        commandData.addOption(OptionType.USER, "user", "The user you wish to add credits to", true);
        commandData.addOption(OptionType.INTEGER, "amount", "The amount of credits that you would like to add", true);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"add"};
    }
}
