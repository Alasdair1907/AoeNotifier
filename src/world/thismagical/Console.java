package world.thismagical;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Console {
    private List<String> logs;

    public Console(){
        logs = new ArrayList<>();
    }

    public String getConsoleText(){
        StringBuilder sb = new StringBuilder();
        for (int t = logs.size()-1; t >= 0; t--){
            sb.append(logs.get(t));
            if (!logs.get(t).contains("0 matching lobbies")) {
                sb.append("\r\n---------------------------\r\n");
            } else {
                sb.append("\r\n");
            }
        }
        return sb.toString();
    }

    public void log(String message){
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        String record = "["+ ldt.format(dtf) + "] " + message;

        logs.add(record);

        while (logs.size() > Literals.CONSOLE_RECORDS_MAX){
            logs.remove(0);
        }
    }

    public void logLobby(Lobby lobby){
        StringBuilder sb = new StringBuilder();
        sb.append("Lobby: "+lobby.getTitle());
        sb.append(" Players: ");
        sb.append(lobby.getPlayers().stream().map(Player::getName).collect(Collectors.joining(", ")));
        log(sb.toString());
    }

    public void clear(){
        logs.clear();
    }
}
