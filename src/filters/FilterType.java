package filters;

public enum FilterType {

    LOBBY_TITLE("Lobby title"),
    PLAYER_NAME("Player name");

    private String buttonText;

    FilterType(String buttonText){
        this.buttonText = buttonText;
    }

    public static FilterType getByText(String buttonText){
        for (FilterType filterType : FilterType.values()){
            if (filterType.buttonText.equals(buttonText)){
                return filterType;
            }
        }
        return null;
    }
}
