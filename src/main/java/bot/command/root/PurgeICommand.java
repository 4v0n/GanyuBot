package bot.command.root;

import bot.command.ICommand;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static bot.command.CommandMethods.*;

public class PurgeICommand implements ICommand {

    private final CommandDataImpl commandData;
    private final OptionData isCommand;
    private final OptionData hasImage;
    private final OptionData hasVideo;
    private final OptionData targetedUsers;
    private final OptionData numToDelete;
    private final OptionData domain;

    public PurgeICommand() {
        this.commandData = new CommandDataImpl(getCommandWord(), getDescription());

        this.isCommand = new OptionData(OptionType.BOOLEAN, "commands", "Whether to delete commands");
        this.hasImage = new OptionData(OptionType.BOOLEAN, "images", "whether to delete images");
        this.hasVideo = new OptionData(OptionType.BOOLEAN, "videos", "whether to delete videos");
        this.targetedUsers = new OptionData(OptionType.USER, "user", "A specific user to delete messages from");
        this.domain = new OptionData(OptionType.STRING, "website", "whether to delete any messages with this specific website");
        this.numToDelete = new OptionData(OptionType.INTEGER, "amount", "amount of messages to scroll back to. (optional) (not working)");

        this.commandData.addOptions(isCommand, hasImage, hasVideo, targetedUsers, domain, numToDelete);
    }

    @Override
    public void run(Event event, List<String> args) {
        if (event instanceof MessageReceivedEvent) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Incorrect usage!");
            embed.setDescription("This command may only be used through a slash command");
            embed.setColor(ColorScheme.ERROR);
            ((MessageReceivedEvent) event).getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        if (event instanceof SlashCommandInteractionEvent) {
            Member member = ((SlashCommandInteractionEvent) event).getMember();

            boolean hasPermission = member.hasPermission(Permission.ADMINISTRATOR);

            if (!hasPermission) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Insufficient permissions!");
                embed.setDescription("You need to have the `Administrator` permission to use this set of commands");
                embed.setColor(ColorScheme.ERROR);
                ((SlashCommandInteractionEvent) event).replyEmbeds(embed.build()).queue();
                return;
            }

            Thread thread = new Thread(() -> purge((SlashCommandInteractionEvent) event));
            thread.start();
        }
    }

    private int purgeAll(MessageChannel channel, List<OptionMapping> options) {
        int deletions = 0;
        ArrayList<Message> bin = new ArrayList<>();
        for (Message message : channel.getIterableHistory()) {
            if (message.getAuthor().isBot()) continue;

            try {
                OptionMapping command = getOption(options, this.isCommand.getName());
                if (command != null) {
                    if (isCommand(message)) {
                        message.delete().queue();
                        deletions++;
                    }
                }

                OptionMapping hasImage = getOption(options, this.hasImage.getName());
                if (hasImage != null) {
                    if (hasImage(message)) {
                        message.delete().queue();
                        deletions++;
                    }
                }

                OptionMapping hasVideo = getOption(options, this.hasVideo.getName());
                if (hasVideo != null) {
                    if (hasVideo(message)) {
                        message.delete().queue();
                        deletions++;
                    }
                }

                OptionMapping targetUser = getOption(options, targetedUsers.getName());
                if (targetUser != null) {
                    if (message.getAuthor() == targetUser.getAsUser()) {
                        message.delete().queue();
                        deletions++;
                    }
                }

                OptionMapping domain = getOption(options, this.domain.getName());
                if (domain != null) {
                    if (linksToDomain(message, domain.getAsString())) {
                        message.delete().queue();
                        deletions++;
                    }
                }
            } catch (Exception e) {
                // ignore
            }
        }
        return deletions;
    }

    private int purgeSpecific(MessageChannel channel, List<OptionMapping> options) {

        return 0;
    }

    private void purge(SlashCommandInteractionEvent event) {
        MessageChannel channel = event.getChannel();
        List<OptionMapping> options = event.getOptions();
        EmbedBuilder preEmbed = new EmbedBuilder();
        preEmbed.setColor(ColorScheme.INFO);
        preEmbed.setDescription("Starting purge");
        preEmbed.setFooter("Hold on tight!");
        sendEphemeralEmbed(preEmbed, event);

        OptionMapping amount = getOption(options, this.numToDelete.getName());
        int deletions;

        if (amount == null) {
            deletions = purgeAll(channel, options);
        } else {
            deletions = purgeSpecific(channel, options);
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);
        embed.setDescription("Successfully completed purge");
        embed.setFooter("Deleted **" + deletions + "** messages");
        Member member = event.getMember();
        member.getUser().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessageEmbeds(embed.build())).queue();
    }

    @Override
    public @NotNull String getCommandWord() {
        return "purge";
    }

    @Override
    public @NotNull String getDescription() {
        return "Purges all sent messages with the set filters";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
