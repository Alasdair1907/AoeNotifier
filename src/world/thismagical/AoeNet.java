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
            throw new RuntimeException("AOE2.NET HTTP STATUS CODE: "+response.statusCode());
        }

        String data = response.body();

        if (data == null || data.isEmpty()){
            return lobbies;
        }

        JSONArray lobbiesJson = new JSONArray(data);

        for (int i = 0; i < lobbiesJson.length(); i++){
            JSONObject lobbyJson = lobbiesJson.getJSONObject(i);

            Lobby lobby = new Lobby();

            try {
                lobby.id = lobbyJson.getLong("lobby_id");
                lobby.title = lobbyJson.getString("name");
            } catch (Exception ex){
                // these are crucial infos, we discard the lobby without them
                System.out.println("Lobby discarded due to parsing error");
                continue;
            }

            try {
                lobby.numSlots = lobbyJson.getInt("num_slots");
                lobby.turbo = lobbyJson.getBoolean("turbo");
                lobby.pop = lobbyJson.getInt("pop");
            } catch (Exception ex){
                // these are not crucial, ignore
                System.out.println("Lobby parsing error, ignored");
            }
            JSONArray playersJson;
            try {
                playersJson = lobbyJson.getJSONArray("players");
            } catch (Exception ex){
                System.out.println("Can not parse players array");
                continue;
            }

            for (int t = 0; t < playersJson.length(); t++){
                JSONObject playerJson;
                Player player = new Player();

                try {
                    playerJson = playersJson.getJSONObject(t);
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
