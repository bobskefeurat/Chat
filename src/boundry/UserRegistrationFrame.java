package boundry;

import client.ClientLauncher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class UserRegistrationFrame extends JFrame {

    private JTextField textField;
    private JLabel imageLabel;
    private ImageIcon profilePicture;
    private ClientLauncher clientLauncher;
    private LoginFrame loginFrame;

    public UserRegistrationFrame(ClientLauncher clientLauncher, LoginFrame loginFrame) {
        this.clientLauncher = clientLauncher;
        this.loginFrame = loginFrame;

        initializeFrame();
        setupUIComponents();
        this.setVisible(false);
    }

    private void initializeFrame() {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
    }

    private void setupUIComponents() {
        setupInfoPanel();
        setupCenterPanel();
        setupButtonPanel();
    }

    private void setupInfoPanel() {
        JPanel infoPanel = createInfoPanel();
        getContentPane().add(infoPanel, BorderLayout.NORTH);
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2,1));

        JLabel nameLabel = new JLabel("Choose Username");
        infoPanel.add(nameLabel);

        textField = new JTextField();
        infoPanel.add(textField);

        return infoPanel;
    }

    private void setupCenterPanel() {
        JPanel centerPanel = createCenterPanel();
        getContentPane().add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());

        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(100, 100));
        centerPanel.add(imageLabel);

        return centerPanel;
    }

    private void setupButtonPanel() {
        JPanel buttonPanel = createButtonPanel();
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2,1));

        JButton selectImageButton = createButton("Choose Profile Picture", this::chooseProfilePictureAction);
        buttonPanel.add(selectImageButton);

        JButton createUserButton = createButton("Create User", this::createUserAction);
        buttonPanel.add(createUserButton);

        return buttonPanel;
    }

    private void chooseProfilePictureAction(java.awt.event.ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File chosenFile = fileChooser.getSelectedFile();
            try {
                ImageIcon imageIcon = new ImageIcon(ImageIO.read(chosenFile));
                Image image = imageIcon.getImage().getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(image));
                profilePicture = new ImageIcon(image);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void createUserAction(java.awt.event.ActionEvent e) {
        String username = textField.getText();
        if (username != null && profilePicture != null) {
            clientLauncher.setUsername(username);
            clientLauncher.setProfilePicture(profilePicture);
            loginFrame.setLogInTextFieldUsername(username);
            dispose();
        }
    }

    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        return button;
    }
}
