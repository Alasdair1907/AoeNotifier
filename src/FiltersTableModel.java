import filters.Filter;
import filters.FiltersContainer;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class FiltersTableModel extends AbstractTableModel {
    private List<String> columnNames = List.of("Filter type", "Predicate type", "Search predicate");
    private List<FiltersContainer> filtersContainers;

    @Override
    public int getColumnCount(){
        return columnNames.size();
    }

    @Override
    public int getRowCount(){
        return Long.valueOf(filtersContainers.stream().map(filtersContainer -> filtersContainer.getFilters().size()).count()).intValue();
    }

    @Override
    public Object getValueAt(int row, int column){
        List<Filter> filters = filtersContainers.stream().flatMap(filter -> filter.getFilters().stream()).collect(Collectors.toList());
        Filter filter = filters.get(row);
        return filter.getColumn(column);
    }

    @Override
    public Class<?> getColumnClass(int column){
        return Filter.getClass(column);
    }

    @Override
    public String getColumnName(int column){
        return columnNames.get(column);
    }

    public List<FiltersContainer> getFiltersContainers() {
        return filtersContainers;
    }

    public void setFiltersContainers(List<FiltersContainer> filtersContainers) {
        this.filtersContainers = filtersContainers;
    }
}
