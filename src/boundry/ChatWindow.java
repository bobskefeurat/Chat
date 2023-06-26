package boundry;

import control.MessageManager;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class ChatWindow extends JFrame {

    private MessageManager messageManager;
    private ArrayList<String> receivers;
    private JPanel chatPanel;
    private JPanel leftPanel;
    private UserPanel userPanel;
    private JTextPane chatPane;
    private JTextField messageField;
    private JButton sendImageButton;
    private JButton addContactButton;
    private ImageIcon imageToSend;

    public ChatWindow(ArrayList<String> selectedUsers, MessageManager messageManager, UserPanel userPanel) {
        this.setTitle(getChatTitle(selectedUsers));
        this.messageManager = messageManager;
        this.userPanel = userPanel;
        receivers = selectedUsers;
        setUpChatWindow();
    }

    private String getChatTitle(ArrayList<String> selectedUsers) {
        return selectedUsers.size() == 1 ? "Chatting with " + selectedUsers.get(0) : String.join(", ", selectedUsers);
    }

    public void setUpChatWindow() {
        this.setSize(450, 300);
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        createLeftPanel();
        createChatPanel();
        createMessageField();
        createSendImageButton();
        createAddContactButton();
        addComponentsToWindow();
        this.setVisible(true);
    }

    private void createLeftPanel() {
        leftPanel = new JPanel();
        leftPanel.setBackground(new Color(0xBCBCBC));
        leftPanel.setPreferredSize(new Dimension(50, getHeight()));
        JLabel profilePictureLabel = new JLabel();
        profilePictureLabel.setBackground(Color.BLACK);
        profilePictureLabel.setOpaque(true);
        profilePictureLabel.setPreferredSize(new Dimension(50,50));

        ImageIcon profilePictureChatPartner = messageManager.getProfilePictureFromUser(receivers.get(0));
        Image image = profilePictureChatPartner.getImage();
        Image scaledImage = image.getScaledInstance(profilePictureLabel.getPreferredSize().width, profilePictureLabel.getPreferredSize().height, Image.SCALE_SMOOTH);
        profilePictureChatPartner = new ImageIcon(scaledImage);

        profilePictureLabel.setIcon(profilePictureChatPartner);
        leftPanel.add(profilePictureLabel);
    }

    private void createChatPanel() {
        chatPanel = new JPanel(new BorderLayout());
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatPane);
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);
    }

    private void createMessageField() {
        messageField = new JTextField();
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = "You: " + messageField.getText() + "\n";
                    chatPane.getDocument().insertString(chatPane.getDocument().getLength(), text, null);
                    sendMessage();
                    messageField.setText("");
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void createSendImageButton() {
        sendImageButton = new JButton("Choose Image");
        sendImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    imageToSend = new ImageIcon(selectedFile.getPath());
                    Image image = imageToSend.getImage();
                    Image scaledImage = image.getScaledInstance(50, 50, Image.SCALE_DEFAULT);
                    imageToSend = new ImageIcon(scaledImage);
                }
            }
        });
    }

    private void createAddContactButton() {
        addContactButton = new JButton("Add Contact");
        addContactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userPanel.getSelectedUsername();
                userPanel.addToContacts(username);
                messageManager.addUserContact(username);
            }
        });
    }

    private void addComponentsToWindow() {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(sendImageButton, BorderLayout.NORTH);
        buttonPanel.add(addContactButton, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(messageField, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        chatPanel.add(southPanel, BorderLayout.SOUTH);

        this.setLayout(new BorderLayout());
        this.add(leftPanel, BorderLayout.WEST);
        this.add(chatPanel, BorderLayout.CENTER);
    }

    public void sendMessage() throws BadLocationException {
        printReceivers();

        if (imageToSend != null) {
            chatPane.insertIcon(imageToSend);
            chatPane.getDocument().insertString(chatPane.getDocument().getLength(), "\n", null);
            messageManager.createMessage(messageField.getText(), receivers, imageToSend);
            imageToSend = null;
        } else {
            messageManager.createMessage(messageField.getText(), receivers, null);
        }
    }

    private void printReceivers() {
        System.out.println("Receivers on client side: ");
        for (String receiver : receivers) {
            System.out.println(receiver);
        }
        System.out.println(" ");
    }

    public void showMessage(String text, String sender, ImageIcon receivedImage) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    chatPane.getDocument().insertString(chatPane.getDocument().getLength(), sender + ": " + text + "\n", null);

                    if (receivedImage != null) {
                        chatPane.insertIcon(receivedImage);
                        chatPane.getDocument().insertString(chatPane.getDocument().getLength(), "\n", null);
                    }
                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
