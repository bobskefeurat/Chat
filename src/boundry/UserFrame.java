package boundry;

import control.MessageManager;

import javax.swing.*;
import java.util.ArrayList;

public class UserFrame extends JFrame {

    private int width;
    private int height;
    private UserPanel userPanel;
    private MessageManager messageManager;

    public UserFrame(int width, int height, MessageManager messageManager, String username) {
        super(username);
        this.width = width;
        this.height = height;
        this.messageManager = messageManager;
        initialize();
    }

    private void initialize() {
        setFrameProperties();
        createUserPanel();
    }

    private void setFrameProperties() {
        this.setResizable(false);
        this.setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void createUserPanel() {
        this.userPanel = new UserPanel(width, height, messageManager, this);
        this.setContentPane(userPanel);
        this.setVisible(true);
    }

    public void populateList(ArrayList<String> onlineUsers) {
        userPanel.populateList(onlineUsers);
    }

    public void findConversation(String text, String sender, ImageIcon imageIcon) {
        userPanel.findConversation(text, sender, imageIcon);
    }

    public boolean doesChatWindowExist(String senderName) {
        return userPanel.doesChatWindowExist(senderName);
    }
}
