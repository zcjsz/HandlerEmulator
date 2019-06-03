package monitor;

import config.HandlerStatus;
import java.util.Arrays;
import java.util.HashMap;

public class DataParser {

    public static HashMap<String, String> parse(byte [] bytes) {
       
        if(bytes.length < 3) {
            System.out.println("Recived command bytes less then 3 bytes, invalid command!");
            return null;
        }

        int len = (int) (bytes[0] & 0xFF);
        char cmd = (char) bytes[1];
        int chksum = bytes[bytes.length-1] & 0xFF;
        String data = len > 0 ? new String(Arrays.copyOfRange(bytes, 2, bytes.length-1)) : "";

        if(!isCmdValid(len, cmd, chksum, data)) {
            System.out.println("Received command is Not Valid!");
            return null;
        }

        HashMap<String, String> pack = new HashMap();
        pack.put("length", Integer.toString(len));
        pack.put("command", String.valueOf(cmd));
        pack.put("checksum", Integer.toString(chksum & 0xFF));
        pack.put("data", data);
       
        byte[] echoBytes = new byte[0];
        
        switch(cmd) {
            case '1':
                echoBytes = new byte[4];
                echoBytes[0] = (byte) 0x02;
                int echoLen = 2;
                byte echoData = getHandlerStatus();
                char echoCmd = cmd;
                int echoChksum = calChkSum(cmd, data);
                
                break;
            case '2':
                break;
            case '3':
                break;
        }

        return pack;
    }

    
    
//    private byte[] generateEchoBytes(char cmd, byte data){
//        int cmdByteLen = data.length() + 3;
//        byte[] cmdByteAry = new byte[cmdByteLen];
//        byte[] dataByteAry = data.getBytes();
//        cmdByteAry[0] = (byte) data.length();
//        cmdByteAry[1] = (byte) cmd;
//        for(int i=2; i<=cmdByteLen-2; i++){
//            cmdByteAry[i] = dataByteAry[i-2];
//        }
//        cmdByteAry[cmdByteLen-1] = (byte) calChkSum(cmd, data);
//        return cmdByteAry;
//    }
    
    
    public static byte[] generateEchoBytes(char cmd, String data){
        int cmdByteLen = data.length() + 3;
        byte[] cmdByteAry = new byte[cmdByteLen];
        byte[] dataByteAry = data.getBytes();
        cmdByteAry[0] = (byte) data.length();
        cmdByteAry[1] = (byte) cmd;
        for(int i=2; i<=cmdByteLen-2; i++){
            cmdByteAry[i] = dataByteAry[i-2];
        }
        cmdByteAry[cmdByteLen-1] = (byte) calChkSum(cmd, data);
        return cmdByteAry;
    }
    
    
    public static byte getHandlerStatus() {
        HandlerStatus handlerStatus = HandlerStatus.getInstance();
        byte status = 0x00;
        if(handlerStatus.isHandlerReady()) status = (byte) (0x08 | status);
        if(handlerStatus.isReadyToTest())  status = (byte) (0x04 | status);
        if(handlerStatus.isSocket2Ready()) status = (byte) (0x02 | status);
        if(handlerStatus.isSocket1Ready()) status = (byte) (0x01 | status);
        return status;
    }
    
    
    public static String getHandlerEcho() {
        HandlerStatus handlerStatus = HandlerStatus.getInstance();
        if(handlerStatus.isHandlerACK()) {
            return "ACK";
        } else {
            return "NAK";
        }
    }
    
    
    public static boolean isCmdValid(int len, char cmd, int chksum, String data) {
        boolean flag = true;
        if(cmd == '1') {
            if(len != 0 || chksum != 0x31) flag = false;
        } else {
            int calChkSum = calChkSum(cmd, data);
            if(len != data.length() || chksum != calChkSum) flag = false;
        }
        return flag;
    }
    
    
    private static int calChkSum(char cmd, String data){
        int chkSum = 0;
        int tmp = 0;
        int cmdInt = cmd;
        byte[] dataByte = data.getBytes();
        chkSum = cmdInt;
        for(int i=0; i<dataByte.length; i++){
            chkSum += (int)(dataByte[i] & 0xFF);
        }
        if(chkSum >= 256){
            tmp = (chkSum & 0x00FF);
            chkSum = tmp;
        }
        return chkSum;
    }
    
    
    public int calChkSum(char cmd, byte[] data){
        int chkSum = 0;
        int tmp = 0;
        int cmdInt = cmd;
        chkSum = cmdInt;
        for(int i=0; i<data.length; i++){
            chkSum += (int) (data[i] & 0xFF);
        }
        if(chkSum >= 256){
            tmp = (chkSum & 0x00FF);
            chkSum = tmp;
        }
        return chkSum;
    }
    

    public static void main(String[] args) {
        
        byte b = (byte) 0x80;
        int a = 128;
        System.out.println(((byte) a) & 0xFF);
        
    }
    
    
}
