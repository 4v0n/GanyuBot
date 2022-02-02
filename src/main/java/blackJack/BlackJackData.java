package blackJack;

import Database.ActivityData;

import java.util.ArrayList;
import java.util.HashMap;

public class BlackJackData extends ActivityData {

    private final HashMap<String, BlackjackPlayerData> players;
    private HashMap<Integer, BlackjackPlayerData> leaderBoard;

    public BlackJackData(){
        super("BLACKJACK");
        this.players = new HashMap<>();
        this.leaderBoard = new HashMap<>();
    }

    public HashMap<Integer, BlackjackPlayerData> getLeaderBoard() {
        return leaderBoard;
    }

    public HashMap<String, BlackjackPlayerData> getPlayers() {
        return players;
    }

    public void incrementCredits(){
        for (BlackjackPlayerData player : players.values()){
            player.setCredits(player.getCredits() + 100);
        }
    }

    public void updateLeaderBoard(){
        //HashMap<Integer, PlayerData> tempMap = new HashMap<>();
        leaderBoard = new HashMap<>();

        int offset = 0;
        for (BlackjackPlayerData player : players.values()){

            /*
            if (!leaderBoard.containsKey(player)){
                leaderBoard.put(player, player.getCredits());
            } else {
                leaderBoard.remove(player);
                leaderBoard.put(player, player.getCredits());
            }


            tempMap.put(player.getCredits(), player);

            */

            if (!leaderBoard.containsKey(player.getCredits())){
                leaderBoard.put(player.getCredits(), player);
            } else {
                offset++;
                leaderBoard.remove(player.getCredits(), player);
                player.setTieOffset(offset);
                leaderBoard.put(player.getCredits(), player);
            }
        }
    }

    public ArrayList<Integer> getPositions(){
        return new ArrayList<>(leaderBoard.keySet());
    }
}
