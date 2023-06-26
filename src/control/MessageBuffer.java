package control;

import entity.Message;

import java.util.LinkedList;

public class MessageBuffer {
    private LinkedList<Message> buffer = new LinkedList<>();

    public synchronized void put(Message message) {
        buffer.addLast(message);
        notifyAll();
    }

    public synchronized Message get() throws InterruptedException {
        while(buffer.isEmpty()) {
            wait();
        }
        return buffer.removeFirst();
    }

    public int size() {
        return buffer.size();
    }
}
