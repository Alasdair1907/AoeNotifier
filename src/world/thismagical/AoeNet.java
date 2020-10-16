package world.thismagical;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class AoeNet {

    public static List<Lobby> connectAndFetchLobbies() throws Exception {
        List<Lobby> lobbies = new ArrayList<>();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(Literals.apiLobbiesLocation)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200){
            return lobbies;
        }

        String data = response.body();

        if (data == null || data.isEmpty()){
            return lobbies;
        }

        JSONArray lobbiesJson = new JSONArray(data);

        for (int i = 0; i < lobbiesJson.length(); i++){
            JSONObject lobbyJson = lobbiesJson.getJSONObject(i);

            Lobby lobby = new Lobby();
            lobby.id = lobbyJson.getLong("lobby_id");
            lobby.title = lobbyJson.getString("name");
            Integer numSlots = lobbyJson.getInt("num_slots");

            Boolean turbo = lobbyJson.getBoolean("turbo");
            Integer pop = lobbyJson.getInt("pop");

            JSONArray playersJson = lobbyJson.getJSONArray("players");
            for (int t = 0; t < playersJson.length(); t++){
                JSONObject playerJson = playersJson.getJSONObject(t);
                Player player = new Player();

                try {
                    player.name = playerJson.getString("name");
                } catch (Exception ex){
                    continue;
                }

                lobby.players.add(player);
            }

            lobbies.add(lobby);
        }

        return lobbies;
    }
}
