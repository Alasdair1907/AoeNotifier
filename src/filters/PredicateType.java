package filters;

public enum PredicateType {
    FULL_MATCH("Full match"),
    CONTAINS("Contains"),
    ALL_WORDS("All words");

    private String buttonText;

    PredicateType(String buttonText){
        this.buttonText = buttonText;
    }

    public static PredicateType getByText(String buttonText){
        for (PredicateType predicateType : PredicateType.values()){
            if (predicateType.buttonText.equals(buttonText)){
                return predicateType;
            }
        }
        return null;
    }
}
