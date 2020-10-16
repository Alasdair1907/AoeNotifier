package world.thismagical;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jshell.spi.ExecutionControlProvider;
import world.thismagical.filters.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
    private JPanel radioColumn1;
    private JRadioButton caseSensitiveRadioButton;
    private JPanel radioColumn2;
    private JRadioButton caseInsensitiveRadioButton;
    private JPanel radioColumn3;
    private JLabel activityLabel;
    private JTextPane consoleTextPane;
    private JButton helpButton;
    private JButton resetNotificationsHistoryButton;

    private ButtonGroup filterTypeGroup;
    private ButtonGroup predicateTypeGroup;
    private ButtonGroup textModeGroup;

    private List<JRadioButton> filterTypeRadios;
    private List<JRadioButton> predicateTypeRadios;
    private List<JRadioButton> textModeRadios;


    public void createAndShowGUI() throws Exception {

        JFrame mainFrame = new JFrame(Literals.WINDOW_TITLE);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 700);

        mainFrame.getContentPane().add(basePanel);

        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/resources/aoeNotifierLogo.gif"));
        mainFrame.setIconImage(image);

        activityLabel.setText(Literals.ACTIVITY_LABEL_INACTIVE);
        startMonitoringButton.setText(Literals.START_MONITORING_BUTTON);

        SystemTray tray = SystemTray.getSystemTray();
        TrayIcon trayIcon = new TrayIcon(image, Literals.WINDOW_TITLE);
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip(Literals.WINDOW_TITLE);
        tray.add(trayIcon);

        Console console = new Console();
        Set<Long> notifiedLobbies = new HashSet<>();
        Object lobbiesNotificationsLock = new Object();

        // group radio buttons
        filterTypeRadios = List.of(lobbyTitleRadioButton, playerNameRadioButton);
        predicateTypeRadios = List.of(fullMatchRadioButton, containsRadioButton, allWordsRadioButton);
        textModeRadios = List.of(caseSensitiveRadioButton, caseInsensitiveRadioButton);

        filterTypeGroup = new ButtonGroup();
        filterTypeRadios.forEach(b->filterTypeGroup.add(b));

        predicateTypeGroup = new ButtonGroup();
        predicateTypeRadios.forEach(b->predicateTypeGroup.add(b));

        textModeGroup = new ButtonGroup();
        textModeRadios.forEach(b->textModeGroup.add(b));

        // select first options for each group by default
        filterTypeRadios.get(0).setSelected(true);
        predicateTypeRadios.get(0).setSelected(true);
        textModeRadios.get(0).setSelected(true);

        // initialize table
        List<FiltersContainer> filtersContainers = loadFiltersFromFile();
        refreshTable(filtersContainers);

        // process "Save filter" click
        saveFilterButton.addActionListener(actionEven -> {
            synchronized (this) {
                FilterType filterType = FilterType.getByText(filterTypeRadios.stream().filter(JRadioButton::isSelected).findAny().get().getText());
                PredicateType predicateType = PredicateType.getByText(predicateTypeRadios.stream().filter(JRadioButton::isSelected).findAny().get().getText());
                TextMode textMode = TextMode.getByText(textModeRadios.stream().filter(JRadioButton::isSelected).findAny().get().getText());
                String predicate = textPredicateField.getText();

                if (predicate.isBlank()) {
                    JOptionPane.showMessageDialog(null, "Predicate text can not be empty!");
                    return;
                }

                // add the new filter
                Filter filter = new Filter();
                filter.setFilterType(filterType);
                filter.setPredicateType(predicateType);
                filter.setTextMode(textMode);
                filter.setPredicate(predicate);

                FiltersContainer filtersContainer = new FiltersContainer(filtersContainers);
                filtersContainer.addFilter(filter);

                filtersContainers.add(filtersContainer);
                refreshTable(filtersContainers);
                saveFiltersToFile(filtersContainers);
            }
        });

        // process "Delete selected filters" click
        deleteSelectedFiltersButton.addActionListener(actionEvent -> {
            synchronized (this) {
                int[] selectedRows = filtersTable.getSelectedRows();

                if (selectedRows.length == 0) {
                    JOptionPane.showMessageDialog(null, "No filters selected for deletion");
                    return;
                }

                Helpers.removeFilter(Arrays.stream(selectedRows).boxed().collect(Collectors.toList()), filtersContainers);
                refreshTable(filtersContainers);
                saveFiltersToFile(filtersContainers);
            }
        });

        // process "Join selected filters" click
        joinSelectedFiltersANDButton.addActionListener(actionEvent -> {
            synchronized (this) {
                int[] selectedRows = filtersTable.getSelectedRows();

                if (selectedRows.length < 2) {
                    JOptionPane.showMessageDialog(null, "Select at least 2 filters to join");
                    return;
                }

                List<Integer> filterIds = Arrays.stream(selectedRows).boxed().collect(Collectors.toList());
                Helpers.joinFilters(filterIds, filtersContainers);
                refreshTable(filtersContainers);
                saveFiltersToFile(filtersContainers);
            }
        });


        // monitoring
        AtomicReference<Boolean> monitoringIsActive = new AtomicReference<>();
        monitoringIsActive.set(Boolean.FALSE);

        Thread t = new Thread(() -> {
            try {
                while (true) {
                    if (!monitoringIsActive.get()) {
                        Thread.sleep(500);
                        continue;
                    }

                    List<Lobby> currentLobbies = AoeNet.connectAndFetchLobbies();
                    List<Lobby> matchingLobbies = Matcher.getMatchingLobbies(filtersContainers, currentLobbies);

                    synchronized (lobbiesNotificationsLock) {
                        if (!matchingLobbies.isEmpty()) {

                            String message = generateMessage(matchingLobbies, notifiedLobbies);
                            if (!message.isBlank()){
                                trayIcon.displayMessage(Literals.NOTIFICATION_TITLE, message, TrayIcon.MessageType.INFO);
                            }

                            matchingLobbies.forEach(lobby -> {
                                console.logLobby(lobby);
                                notifiedLobbies.add(lobby.getId());
                            });

                        } else {
                            console.log("0 matching lobbies of " + currentLobbies.size());
                        }

                        consoleTextPane.setText(console.getConsoleText());
                    }

                    Thread.sleep(Literals.AOE_NET_REFRESH_RATE_SECONDS * 1000);
                }
            } catch (Exception ex){
                ex.printStackTrace();
            }
        });
        t.start();

        // process "Start monitoring" button click
        startMonitoringButton.addActionListener(actionEvent -> {
            synchronized (this){
                if (monitoringIsActive.get()){
                    startMonitoringButton.setText(Literals.START_MONITORING_BUTTON);
                    activityLabel.setText(Literals.ACTIVITY_LABEL_INACTIVE);
                    monitoringIsActive.set(Boolean.FALSE);
                } else {
                    startMonitoringButton.setText(Literals.STOP_MONITORING_BUTTON);
                    activityLabel.setText(Literals.ACTIVITY_LABEL_ACTIVE);
                    monitoringIsActive.set(Boolean.TRUE);
                }
            }
        });

        // process "Reset Notifications History" button click
        resetNotificationsHistoryButton.addActionListener(actionEvent -> {
            synchronized (lobbiesNotificationsLock){
                notifiedLobbies.clear();
                consoleTextPane.setText("");
                console.clear();
            }
        });

        // process "Info" button click
        helpButton.addActionListener(actionEvent -> {
            JOptionPane.showMessageDialog(null, Literals.HELP);
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

        filtersTable.getColumnModel().getColumn(3).setMinWidth(120);

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

    public String generateMessage(List<Lobby> lobbies, Set<Long> ignoreLobbies){
        return lobbies.stream().filter(lobby-> !ignoreLobbies.contains(lobby.getId())).map(Lobby::getTitle).collect(Collectors.joining(", "));
    }

}
