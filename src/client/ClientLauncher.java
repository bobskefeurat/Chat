package client;

import boundry.LoginFrame;
import entity.User;

import javax.swing.*;
import java.awt.*;

public class ClientLauncher {

    public static LoginFrame loginFrame;
    public static String username;
    public static ImageIcon profilePicture;

    public static void main(String[] args) {

        ClientLauncher clientLauncher = new ClientLauncher();

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    loginFrame = new LoginFrame(clientLauncher);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void connectToServer() {

        User user = new User(username, profilePicture);
        Client client = new Client(user);
        client.connectToServer();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProfilePicture(ImageIcon profilePicture) {
        this.profilePicture = profilePicture;
    }

    public static String getUsername() {
        return username;
    }

    public static ImageIcon getProfilePicture() {
        return profilePicture;
    }
}
