package boundry;

import client.ClientLauncher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginFrame {

    private JFrame frame;
    private JTextField textField;
    private ClientLauncher clientLauncher;
    private UserRegistrationFrame userRegistrationFrame;

    public LoginFrame(ClientLauncher clientLauncher) {
        this.clientLauncher = clientLauncher;
        createLoginFrame();
        frame.setVisible(true);
    }

    private void createLoginFrame() {
        frame = new JFrame();
        setFrameProperties();
        addComponentsToFrame();
    }

    private void setFrameProperties() {
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new GridBagLayout());
    }

    private void addComponentsToFrame() {
        GridBagConstraints gbc = createGridBagConstraints();
        addUsernameLabelToFrame(gbc);
        addUsernameFieldToFrame(gbc);
        addRegisterButtonToFrame(gbc);
        addLogInButtonToFrame(gbc);
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        return gbc;
    }

    private void addUsernameLabelToFrame(GridBagConstraints gbc) {
        JLabel usernameLabel = new JLabel("Enter Username");
        usernameLabel.setHorizontalAlignment(JLabel.CENTER);
        frame.getContentPane().add(usernameLabel, gbc);
    }

    private void addUsernameFieldToFrame(GridBagConstraints gbc) {
        textField = new JTextField();
        textField.setPreferredSize(new Dimension(frame.getWidth()/3, frame.getHeight()/9));
        frame.getContentPane().add(textField, gbc);
    }

    private void addRegisterButtonToFrame(GridBagConstraints gbc) {
        JButton registerUserButton = createButton("Register User", e -> openRegistrationFrame());
        registerUserButton.setPreferredSize(new Dimension(frame.getWidth()/3, frame.getHeight()/9));
        frame.getContentPane().add(registerUserButton, gbc);
    }

    private void openRegistrationFrame() {
        userRegistrationFrame = new UserRegistrationFrame(clientLauncher, this);
        userRegistrationFrame.setVisible(true);
    }

    private void addLogInButtonToFrame(GridBagConstraints gbc) {
        JButton logInButton = createButton("LOG IN", e -> loginUser());
        logInButton.setPreferredSize(new Dimension(frame.getWidth()/3, frame.getHeight()/9));
        frame.getContentPane().add(logInButton, gbc);
    }

    private void loginUser() {
        if (textField.getText() != null) {
            clientLauncher.setUsername(textField.getText());
            frame.dispose();
            clientLauncher.connectToServer();
        }
    }

    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        return button;
    }

    public void setLogInTextFieldUsername(String username) {
        textField.setText(username);
    }
}
