package world.thismagical.filters;

import world.thismagical.filters.Filter;
import world.thismagical.filters.FiltersContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Helpers {
    public static List<Filter> getFlatFilters(List<FiltersContainer> filtersContainers){
        return filtersContainers.stream().flatMap(filter -> filter.getFilters().stream()).collect(Collectors.toList());
    }

    public static Integer getFilterContainerIdByRowNum(Integer row, List<FiltersContainer> filtersContainers){
        return getFlatFilters(filtersContainers).get(row).getContainerId();
    }

    public static FiltersContainer getContainerById(Integer id, List<FiltersContainer> filtersContainers){
        return filtersContainers.stream().filter(filtersContainer -> filtersContainer.getId().equals(id)).findAny().orElse(null);
    }

    public static void removeFilter(Integer rowId, List<FiltersContainer> containers){
        List<Filter> filterRows = getFlatFilters(containers);
        String guidToDelete = filterRows.get(rowId).getGuid();

        for (FiltersContainer filtersContainer : containers){
            filtersContainer.getFilters().removeIf(filter -> guidToDelete.equals(filter.getGuid()));
        }

        containers.removeIf(filtersContainer -> filtersContainer.getFilters().isEmpty());
    }

    public static void joinFilters(List<Integer> rowIdsToJoin, List<FiltersContainer> containers){
        // get filters we want to join
        FiltersContainer newContainer = new FiltersContainer(containers);
        List<Filter> filterRows = getFlatFilters(containers);
        for (int i = 0; i < filterRows.size(); i++){
            if (rowIdsToJoin.contains(i)){
                newContainer.addFilter(filterRows.get(i));
            }
        }

        // yank them out of their containers
        List<String> filterGuids = newContainer.getFilters().stream().map(Filter::getGuid).collect(Collectors.toList());
        for (FiltersContainer filtersContainer : containers){
            List<Filter> filterList = filtersContainer.getFilters();
            for (int i = filterList.size()-1; i >= 0; i--){
                if (filterGuids.contains(filterList.get(i).getGuid())){
                    filterList.remove(i);
                }
            }
        }

        // add new container to the bottom
        containers.add(newContainer);

        // delete empty containers
        containers.removeIf(filtersContainer -> filtersContainer.getFilters().isEmpty());
    }

}
