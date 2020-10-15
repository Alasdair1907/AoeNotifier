package world.thismagical.filters;

import java.util.ArrayList;
import java.util.List;

public class FiltersContainer {

    private Integer id;

    public FiltersContainer(){
    }

    public FiltersContainer(List<FiltersContainer> existingContainers){
        Integer maxId = existingContainers.stream().mapToInt(FiltersContainer::getId).max().orElseGet(()-> -1);

        this.id = maxId+1;
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

        filter.setContainerId(this.id);
        filters.add(filter);
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
