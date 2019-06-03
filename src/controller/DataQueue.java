package controller;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataQueue {

    private LinkedBlockingQueue<Object> list = new LinkedBlockingQueue<>(10);
    
    private static final DataQueue dataQueue = new DataQueue();
    private DataQueue(){};
    
    public static DataQueue getInstance() {
        return dataQueue;
    }    
    
    public boolean offer(byte [] bytes) {
        return list.offer(bytes);
    }
    
    public byte[] poll() {
        return (byte[]) list.poll();
    }
    
    public byte [] take() {
        try {
            return (byte[]) list.take();
        } catch (InterruptedException ex) {
            Logger.getLogger(DataQueue.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
}
