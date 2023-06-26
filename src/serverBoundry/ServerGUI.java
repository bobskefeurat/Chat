package serverBoundry;

import javax.swing.*;
import java.awt.*;

public class ServerGUI extends JFrame {

    private JTextArea serverTrafficArea;

    public ServerGUI() {
        super("Server Traffic");

        serverTrafficArea = new JTextArea(20, 50);
        serverTrafficArea.setLineWrap(true);
        serverTrafficArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(serverTrafficArea);

        serverTrafficArea.setEditable(false);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setVisible(true);
    }

    public void updateServerTraffic(String message) {
        SwingUtilities.invokeLater(() -> {
            serverTrafficArea.append(message + "\n");
            serverTrafficArea.setCaretPosition(serverTrafficArea.getDocument().getLength());
        });
    }
}
