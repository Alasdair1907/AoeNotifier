import java.util.ArrayList;
import java.util.List;

public class Lobby {

    Lobby() {
        this.players = new ArrayList<>();
    }

    public String title;
    public Integer numSlots;

    public Boolean turbo;
    public Integer pop;

    public List<Player> players;
}
