package world.thismagical;

import world.thismagical.filters.Filter;
import world.thismagical.filters.FiltersContainer;
import world.thismagical.filters.Helpers;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class FiltersTableModel extends AbstractTableModel {

    private List<FiltersContainer> filtersContainers;

    @Override
    public int getColumnCount(){
        return Filter.getColumnCount();
    }

    @Override
    public int getRowCount(){
        return Helpers.getFlatFilters(filtersContainers).size();
    }

    @Override
    public Object getValueAt(int row, int column){
        List<Filter> filters = Helpers.getFlatFilters(filtersContainers);
        Filter filter = filters.get(row);
        return filter.getColumn(column);
    }

    @Override
    public Class<?> getColumnClass(int column){
        return Filter.getClass(column);
    }

    @Override
    public String getColumnName(int column){
        return Filter.getColumnName(column);
    }

    public List<FiltersContainer> getFiltersContainers() {
        return filtersContainers;
    }

    public void setFiltersContainers(List<FiltersContainer> filtersContainers) {
        this.filtersContainers = filtersContainers;
    }
}
