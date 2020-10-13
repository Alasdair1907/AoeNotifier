import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class AoeNet {

    public List<Lobby> connectAndFetchLobbies() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(Literals.apiLobbiesLocation)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200){
            return null;
        }

        String data = response.body();

        if (data == null || data.isEmpty()){
            return null;
        }

        List<Lobby> lobbies = new ArrayList<>();
        JSONArray lobbiesJson = new JSONArray(data);

        for (int i = 0; i < lobbiesJson.length(); i++){
            JSONObject lobbyJson = lobbiesJson.getJSONObject(i);

            Lobby lobby = new Lobby();
            lobby.title = lobbyJson.getString("name");
            Integer numSlots = lobbyJson.getInt("num_slots");

            Boolean turbo = lobbyJson.getBoolean("turbo");
            Integer pop = lobbyJson.getInt("pop");

            JSONArray playersJson = lobbyJson.getJSONArray("players");
            for (int t = 0; t < playersJson.length(); t++){
                JSONObject playerJson = playersJson.getJSONObject(t);
                Player player = new Player();
                player.name = playerJson.getString("name");

                lobby.players.add(player);
            }

            lobbies.add(lobby);
        }

        return lobbies;
    }
}
