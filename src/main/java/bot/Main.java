package bot;

import bot.feature.music.AutoLeaveVC;
import bot.feature.root.BaseCommandHandler;
import bot.listener.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Aron Navodh Kumarawatta
 * @version 28.03.2023
 */
public class Main {
    private static HashMap<String, String> settings;

    /**
     * Main method for bot
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Bot botData = Bot.getINSTANCE();
        settings = new HashMap<>();

        if (!loadConfig()) {
            return;
        }

        botData.setToken(settings.get("TOKEN"));
        botData.setPrefix(settings.get("PREFIX"));

        Reaction reactionParser = Reaction.getINSTANCE();

        JDABuilder jda = JDABuilder.create(botData.getToken(),
                GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)
        );



        jda.setStatus(OnlineStatus.ONLINE);
        jda.addEventListeners(new GuildMessage());
        jda.addEventListeners(reactionParser);
        jda.addEventListeners(new AutoLeaveVC());
        jda.addEventListeners(new SlashCommand());
        jda.addEventListeners(new GuildJoin());
        jda.setActivity(Activity.playing("(" + botData.getGlobalPrefix() + ") " + settings.get("STATUS")));

        try (MongoClient mongoClient = MongoClients.create(settings.get("DB_URI"))) {
            MongoDatabase database = mongoClient.getDatabase("GanyuBot");
            botData.setDB(database);
            System.out.println("Connected to DB: " + database.getName());
        } catch (Exception e) {
            System.out.println("Could not connect to DB");
            return;
        }

        Bot.getINSTANCE().setJDA(jda.build().awaitReady());
        System.out.println("Bot started");

        botData.addAdmin("195929905857429504");
        for (Guild guild : Bot.getJDA().getGuilds()){
            botData.loadGuildData(guild);
            BaseCommandHandler.getINSTANCE().upsertCommands(guild);
        }

    }

    /**
     * Loads the bot config from file
     *
     * @return
     */
    private static boolean loadConfig() {

        try {
            String parameterAndValue;
            BufferedReader configFile = new BufferedReader(new FileReader("config.cfg"));

            while (((parameterAndValue = configFile.readLine()) != null)) {
                String[] data = parameterAndValue.split(":", 2);
                if (data.length >= 2) {
                    settings.put(data[0], data[1]);
                }
            }

            return true;

        } catch (IOException e) {
            System.out.println("Config file missing/empty , please fill in the config file config file.");
            System.out.println("Please fill in the config file.");
            settings.put("TOKEN", null);
            settings.put("PREFIX", null);
            settings.put("STATUS", null);
            settings.put("DB_URI", null);

            File configFileName = new File("config.cfg");

            try (BufferedWriter configFile = new BufferedWriter(new FileWriter(configFileName))) {

                for (Map.Entry<String, String> settingEntry : settings.entrySet()) {
                    configFile.write(settingEntry.getKey() + ":" + settingEntry.getValue());
                    configFile.newLine();
                }
                configFile.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return false;
        }
    }
}
