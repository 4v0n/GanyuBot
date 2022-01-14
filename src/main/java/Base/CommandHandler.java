package Base;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Scanner;

public class CommandHandler {

    private final Bot bot;
    private final ArrayList<String> commandList;

    public CommandHandler(Bot bot){
        this.bot = bot;
        commandList = new ArrayList<>();
    }
    public void parse(MessageReceivedEvent event, Bot bot){
        //template class
    }

    public void addCommand(String command){
        commandList.add(command);
    }

    public ArrayList<String> getCommandList() {
        return commandList;
    }

    public String getPrefix() {
        return bot.getPrefix();
    }

    protected ArrayList<String> splitString(String string) {
        ArrayList<String> stringArray = new ArrayList<>();
        Scanner tokenizer = new Scanner(string);

        while (tokenizer.hasNext()) {
            stringArray.add(tokenizer.next());
        }
        return stringArray;
    }
}
