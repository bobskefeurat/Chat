package entity;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


public class Message implements Serializable {

    private String text;
    private ImageIcon imageIcon;
    private User sender;
    private ArrayList<User> recievers;
    private Date timeStampClient;
    private Date timeStampServer;
    private static final long serialVersionUID = 5466416500551139725L;

    public Message(User sender, ArrayList<User> recievers, String text, ImageIcon imageIcon, Date timeStampClient, Date timeStampServer) {
        this.sender = sender;
        this.recievers = recievers;
        this.text = text;
        this.imageIcon = imageIcon;
        this.timeStampClient = timeStampClient;
        this.timeStampServer = timeStampServer;
    }

    public User getSender(){
        return  sender;
    }

    public ArrayList<User> getReceivers(){
        return  recievers;
    }

    public String getText(){
        return text;
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public void setReceivers(ArrayList<User> receivers) {
        this.recievers = receivers;
    }

    public void setTimeStampClient(Date timeStampClient) {
        this.timeStampClient = timeStampClient;
    }

    public void setTimeStampServer(Date timeStampServer) {
        this.timeStampServer = timeStampServer;
    }

    public Date getTimeStampClient() {
        return timeStampClient;
    }

    public Date getTimeStampServer() {
        return timeStampServer;
    }

    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", imageIcon=" + imageIcon +
                ", sender=" + sender +
                ", recievers=" + recievers +
                ", timeStampClient=" + timeStampClient +
                ", timeStampServer=" + timeStampServer +
                '}';
    }
}
