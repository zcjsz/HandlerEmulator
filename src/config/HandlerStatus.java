package config;

public class HandlerStatus {
    
    private boolean isHandlerReady = false;
    private boolean isReadyToTest = false;
    private boolean isSocket1Ready = false;
    private boolean isSocket2Ready = false;
    private boolean isHandlerACK = false;
    
    private static final HandlerStatus handlerStatus = new HandlerStatus();
    private HandlerStatus(){};
    
    public static HandlerStatus getInstance() {
        return handlerStatus;
    }

    public boolean isHandlerReady() {
        return isHandlerReady;
    }

    public boolean isReadyToTest() {
        return isReadyToTest;
    }

    public boolean isSocket1Ready() {
        return isSocket1Ready;
    }

    public boolean isSocket2Ready() {
        return isSocket2Ready;
    }
    
    public boolean isHandlerACK() {
        return isHandlerACK;
    }
    
    public void setHandlerReady(boolean isHandlerReady) {
        this.isHandlerReady = isHandlerReady;
    }

    public void setReadyToTest(boolean isReadyToTest) {
        this.isReadyToTest = isReadyToTest;
    }

    public void setSocket1Ready(boolean isSocket1Ready) {
        this.isSocket1Ready = isSocket1Ready;
    }

    public void setSocket2Ready(boolean isSocket2Ready) {
        this.isSocket2Ready = isSocket2Ready;
    }
    
    public void setHandlerACK(boolean isHandlerACK) {
        this.isHandlerACK = isHandlerACK;
    }
    
    
}
