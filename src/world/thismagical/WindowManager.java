package world.thismagical;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jshell.spi.ExecutionControlProvider;
import world.thismagical.filters.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;


public class WindowManager {
    private JPanel mainPanel;
    private JButton deleteSelectedFiltersButton;
    private JTable filtersTable;
    private JRadioButton lobbyTitleRadioButton;
    private JRadioButton playerNameRadioButton;
    private JRadioButton fullMatchRadioButton;
    private JRadioButton containsRadioButton;
    private JPanel basePanel;
    private JPanel radioContainer;
    private JPanel filterSettingsLabels;
    private JPanel addNewFilterPanel;
    private JButton saveFilterButton;
    private JTextField textPredicateField;
    private JPanel currentFilterPanel;
    private JPanel actionPanel;
    private JButton startMonitoringButton;
    private JRadioButton allWordsRadioButton;
    private JButton joinSelectedFiltersANDButton;

    private ButtonGroup filterTypeGroup;
    private ButtonGroup predicateTypeGroup;

    List<JRadioButton> filterTypeRadios;
    List<JRadioButton> predicateTypeRadios;


    public void createAndShowGUI() throws Exception {

        JFrame mainFrame = new JFrame(Literals.WINDOW_TITLE);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 700);

        mainFrame.getContentPane().add(basePanel);

        // group radio buttons
        filterTypeRadios = List.of(lobbyTitleRadioButton, playerNameRadioButton);
        predicateTypeRadios = List.of(fullMatchRadioButton, containsRadioButton, allWordsRadioButton);

        filterTypeGroup = new ButtonGroup();
        filterTypeRadios.forEach(b->filterTypeGroup.add(b));

        predicateTypeGroup = new ButtonGroup();
        predicateTypeRadios.forEach(b->predicateTypeGroup.add(b));

        // select first options for each group by default
        filterTypeRadios.get(0).setSelected(true);
        predicateTypeRadios.get(0).setSelected(true);

        // initialize table
        List<FiltersContainer> filtersContainers = loadFiltersFromFile();
        refreshTable(filtersContainers);

        // process "Save filter" click
        saveFilterButton.addActionListener(actionEven -> {
            FilterType filterType = FilterType.getByText(filterTypeRadios.stream().filter(JRadioButton::isSelected).findAny().get().getText());
            PredicateType predicateType = PredicateType.getByText(predicateTypeRadios.stream().filter(JRadioButton::isSelected).findAny().get().getText());
            String predicate = textPredicateField.getText();

            if (predicate.isBlank()){
                JOptionPane.showMessageDialog(null, "Predicate text can not be empty!");
                return;
            }

            // add the new filter
            Filter filter = new Filter();
            filter.setFilterType(filterType);
            filter.setPredicateType(predicateType);
            filter.setPredicate(predicate);

            FiltersContainer filtersContainer = new FiltersContainer(filtersContainers);
            filtersContainer.addFilter(filter);

            filtersContainers.add(filtersContainer);
            refreshTable(filtersContainers);
            saveFiltersToFile(filtersContainers);
        });

        // process "Delete selected filters" click
        deleteSelectedFiltersButton.addActionListener(actionEvent -> {
            int[] selectedRows = filtersTable.getSelectedRows();

            if (selectedRows.length == 0){
                JOptionPane.showMessageDialog(null, "No filters selected for deletion");
                return;
            }

            for (int row : selectedRows){
                Helpers.removeFilter(row, filtersContainers);
            }

            refreshTable(filtersContainers);
        });

        // process "Join selected filters" click
        joinSelectedFiltersANDButton.addActionListener(actionEvent -> {
            int[] selectedRows = filtersTable.getSelectedRows();

            if (selectedRows.length < 2){
                JOptionPane.showMessageDialog(null, "Select at least 2 filters to join");
                return;
            }

            List<Integer> filterIds = Arrays.stream(selectedRows).boxed().collect(Collectors.toList());
            Helpers.joinFilters(filterIds, filtersContainers);
            refreshTable(filtersContainers);
        });

        mainFrame.setResizable(false);
        mainFrame.setLocationByPlatform(true);
        mainFrame.setDefaultLookAndFeelDecorated(true);
        mainFrame.setVisible(true);

        setNimbus();
    }

    private void refreshTable(List<FiltersContainer> filtersContainers){
        FiltersTableModel newTableModel = new FiltersTableModel();
        newTableModel.setFiltersContainers(filtersContainers);
        filtersTable.setModel(newTableModel);

        filtersTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
                Integer containerId = Helpers.getFilterContainerIdByRowNum(row, filtersContainers);
                Integer containerIndex = filtersContainers.indexOf(filtersContainers.stream().filter(container -> container.getId().equals(containerId)).findFirst().get());

                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected){
                    c.setBackground(new Color(16, 122, 109));
                } else {
                    c.setBackground(containerIndex % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE);
                }
                return c;
            }
        });
    }

    private void saveFiltersToFile(List<FiltersContainer> filtersContainers) {
        try {
            String homeDir = System.getProperty("user.home");

            ObjectMapper objectMapper = new ObjectMapper();
            String filtersContainersSerialized = objectMapper.writeValueAsString(filtersContainers);

            String path = homeDir + "/" + Literals.FILTERS_FILENAME;

            File file = new File(path);
            if (file.exists()){
                file.delete();
            }

            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write(filtersContainersSerialized);
            fileWriter.close();

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private List<FiltersContainer> loadFiltersFromFile(){
        try {
            String homeDir = System.getProperty("user.home");
            ObjectMapper objectMapper = new ObjectMapper();
            String path = homeDir + "/" + Literals.FILTERS_FILENAME;

            File filtersFile = new File(path);
            if (!filtersFile.exists()){
                return new ArrayList<>();
            }
            Scanner scanner = new Scanner(filtersFile);
            String filtersContainersSerialized = scanner.nextLine();

            if (filtersContainersSerialized == null || filtersContainersSerialized.isBlank()){
                return new ArrayList<>();
            }

            return objectMapper.readValue(filtersContainersSerialized, new TypeReference<List<FiltersContainer>>(){});
        } catch (Exception ex){
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void setNimbus() throws Exception {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }

        for (Window window : Window.getWindows())
        {
            SwingUtilities.updateComponentTreeUI(window);
        }
    }
}
