package Casino.Blackjack;

import java.util.HashMap;

public class BlackJackCommandCentre {

    private HashMap<String, String> commands;

    public BlackJackCommandCentre() {
        initCommands();
    }

    private void initCommands(){
        commands = new HashMap<>();
        commands.put("play","starts a game of blackjack. Usage: `>g blackjack play [bet amount]`");
        commands.put("help", "shows a list of available commands.");
        commands.put("profile","Displays your blackjack profile.");
        commands.put("leaderboard","Displays the top 5 blackjack players in terms of credits.");
    }

    private HashMap<String, String> getCommands() {
        return commands;
    }
}


