package monitor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PortManager {

    private static HashMap<String, PortMonitor> monitorList = new HashMap();
    private static Pattern pat = Pattern.compile("^COM\\d+$");
    
    private static final PortManager portManager = new PortManager();
    private PortManager() {};
    
    public static PortManager getInstance() {
        return portManager;
    }

    public void openPort(String portName) {
        if(portName == null || !pat.matcher(portName).find()) {
            System.out.println("Port Name is Not Corret");
            return;
        }
        PortMonitor pm = new PortMonitor(portName);
        if(pm.open()) {
            monitorList.put(portName, pm);
            pm.start();
        } else {
            System.out.println(portName + " open failed!");
        }
    }
    
    public void closePort(String portName) {
        PortMonitor pm = monitorList.getOrDefault(portName, null);
        if(pm != null) {
            pm.stop();
            pm.close();
            monitorList.remove(portName);
        }
    }
    
    public void closeAllPort() {
        for (Map.Entry<String, PortMonitor> entry : monitorList.entrySet()) {
            String portName = entry.getKey();
            PortMonitor pm = entry.getValue();
            if(pm != null) {
                pm.stop();
                pm.close();
            }
        }
    }
}
