package blackJack;

import java.util.HashMap;

public class BlackJackCommands {
    private HashMap<String, String> commands;

    public BlackJackCommands(){
        // base commands
        commands = new HashMap<>();
        commands.put("play","starts a game of blackjack");
    }

    public HashMap<String, String> getCommands(){
        return commands;
    }
}
