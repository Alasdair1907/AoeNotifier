package filters;

import java.util.ArrayList;
import java.util.List;

public class FiltersContainer {

    public FiltersContainer(){
        this.filters = new ArrayList<>();
    }

    private List<Filter> filters;

    public List<Filter> getFilters(){
        return (filters == null) ? new ArrayList<>() : filters;
    }

    public void addFilter(Filter filter){
        if (filters == null){
            filters = new ArrayList<>();
        }

        filters.add(filter);
    }

    public void setFilters(List<Filter> filters){
        this.filters = filters;
    }
}
