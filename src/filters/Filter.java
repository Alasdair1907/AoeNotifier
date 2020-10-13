package filters;

public class Filter {
    private FilterType filterType;
    private PredicateType predicateType;

    private String predicate;

    public Object getColumn(Integer index){
        return switch (index){
            case 0 -> filterType;
            case 1 -> predicateType;
            case 2 -> predicate;
            default -> null;
        };
    }

    public static Class<?> getClass(Integer index){
        return switch (index){
            case 0 -> FilterType.class;
            case 1 -> PredicateType.class;
            case 2 -> String.class;
            default -> null;
        };
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }

    public PredicateType getPredicateType() {
        return predicateType;
    }

    public void setPredicateType(PredicateType predicateType) {
        this.predicateType = predicateType;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }
}
