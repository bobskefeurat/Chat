package client;

import boundry.UserFrame;
import control.MessageManager;
import control.UserManager;
import entity.Message;
import entity.User;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

    private Socket client;
    private Reader reader;
    private Writer writer;
    private UserFrame userFrame;
    private UserManager userManager;
    private MessageManager messageManager;
    private User clientUser;
    private ArrayList<User> onlineUsers = new ArrayList<>();
    private ClientFileHandler clientFileHandler;

    public Client(User clientUser) {
        initializeClient(clientUser);
        userManager = new UserManager();
    }

    private void initializeClient(User clientUser) {
        messageManager = new MessageManager(this);
        setupClientFileHandler(clientUser);
        userFrame = new UserFrame(300,400, messageManager, clientUser.getName());
    }

    private void setupClientFileHandler(User clientUser) {
        clientFileHandler = new ClientFileHandler(clientUser, this);
        clientFileHandler.getClientUser();
        setClientUser(clientFileHandler.getClientUser());
    }

    public void connectToServer() {
        try {
            setupClientConnection();
            initializeReaderAndWriter();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setupClientConnection() throws IOException {
        client = new Socket("127.0.0.1",6556);
    }

    private void initializeReaderAndWriter() {
        reader = new Reader();
        writer = new Writer();
        reader.start();
        writer.start();
    }

    public ArrayList<String> getContactNames() {
        return clientFileHandler.getContactNames();
    }

    public void showMessage(String text, String name, ImageIcon imageIcon) {
        reader.showMessage(text, name, imageIcon);
    }

    public ArrayList<User> getUserContacts() {
        return clientFileHandler.getUserContacts();
    }

    public void setContacts(ArrayList<User> contacts) {
        messageManager.setContacts(contacts);
    }

    private class Reader extends Thread {

        private Message message;

        @Override
        public void run() {
            handleIncomingData();
        }

        private void handleIncomingData() {
            try (ObjectInputStream ois = new ObjectInputStream(client.getInputStream())){
                while(true) {
                    processReceivedData(ois);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void processReceivedData(ObjectInputStream ois) throws IOException, ClassNotFoundException {
            Object object = ois.readObject();
            if (object instanceof ArrayList<?>) {
                onlineUsers = (ArrayList<User>)object;
                userFrame.populateList(userManager.createStringList(onlineUsers, clientUser));
            } else if (object instanceof Message) {
                handleReceivedMessage((Message)object);
            }
        }

        private void handleReceivedMessage(Message message) {
            if (userFrame.doesChatWindowExist(message.getSender().getName())) {
                showMessage(message.getText(), message.getSender().getName(), message.getImageIcon());
            } else {
                messageManager.addToMessageBuffer(message);
            }
        }

        public void showMessage(String text, String senderName, ImageIcon imageIcon) {
            userFrame.findConversation(text, senderName, imageIcon);
        }
    }

    private class Writer extends Thread {

        Message message = null;

        @Override
        public void run() {
            handleOutgoingData();
        }

        private void handleOutgoingData() {
            try (ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream())){

                deliverClientName(oos);

                synchronized(this) {
                    while(true) {
                        if (message == null) {
                            wait();
                        }
                        sendOutgoingMessage(oos);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void sendOutgoingMessage(ObjectOutputStream oos) throws IOException {
            oos.writeObject(message);
            oos.flush();
            message = null;
        }

        public void deliverClientName(ObjectOutputStream oos) throws IOException {
            oos.writeObject(clientUser);
            oos.flush();
        }

        public synchronized void setMessageToSend(Message message) {
            this.message = message;
            notify();
        }
    }

    public ArrayList<User> getOnlineUsers() {
        return onlineUsers;
    }

    public void setMessageToSend(Message message) {
        writer.setMessageToSend(message);
    }

    public void setClientUser(User clientUser) {
        this.clientUser = clientUser;
        messageManager.setSender(clientUser);
    }

    public void storeContact(User user) {
        clientFileHandler.storeContact(user);
    }
}
