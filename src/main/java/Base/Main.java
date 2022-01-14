package Base;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class Main {
    public static void main(String args[]) throws Exception {
        Bot botData = new Bot();
        botData.setToken("OTI2ODIxNjI5MzI0MDQ2NDA3.YdBP5w.GVhHa7AtBieMZPunw2-LXa-lBYs");
        botData.setPrefix(">g");

        JDABuilder jda = JDABuilder.createDefault(botData.getToken());
        jda.setActivity(Activity.playing("(" + botData.getPrefix() + ") not genshin impact"));
        jda.setStatus(OnlineStatus.ONLINE);
        jda.addEventListeners(new Parser(botData));


        botData.setJda(jda);

        botData.getJda().build();

        System.out.println("Bot started.");
    }

}
