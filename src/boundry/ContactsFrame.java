package boundry;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ContactsFrame extends JFrame {
    private JList<String> contactsList;
    private DefaultListModel<String> listModel;
    private UserPanel userPanel;

    public ContactsFrame(int width, int height, UserPanel userPanel) {
        super("Contacts");
        this.userPanel = userPanel;
        setUpContactsFrame(width, height);
        createContactsList();
        this.add(new JScrollPane(contactsList));
        this.setVisible(true);
    }

    private void setUpContactsFrame(int width, int height) {
        this.setSize(width, height / 2);
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }

    private void createContactsList() {
        listModel = new DefaultListModel<>();
        contactsList = new JList<>(listModel);
        contactsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        contactsList.addMouseListener(createContactsListMouseListener());
    }

    private MouseAdapter createContactsListMouseListener() {
        return new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    String chatPartner = contactsList.getSelectedValue();
                    handleChatWindowActions(chatPartner);
                }
            }
        };
    }

    private void handleChatWindowActions(String chatPartner) {
        if (userPanel.isChatWindowCreated(chatPartner)) {
            userPanel.openChatWindow(chatPartner);
        } else {
            try {
                userPanel.createChatWindow(chatPartner);
                userPanel.openChatWindow(chatPartner);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void updateContactsList(String contact) {
        if (!listModel.contains(contact)) {
            listModel.addElement(contact);
        }
    }
}
