package world.thismagical;

import javax.swing.*;

// https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html
// https://stackoverflow.com/questions/13989265/task-tray-notification-balloon-events/14119801

public class AoeNotifier {

    public static void main(String[] argv){

        javax.swing.SwingUtilities.invokeLater( () -> {
            try {
                WindowManager windowManager = new WindowManager();
                windowManager.createAndShowGUI();
            } catch (Exception ex){
                JOptionPane.showMessageDialog(null, ex.toString());
            }
        });
    }
}
