package Base;

import Database.GuildData;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static Bot botData;
    private static JDABuilder jda;
    private static HashMap<String, String> settings;

    public static void main(String[] args) throws Exception {
        createPathIfNotExist("GuildData");

        //botData = load();
        botData = new Bot();
        settings = new HashMap<>();

        loadConfig();
        loadFiles();

        botData.setToken(settings.get("TOKEN"));
        botData.setPrefix(settings.get("PREFIX"));
        botData.setUserID(settings.get("APPLICATIONID"));
        botData.setPfpURL(settings.get("PROFILEPICURL"));

        jda = JDABuilder.createDefault(botData.getToken());
        jda.setStatus(OnlineStatus.ONLINE);
        jda.addEventListeners(new Parser(botData));
        jda.setActivity(Activity.playing("(" + botData.getPrefix() + ") " + settings.get("STATUS")));

        botData.setJda(jda);

        botData.getJda().build();

        System.out.println("Bot started");
        botData.botLoop();

        botData.addAdmin("195929905857429504");
    }

    private static void createPathIfNotExist(String path) {
        File directory = new File(path);
        boolean done = false;

        if (!directory.exists()){
            done = directory.mkdir();
        }
        //System.out.println(done);
    }

    private static void hardCodedLoad(){
        botData.setToken("OTI2ODIxNjI5MzI0MDQ2NDA3.YdBP5w.GVhHa7AtBieMZPunw2-LXa-lBYs");
        botData.setPrefix(">g");
        botData.setUserID("926821629324046407");
        botData.setPfpURL("https://cdn.discordapp.com/avatars/926821629324046407/445ef6f9961d6b5bb09f03a1efc87cdd.png?size=256");
    }

    private static void loadConfig(){

        try{
            String parameterAndValue;
            BufferedReader configFile = new BufferedReader(new FileReader("config.cfg"));

            while (((parameterAndValue = configFile.readLine()) != null)){
                String[] data = parameterAndValue.split(":",2);
                if (data.length >= 2){
                    settings.put(data[0], data[1]);
                }
            }
        }
        catch (IOException e){
            System.out.println("Config file missing, creating new config file.");
            System.out.println("Please fill in the config file.");
            settings.put("TOKEN",null);
            settings.put("PREFIX", null);
            settings.put("APPLICATIONID", null);
            settings.put("PROFILEPICURL", "https://cdn.discordapp.com/attachments/931671609134178368/937342736934268928/settings.png");
            settings.put("STATUS", null);

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
        }
    }

    private static void loadFiles(){
        String[] pathnames;
        File file = new File("GuildData");
        pathnames = file.list();

        for (String fileName : pathnames) {
            System.out.println(fileName);
            String path = ("GuildData/" + fileName);


            if (path.endsWith(".dta")) {
                try {
                    String[] keys = fileName.split("\\.");
                    botData.addGuildData(keys[0], loadGuildData(path));
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("Could not load " + fileName);
                }
            }
        }
    }

    private static GuildData loadGuildData(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fin = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fin);
        GuildData object= (GuildData) ois.readObject();
        ois.close();
        fin.close();
        return object;
    }
}
