package blackJack;

import java.util.HashMap;

public class BlackJackCommands {
    private HashMap<String, String> commands;

    public BlackJackCommands(){
        // base commands
        commands = new HashMap<>();
        commands.put("play","starts a game of blackjack. Usage: `>g blackjack play [bet amount]`");
        commands.put("help", "shows a list of available commands.");
        commands.put("profile","Displays your blackjack profile.");
        commands.put("leaderboard","Displays the top 5 blackjack players in terms of credits.");
    }

    public HashMap<String, String> getCommands(){
        return commands;
    }
}
