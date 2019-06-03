package config;

public class EnvConfig {

    private String portName;
    
    private static EnvConfig envConfig = new EnvConfig();
    private EnvConfig(){};
    
    public static EnvConfig getInstance() {
        return envConfig;
    }

    public String getPortName() {
        return portName;
    }
    
    public void setPortName(String portName) {
        this.portName = portName;
    }
        
}
