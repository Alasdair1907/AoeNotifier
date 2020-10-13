import filters.Filter;
import filters.FilterType;
import filters.FiltersContainer;
import filters.PredicateType;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;


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

        List<FiltersContainer> filtersContainers = new ArrayList<>();
        FiltersTableModel filtersTableModel = new FiltersTableModel();
        filtersTableModel.setFiltersContainers(filtersContainers);
        filtersTable.setModel(filtersTableModel);

        // process "Save filter" click
        saveFilterButton.addActionListener(actionEven -> {
            FilterType filterType = FilterType.getByText(filterTypeRadios.stream().filter(JRadioButton::isSelected).findAny().get().getText());
            PredicateType predicateType = PredicateType.getByText(predicateTypeRadios.stream().filter(JRadioButton::isSelected).findAny().get().getText());
            String predicate = textPredicateField.getText();

            if (predicate.isBlank()){
                JOptionPane.showMessageDialog(null, "Predicate text can not be empty!");
                return;
            }

            Filter filter = new Filter();
            filter.setFilterType(filterType);
            filter.setPredicateType(predicateType);
            filter.setPredicate(predicate);

            FiltersContainer filtersContainer = new FiltersContainer();
            filtersContainer.addFilter(filter);

            filtersContainers.add(filtersContainer);

            // refresh the table
            FiltersTableModel newTableModel = new FiltersTableModel();
            newTableModel.setFiltersContainers(filtersContainers);
            filtersTable.setModel(newTableModel);
        });

        mainFrame.setResizable(false);
        mainFrame.setLocationByPlatform(true);
        mainFrame.setDefaultLookAndFeelDecorated(true);
        mainFrame.setVisible(true);

        setNimbus();
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
