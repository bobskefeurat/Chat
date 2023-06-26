package server;

import entity.*;
import serverBoundry.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private ServerSocket serverSocket;
    private HashMap<User, ClientHandler> clients = new HashMap<>();
    private ConcurrentHashMap<User, ArrayList<Message>> messagesToBeDelivered = new ConcurrentHashMap<>();
    private ArrayList<User> onlineUsers = new ArrayList<>();
    private ArrayList<User> registeredUsers = new ArrayList<>();
    private ServerGUI serverGUI;
    private ServerFileHandler serverFileHandler;

    public Server() {
        serverGUI = new ServerGUI();
        serverFileHandler = new ServerFileHandler("server_log.txt");
        populateInitialServerLog();
    }

    private void populateInitialServerLog() {
        List<String> logMessages = serverFileHandler.readLog();
        for (String message : logMessages) {
            serverGUI.updateServerTraffic(message);
        }
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(6556);
            waitForClients();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void waitForClients() throws IOException {
        while (true) {
            Socket clientSocket = serverSocket.accept();
            createAndStartNewClientHandler(clientSocket);
        }
    }

    private void createAndStartNewClientHandler(Socket clientSocket) {
        ClientHandler clientHandler = new ClientHandler(clientSocket);
        clientHandler.start();
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private User clientUser;
        private ObjectOutputStream oos;
        private ObjectInputStream ois;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                setupClientHandler();
                handleIncomingMessages();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e);
            } finally {
                disconnectClient();
            }
        }

        private void setupClientHandler() throws IOException, ClassNotFoundException {
            setupStreams();
            handleClientUser();
            handleOfflineMessages(clientUser);
        }

        private void setupStreams() throws IOException {
            ois = new ObjectInputStream(clientSocket.getInputStream());
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
        }

        private void handleClientUser() throws IOException, ClassNotFoundException {
            clientUser = (User) ois.readObject();
            updateUserLists();
            updateAllClients();
        }

        private void updateUserLists() {
            synchronized (clients) {
                clients.put(clientUser, this);
            }
            synchronized (onlineUsers) {
                onlineUsers.add(clientUser);
            }
        }

        private void handleIncomingMessages() throws IOException, ClassNotFoundException {
            while(!clientSocket.isClosed()) {
                Message message = (Message) ois.readObject();
                handleMessage(message);
            }
        }

        private void handleMessage(Message message) throws IOException {
            System.out.println(" ");
            System.out.println("Message received on server: " + message.getText());

            updateMessageWithServerTimestamp(message);
            routeMessage(message);

            serverGUI.updateServerTraffic(message.toString());
            serverFileHandler.appendToLog(message.toString());
        }

        private void updateMessageWithServerTimestamp(Message message) {
            Date timestampServer = new Date();
            message.setTimeStampServer(timestampServer);
        }

        private void disconnectClient() {
            closeResources();
            removeClientFromServer();
            updateAllClients();
        }

        private void closeResources() {
            try {
                if (ois != null) ois.close();
                if (oos != null) oos.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(" ");
            System.out.println("[CLIENT DISCONNECTED]");
        }

        private void removeClientFromServer() {
            clients.remove(clientUser);
            onlineUsers.remove(clientUser);
        }

        public void sendOnlineUsers() {
            try {
                oos.reset();
                oos.writeObject(onlineUsers);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleOfflineMessages(User clientUser) throws IOException {
        System.out.println(" ");
        System.out.println("handleOfflineMessages, clientUser is: " + clientUser);
        System.out.println("Contains clientUserKey: " + messagesToBeDelivered.containsKey(clientUser));
        System.out.println(" ");

        if (messagesToBeDelivered.containsKey(clientUser)) {
            sendStoredMessagesToClient(clientUser);
        }
    }

    private void sendStoredMessagesToClient(User clientUser) throws IOException {
        ArrayList<Message> messages = new ArrayList<>(messagesToBeDelivered.get(clientUser));
        for (Message message : messages) {
            System.out.println("Message in Offline Messages: " + message.getText());
            routeMessage(message);
            messagesToBeDelivered.get(clientUser).remove(message);
        }
    }

    public void routeMessage(Message message) throws IOException {
        ArrayList<User> receivers = message.getReceivers();

        System.out.println(" ");
        System.out.println("In routeMessage, all receivers: ");
        for (User receiver : receivers) {
            System.out.println(receiver + " Receivername: " + receiver.getName());
        }

        distributeMessageToReceivers(message, receivers);
    }

    private void distributeMessageToReceivers(Message message, ArrayList<User> receivers) throws IOException {
        for (User receiver : receivers) {
            if (onlineUsers.contains(receiver)) {
                clients.get(receiver).oos.writeObject(message);
                clients.get(receiver).oos.flush();
            } else {
                storeMessageForOfflineReceiver(message, receiver);
            }
        }
    }

    private void storeMessageForOfflineReceiver(Message message, User receiver) {
        System.out.println(" ");
        System.out.println("In routeMessage, client offline: " + receiver.getName() + "Message: " + message.getText());

        messagesToBeDelivered.putIfAbsent(receiver, new ArrayList<>());
        messagesToBeDelivered.get(receiver).add(message);
    }

    private void updateAllClients() {
        synchronized (clients) {
            for (ClientHandler handler : clients.values()) {
                handler.sendOnlineUsers();
            }
        }

        System.out.println(" ");
        System.out.println("ONLINE USERS: ");
        for (User user : onlineUsers) {
            System.out.println(user.getName() + " object: " + user);
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
}
