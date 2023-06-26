package boundry;

import control.MessageManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class UserPanel extends JPanel {
    private int width;
    private int height;
    private JList<String> onlineUsernames;
    private DefaultListModel<String> listModel;
    private String chatPartner;
    private HashMap<String, ChatWindow> conversations = new HashMap<>();
    private ContactsFrame contactsFrame;
    private MessageManager messageManager;
    private UserFrame userFrame;

    public UserPanel(int width, int height, MessageManager messageManager, UserFrame userFrame) {
        this.width = width;
        this.height = height;
        this.messageManager = messageManager;
        this.userFrame = userFrame;

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setUp();
    }

    public void setUp() {
        setUpUserList();
        setUpContactsFrame();
        setUpSouthPanel();
    }

    private void setUpSouthPanel() {
        JPanel southPanel = createSouthPanel();
        this.add(southPanel);
    }

    private JPanel createSouthPanel() {
        JPanel southPanel = new JPanel();
        southPanel.setBounds(0,height * 2/3, width, height * 1/3);
        southPanel.setBackground(new Color(0x9F9F9F));
        southPanel.setLayout(null);

        JButton groupMessageButton = createGroupButton();
        JButton openContactsButton = createOpenContactsButton();
        groupMessageButton.setBounds(0,35,150,25);
        openContactsButton.setBounds(0,60,150,25);

        southPanel.add(groupMessageButton);
        southPanel.add(openContactsButton);

        JPanel profilePicturePanel = createProfilePicturePanel();
        southPanel.add(profilePicturePanel);

        return southPanel;
    }

    private JPanel createProfilePicturePanel() {
        JPanel profilePicturePanel = new JPanel(new BorderLayout());
        profilePicturePanel.setBounds(150,0,150, height*1/3);

        JLabel profilePictureLabel = createProfilePictureLabel();
        profilePicturePanel.add(profilePictureLabel);

        return profilePicturePanel;
    }

    private JLabel createProfilePictureLabel() {
        ImageIcon profilePicture = getScaledProfilePicture();

        JLabel profilePictureLabel = new JLabel();
        profilePictureLabel.setIcon(profilePicture);
        profilePictureLabel.setBorder(BorderFactory.createEmptyBorder());

        return profilePictureLabel;
    }

    private ImageIcon getScaledProfilePicture() {
        ImageIcon profilePicture = messageManager.getSender().getProfilePicture();

        Image image = profilePicture.getImage();
        Image newimg = image.getScaledInstance(150, height * 1/3, Image.SCALE_SMOOTH);
        profilePicture = new ImageIcon(newimg);

        return profilePicture;
    }

    public void setUpContactsFrame() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndPositionContactsFrame();
            }
        });
    }

    private void createAndPositionContactsFrame() {
        contactsFrame = new ContactsFrame(width, height, UserPanel.this);
        updateContactsFrame();
        positionContactsFrame();
        contactsFrame.setVisible(false);
    }

    private void updateContactsFrame() {
        ArrayList<String> contactNames = getContactNames();

        if (contactNames != null) {
            for (String contactName : contactNames) {
                contactsFrame.updateContactsList(contactName);
            }
        }
    }

    private void positionContactsFrame() {
        Point location = userFrame.getLocationOnScreen();
        int frameWidth = contactsFrame.getWidth();
        int frameHeight = contactsFrame.getHeight();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        if (location.y + userFrame.getHeight() + frameHeight > screenSize.height) {
            contactsFrame.setLocation(location.x, location.y - frameHeight);
        } else {
            contactsFrame.setLocation(location.x, location.y + userFrame.getHeight());
        }
    }

    private void setUpUserList() {
        listModel = new DefaultListModel<>();
        onlineUsernames = new JList<>(listModel);
        onlineUsernames.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        onlineUsernames.addMouseListener(createUserListListener());

        onlineUsernames.setSize(width, height * 2/3);
        this.add(new JScrollPane(onlineUsernames));
    }


    private MouseAdapter createUserListListener() {
        return new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                try {
                    handleDoubleClickOnUsername(evt);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private void handleDoubleClickOnUsername(MouseEvent evt) throws InterruptedException {
        if (evt.getClickCount() == 2) {
            String chatPartner = onlineUsernames.getSelectedValue();

            if (isChatWindowCreated(chatPartner)) {
                openChatWindow(chatPartner);
            } else {
                createChatWindow(chatPartner);
                openChatWindow(chatPartner);
            }


        }
    }

    public boolean isChatWindowCreated(String chatPartner) {
        return conversations.containsKey(chatPartner);
    }

    public void openChatWindow(String chatPartner) {
        if (conversations.containsKey(chatPartner)) {
            conversations.get(chatPartner).setVisible(true);
        }
    }

    public void createChatWindow(String chatPartner) throws InterruptedException {

        ArrayList<String> receivers = new ArrayList<>();
        receivers.add(chatPartner);

        ChatWindow chatWindow = new ChatWindow(receivers, messageManager, this);

        // Get the location of the existing window
        Point location = this.getLocationOnScreen();

        // Get screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Set the location of the new window to be to the right of the existing window
        int newX = location.x + this.getWidth();
        int newY = location.y;

        // Check if the new window would be off the screen and adjust if necessary
        if (newX + chatWindow.getWidth() > screenSize.width) {
            newX = location.x - chatWindow.getWidth();
        }

        chatWindow.setLocation(newX, newY);

        chatWindow.setVisible(false);
        conversations.put(chatPartner, chatWindow);

        messageManager.handleRecievedMessages(chatPartner);
    }



    private JButton createGroupButton() {
        JButton groupMessageButton = new JButton("Group Message");
        groupMessageButton.addMouseListener(createGroupButtonListener());
        return groupMessageButton;
    }

    private JButton createOpenContactsButton() {
        JButton openContactsButton = new JButton("Open Contacts");
        openContactsButton.addMouseListener(createOpenContactsButtonListener());
        return openContactsButton;
    }

    private MouseAdapter createGroupButtonListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openGroupChatWindow();
            }
        };
    }

    private void openGroupChatWindow() {
        ArrayList<String> selectedUsers = new ArrayList<>(onlineUsernames.getSelectedValuesList());
        ChatWindow chatWindow = new ChatWindow(selectedUsers, messageManager, this);
    }

    private MouseAdapter createOpenContactsButtonListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                contactsFrame.setVisible(true);
            }
        };
    }

    public void populateList(ArrayList<String> onlineUsersList) {
        for (String onlineUsername : onlineUsersList) {
            if (!listModel.contains(onlineUsername)) {
                listModel.addElement(onlineUsername);
                //createChatWindow(onlineUsername);
            }
        }
        for (int i = listModel.size() - 1; i >= 0; i--) {
            if (!onlineUsersList.contains(listModel.get(i))) {
                listModel.remove(i);
            }
        }

    }

    public String getSelectedUsername() {
        return onlineUsernames.getSelectedValue();
    }

    public void addToContacts(String selectedUsername) {
        contactsFrame.updateContactsList(selectedUsername);
    }

    public void findConversation(String text, String sender, ImageIcon imageIcon) {
        if (conversations.containsKey(sender)) {
            conversations.get(sender).showMessage(text, sender, imageIcon);
        }
    }

    public ArrayList<String> getContactNames() {
        return messageManager.getContactNames();
    }

    public boolean doesChatWindowExist(String senderName) {

        if (conversations.containsKey(senderName)) {
            return true;
        }

        return false;
    }
}
