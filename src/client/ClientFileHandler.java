package client;

import entity.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ClientFileHandler {

    private Client client;
    private User clientUser;
    private ArrayList<User> contacts = new ArrayList<>();
    private String contactsFilePath;
    private String userFilePath;

    public ClientFileHandler(User clientUser, Client client) {
        this.clientUser = clientUser;
        this.client = client;
        this.contactsFilePath = "src/files/" + clientUser.getName() + "/contacts.ser";
        this.userFilePath = "src/files/" + clientUser.getName() + "/User.ser";
        setUpFiles();
    }

    public void setUpFiles() {
        try {
            Path path = Paths.get("src/files/" + clientUser.getName());
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                writeUserFile();
                writeContactsFile();
            } else {
                readUserFile();
                readContactsFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeContactsFile() {
        System.out.println(" ");
        System.out.println("In writeContactsFile: ");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(contactsFilePath))) {

            for (User user : contacts) {
                System.out.println("User object: " + user + " User name: " + user.getName());
            }
            out.writeObject(contacts);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readContactsFile() {
        System.out.println(" ");
        System.out.println("In readContactsFile: ");
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(contactsFilePath))) {

            contacts = (ArrayList<User>) in.readObject();

            for (User user : contacts) {
                System.out.println("User object: " + user + " User name: " + user.getName());
            }

            setContacts();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void writeUserFile() {
        System.out.println(" ");
        System.out.println("In writeUserFile: ");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(userFilePath))) {
            out.writeObject(clientUser);
            System.out.println("User object: " + clientUser + " User name: " + clientUser.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readUserFile() {
        System.out.println(" ");
        System.out.println("In readUserFile:");
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(userFilePath))) {
            clientUser = (User) in.readObject();
            System.out.println("User object: " + clientUser + " User name: " + clientUser.getName());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void storeContact(User contact) {
        contacts.add(contact);
        writeContactsFile();
    }

    public ArrayList<String> getContactNames() {
        ArrayList<String> contactNames = new ArrayList<>();
        for (User user : contacts) {
            contactNames.add(user.getName());
        }
        return contactNames;
    }

    public User getClientUser() {
        return clientUser;
    }

    public ArrayList<User> getUserContacts() {
        return contacts;
    }

    public void setContacts() {
        client.setContacts(contacts);
    }
}
