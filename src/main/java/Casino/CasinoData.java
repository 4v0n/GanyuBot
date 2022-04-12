package Casino;

import Database.ActivityData;

import java.util.ArrayList;
import java.util.HashMap;

public class CasinoData extends ActivityData {

    private final HashMap<String, CasinoPlayerData> players;
    private HashMap<Integer, CasinoPlayerData> leaderBoard;

    public CasinoData(){
        super("BLACKJACK");
        this.players = new HashMap<>();
        this.leaderBoard = new HashMap<>();
    }

    public HashMap<Integer, CasinoPlayerData> getLeaderBoard() {
        return leaderBoard;
    }

    public HashMap<String, CasinoPlayerData> getPlayers() {
        return players;
    }

    public void incrementCredits(){
        for (CasinoPlayerData player : players.values()){
            player.setCredits(player.getCredits() + 100);
        }
    }

    public void updateLeaderBoard(){
        //HashMap<Integer, PlayerData> tempMap = new HashMap<>();
        leaderBoard = new HashMap<>();

        int offset = 0;
        for (CasinoPlayerData player : players.values()){

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
