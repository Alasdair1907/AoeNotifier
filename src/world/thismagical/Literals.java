package world.thismagical;

public class Literals {
    public static final String apiLobbiesLocation = "https://aoe2.net/api/lobbies";

    public static final String WINDOW_TITLE = "Age of Empires 2 DE Game Notifier";

    public static final String COLUMN_FILTER_TYPE = "Filter type";
    public static final String COLUMN_PREDICATE_TYPE = "Predicate type";
    public static final String COLUMN_TEXT_MODE = "Text mode";
    public static final String COLUMN_SEARCH_PREDICATE = "Search predicate";

    public static final String NOTIFICATION_TITLE = "Age of Empires 2 Lobbies";


    // texts for UI elements that get changed on the go
    public static final String ACTIVITY_LABEL_ACTIVE = "ACTIVE";
    public static final String ACTIVITY_LABEL_INACTIVE = "INACTIVE";
    public static final String START_MONITORING_BUTTON = "Start monitoring";
    public static final String STOP_MONITORING_BUTTON = "Stop monitoring";

    public static final String FILTERS_FILENAME = ".aoe2NotifierFilters";

    public static final Integer AOE_NET_REFRESH_RATE_SECONDS = 30;
    public static final Integer CONSOLE_RECORDS_MAX = 15;

    public static final String HELP = """
Create filters and click "Start monitoring". The program will scan all AOE2 DE lobbies and notify the user about the lobbies that match the filters.

Filter type:
Determines whether this filter will be looking at the lobby title or at player names inside the lobby.

Predicate type:
"Full match" means that the lobby title/player name will have to match the input text exactly, e.g. "Forest Nothing" will match "Forest Nothing" but will NOT match "Forest Nothing 3v3".
"Contains" means that if the input text is found within the lobby title/player name, it will match, e.g. if the input is "Forest Nothing" it will match "Forest Nothing 3v3", because the latter contains the input text.
"All words" means that the input text is dissected into words, and each word is looked for in the lobby title/player name,
e.g. if the input is "Forest Nothing" it will match "Forest & Nothing 3v3", as both words "Forest" and "Nothing" are contained in the title.

Text Mode:
Determines, whether the matching is case sensitive or not - "Forest Nothing" will NOT be matched to "forest nothing" if the filter is set to Case sensitive, it will be matched otherwise.

Join Selected filter (AND):
Joins the filters, which means, that for a match to occur, the lobby in question will have to match ALL CONDITIONS INSIDE THE JOINED FILTER.

Reset Notifications History:
The program remembers which lobbies it has notified the user about to avoid producing duplicate notifications. You can reset that memory using this button.

Example usage:
If you want to get notified whenever there is a lobby that has words "forest" and "nothing" in its title AND you want to make sure that there is a player from clan [FN] in it, you need to create to filters and join them:
First create filter for lobby, so set 'Filter type' to 'Lobby title', set 'Predicate type' to 'All words', text mode to 'Case insensitive', put "forest nothing" into the text field and click 'Save filter'.
Then, create filter for the player - set 'Filter type' to 'Player name', set 'Predicate type' to 'Contains', text mode to 'Case insensitive', put "[FN]" into the text field and save the filter.
Now join these filters, so that the lobby will have to match both conditions - select newly created filters using mouse and Ctrl, and click "Join selected filters (AND)" button.
Now you can start monitoring.
-----
Latest version should be available at https://thismagical.world/aoe-notifier/
Coded by Alasdair (alasdair1907@gmail.com)
    """;
}
