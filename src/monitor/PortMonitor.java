package monitor;

import gnu.io.MySerialCOM;
import gnu.io.SerialPort;
import myutil.MyLogger;
import org.apache.log4j.Logger;

public class PortMonitor implements Runnable {
    
    private static Logger statusLogger = MyLogger.getStatusLogger();      
    private static Logger errorsLogger = MyLogger.getErrorsLogger();
    
    private MySerialCOM COM;
    private String portName;
    private boolean isStop = false;
    private Thread comThread;
    
    public PortMonitor(String portName) {
        this.portName = portName;
    }
    
    public boolean open() {
        try {
            COM = new MySerialCOM(portName);
            COM.selectPort();
            COM.openPort();
            COM.setPortParams(57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            COM.initRead();
            COM.initWrite();
            return true;
        } catch (Exception ex) {
            errorsLogger.error(ex.getMessage(), ex);
            return false;
        }
    }
    
    public void close() {
        try {
            if(COM != null){
                COM.close();
                System.out.println(portName + " is Closed");
            }     
        } catch (Exception ex) {
            errorsLogger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void run() {
        while(!isStop) {
            System.out.println(portName + " Monitoring ... ");
            byte[] bytes = DataQueue.getInstance().take();
            if(bytes != null) {
                System.out.println(DataParser.parse(bytes));
                System.out.println(Integer.toHexString(DataParser.getHandlerStatus()));
            }
        }
    }
    
    public void start() {
        System.out.println("Start " + portName + " Monitor : ");
        if(comThread == null) {
            comThread = new Thread(this, portName);
            comThread.start();
        }
    }
    
    public void stop() {
        isStop = true;
        System.out.println(portName + " Stop Monitor");
    }
}
