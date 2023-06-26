package control;

import client.Client;
import entity.Message;
import entity.User;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MessageManager {

    private Client client;
    private User sender;
    private ArrayList<User> userContacts = new ArrayList<>();
    private HashMap<User, MessageBuffer> receivedMessages = new HashMap<>();


    public MessageManager(Client client) {
        this.client = client;
    }

    public void createMessage(String text, ArrayList<String> receiverNames, ImageIcon imageToSend) {
        ArrayList<User> receivers = new ArrayList<>();
        User receiverObject;

        System.out.println(" ");
        System.out.println("In createMessage, finding receiver object ");

        for (String receiver : receiverNames) {
            receiverObject = findReceiverInContacts(receiver);
            if (receiverObject == null) {
                receiverObject = findReceiverOnline(receiver);
            }
            if (receiverObject != null) {
                receivers.add(receiverObject);
            }
        }

        Date timestampClient = new Date();
        Message message = new Message(sender, receivers, text, imageToSend, timestampClient, null);
        client.setMessageToSend(message);
    }

    public User findReceiverInContacts(String receiver) {
        System.out.println(" ");
        System.out.println("In findReceiversInContacts, receiver name is: " + receiver);
        for (User user : userContacts) {
            System.out.println("Receiver object: " + user + " receiver name: " + user.getName());
            if (receiver.equals(user.getName())) {
                System.out.println("RECEIVER FOUND, Receiver object: " + user + " receiver name: " + user.getName());
                return user;
            }
        }
        return null;
    }


    public User findReceiverOnline(String receiver) {
        ArrayList<User> onlineUsers = client.getOnlineUsers();

        System.out.println(" ");
        System.out.println("In findReceiversOnline, receiver name is: " + receiver);

        for (User user : onlineUsers) {
            if (receiver.equals(user.getName())) {
                System.out.println("Receiver object: " + user + " receiver name: " + user.getName());
                return user;
            }
        }
        return null;
    }

    public void addUserContact(String username) {

        ArrayList<User> onlineUsers = client.getOnlineUsers();
        System.out.println(" ");
        System.out.println("in addUserContact: ");
        for (User user : onlineUsers) {
            if (username.equals(user.getName()) && !userContacts.contains(user)) {
                System.out.println("User object: " + user + " user name: " + user.getName());
                userContacts.add(user);
                storeContact(user);
            }
        }

    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void storeContact(User user) {
        client.storeContact(user);
    }

    public ArrayList<String> getContactNames() {
        return client.getContactNames();
    }

    public User getSender() {
        return sender;
    }

    public void addToMessageBuffer(Message message) {

        User sender = message.getSender();

        if (receivedMessages.containsKey(sender)) {
            receivedMessages.get(sender).put(message);
        } else {
            MessageBuffer messageBuffer = new MessageBuffer();
            messageBuffer.put(message);
            receivedMessages.put(sender, messageBuffer);
        }
    }

    public void handleRecievedMessages(String chatPartner) throws InterruptedException {

        MessageBuffer messages;


        for (User user : receivedMessages.keySet()) {

            if (user.getName().equals(chatPartner)) {
                messages = receivedMessages.get(user);

                for (int i = messages.size(); i > 0; i--) {
                    Message message = messages.get();
                    client.showMessage(message.getText(), message.getSender().getName(), message.getImageIcon());
                }
            }
        }
    }
    public void setContacts(ArrayList<User> userContacts) {
        this.userContacts = userContacts;
    }

    public ImageIcon getProfilePictureFromUser(String username) {

        User receiverObject;

        receiverObject = findReceiverInContacts(username);
        if (receiverObject == null) {
            receiverObject = findReceiverOnline(username);
        }

        return receiverObject.getProfilePicture();
    }


}
