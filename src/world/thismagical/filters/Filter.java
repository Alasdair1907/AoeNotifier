package world.thismagical.filters;

import world.thismagical.Literals;

import java.util.List;
import java.util.UUID;

public class Filter {

    public Filter(){
        this.guid = UUID.randomUUID().toString();
    }

    private Integer containerId;
    private String guid;
    private FilterType filterType;
    private PredicateType predicateType;
    private TextMode textMode;

    private String predicate;



    private static List<String> columnNames = List.of(Literals.COLUMN_FILTER_TYPE, Literals.COLUMN_PREDICATE_TYPE,
            Literals.COLUMN_TEXT_MODE, Literals.COLUMN_SEARCH_PREDICATE);

    public Object getColumn(Integer index){
        return switch (index){
            case 0 -> filterType;
            case 1 -> predicateType;
            case 2 -> textMode;
            case 3 -> predicate;
            default -> null;
        };
    }

    public static Integer getColumnByName(String name){
        return columnNames.indexOf(name);
    }

    public static String getColumnName(Integer col){
        return columnNames.get(col);
    }

    public static Integer getColumnCount(){
        return columnNames.size();
    }

    public static Class<?> getClass(Integer index){
        return switch (index){
            case 0 -> FilterType.class;
            case 1 -> PredicateType.class;
            case 2 -> TextMode.class;
            case 3 -> String.class;
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

    public TextMode getTextMode() {
        return textMode;
    }

    public void setTextMode(TextMode textMode) {
        this.textMode = textMode;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public Integer getContainerId() {
        return containerId;
    }

    public void setContainerId(Integer containerId) {
        this.containerId = containerId;
    }

    public String getGuid() {
        return guid;
    }
}
