import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
