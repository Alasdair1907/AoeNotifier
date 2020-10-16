package world.thismagical.filters;


public enum TextMode {
    CASE_SENSITIVE("Case sensitive"),
    CASE_INSENSITIVE("Case insensitive");

    private String buttonText;

    TextMode(String buttonText){
        this.buttonText = buttonText;
    }

    public static TextMode getByText(String buttonText){
        for (TextMode textMode : TextMode.values()){
            if (textMode.buttonText.equals(buttonText)){
                return textMode;
            }
        }
        return null;
    }
}
