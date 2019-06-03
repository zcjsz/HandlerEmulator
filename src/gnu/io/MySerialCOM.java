package gnu.io;

import monitor.DataQueue;
import java.util.Enumeration;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TooManyListenersException;
import myutil.MyLogger;
import org.apache.log4j.*;


public class MySerialCOM implements SerialPortEventListener {

    private String portName;
    private String appName = "RS232";
    private int timeout = 2000;

    private static int waitEchoSetupTime = 200;
    private static int waitEchoIncreasedTime = 100;
    private static int waitEchoCntLimit = 10;

    private char sendCmd;
    private byte [] cmdByte = new byte[0];
    private char echoCmd;
    private byte[] echoData;
    private byte[] echoByte = new byte[0];

    private CommPortIdentifier commPort;
    private SerialPort serialPort;
    private OutputStream outputStream;
    private InputStream inputStream;

    private static Logger statusLogger = MyLogger.getStatusLogger();      
    private static Logger errorsLogger = MyLogger.getErrorsLogger();
    
    public MySerialCOM(String portName){
        this.portName = portName;
    }    
    

    public void listPort(){
        CommPortIdentifier cpid;
        Enumeration en = CommPortIdentifier.getPortIdentifiers();
        while(en.hasMoreElements()){
            cpid = (CommPortIdentifier) en.nextElement();
            if(cpid.getPortType() == CommPortIdentifier.PORT_SERIAL){
                statusLogger.info(cpid.getName() + ", " + cpid.getCurrentOwner());
            }
        }
    }
    
    
    public void selectPort() throws Exception{
        CommPortIdentifier cpid = null;
        Enumeration en = CommPortIdentifier.getPortIdentifiers();
        while(en.hasMoreElements()){
            cpid = (CommPortIdentifier) en.nextElement();
            if(cpid.getPortType() == CommPortIdentifier.PORT_SERIAL && cpid.getName().equalsIgnoreCase(portName)){
                commPort = cpid;
                break;
            }
        }
        if(commPort == null){
            statusLogger.info(portName + " is Not Existed!");
            System.out.println(portName + " is Not Existed!");
            throw new Exception(portName + " is Not Existed!");
        } else {
            statusLogger.info(portName + " is Selected");
            System.out.println(portName + " is Selected");
        }
    }    
    
    
    public void openPort() throws Exception{
        if(commPort == null){
            statusLogger.info("Serial Port is Not Selected!");
            System.out.println("Serial Port is Not Selected!");
            throw new Exception("Serial Port is Not Selected!");
        } else if(commPort.isCurrentlyOwned()) {
            statusLogger.info(portName + " is Owned By Other Process");
            System.out.println(portName + " is Owned By Other Process");
            throw new Exception(portName + " is Owned By Other Process");
        } else {
            try {
                serialPort = (SerialPort) commPort.open(appName, timeout);
                System.out.println(portName + " is opened!");
                statusLogger.info(portName + " is opened!");
            } catch (PortInUseException ex) {
                errorsLogger.error(ex.getMessage(), ex);
                System.out.println(ex.getMessage());
                throw new Exception(portName + " is In Use", ex);
            }
        }
    }
    
    
    public void setPortParams(int BaudRate, int DataBits, int StopBits, int Parity) throws Exception{
        try {
            serialPort.setSerialPortParams(BaudRate, DataBits, StopBits, Parity);
            System.out.println(String.format("Set %s Params: BaudRate - %s, BataBits - %d, StopBits - %d, Parity - %d", portName, BaudRate, DataBits, StopBits, Parity));
            statusLogger.info(String.format("Set %s Params: BaudRate - %s, BataBits - %d, StopBits - %d, Parity - %d", portName, BaudRate, DataBits, StopBits, Parity));
        } catch (UnsupportedCommOperationException ex) {
            errorsLogger.error(ex.getMessage(), ex);
            throw new Exception(portName + " set parameters error", ex);
        }
    }
     
    
    private byte[] generateComBytes(char cmd, String data){
        int cmdByteLen = data.length() + 3;
        byte[] cmdByteAry = new byte[cmdByteLen];
        byte[] dataByteAry = data.getBytes();
        cmdByteAry[0] = (byte) data.length();
        cmdByteAry[1] = (byte) cmd;
        for(int i=2; i<=cmdByteLen-2; i++){
            cmdByteAry[i] = dataByteAry[i-2];
        }
        cmdByteAry[cmdByteLen-1] = (byte) calChkSum(cmd, data);
        statusLogger.debug("Get COM Data : " + echoBytesToString(cmdByteAry));
        cmdByte = cmdByteAry;
        return cmdByteAry;
    }
    
    
    public int calChkSum(char cmd, byte[] data){
        int chkSum = 0;
        int tmp = 0;
        int cmdInt = cmd;

        chkSum = cmdInt;
        for(int i=0; i<data.length; i++){
            //statusLogger.debug(strByte[i] + "(" + (char)strByte[i] + ")" + " ");
            chkSum += (int) (data[i] & 0xFF);
        }
        statusLogger.debug("CheckSum Before adjusting : " + chkSum);
        if(chkSum >= 256){
            tmp = (chkSum & 0x00FF);
            chkSum = tmp;
        }
        statusLogger.debug("CheckSum After adjusting : " + tmp);
        return chkSum;
    }

    
    public int calChkSum(char cmd, String data){
        int chkSum = 0;
        int tmp = 0;
        int cmdInt = cmd;
        byte[] dataByte = data.getBytes();

        chkSum = cmdInt;
        for(int i=0; i<dataByte.length; i++){
            //statusLogger.debug(strByte[i] + "(" + (char)strByte[i] + ")" + " ");
            chkSum += dataByte[i];
        }
        statusLogger.debug("CheckSum Before adjusting : " + chkSum);
        if(chkSum >= 256){
            tmp = (chkSum & 0x00FF);
            chkSum = tmp;
        }
        statusLogger.debug("CheckSum After adjusting : " + tmp);
        return chkSum;
    }
    
    
    private boolean parseEcho(int cnt){
        
        if(echoByte.length < 3){
            statusLogger.debug("Echo Data Fail : less than 3 bytes, Try Echo Count : " + cnt );
            echoCmd = '0';
            echoData = new byte[]{0};
            return false;
        }
        
        statusLogger.debug("Get Echo Data : " + echoBytesToString(echoByte));
        byte[] dataByte = Arrays.copyOfRange(echoByte, 2, echoByte.length-1);
        
        int echoChkSum = echoByte[echoByte.length-1];
        int echoDataLength = echoByte[0];
        echoCmd = (char) echoByte[1];
        echoData = dataByte;

        int calChkSum = calChkSum(echoCmd, echoData);
        int calDataLength = echoData.length;

        if(echoCmd == '1'){
            calDataLength = calDataLength + 1;
        }
        
        if(echoCmd != sendCmd){
            statusLogger.debug("Echo Command Mismatch!!, Try Echo Count : " + cnt);
            return false;
        }        
        
        if(echoDataLength != calDataLength){
            statusLogger.debug("Data Length Mismatch!!, Try Echo Count : " + cnt);
            return false;
        }

        if(echoChkSum != calChkSum){
            statusLogger.debug("Echo CheckSum Mismatch!!, Try Echo Count : " + cnt);
            return false;
        }
        
        return true;
    }

    
// ----------------------------------------------------------------------
//  Send command to handler, check the echo data and return check status
// ----------------------------------------------------------------------
    public boolean sendComCmd(char cmd, String data){
        boolean parseFail;
        int cnt = 0;
        waitEchoSetupTime = 200;
        String echoStr = "";
        
        sendCmd = cmd;
        byte[] comData = generateComBytes(cmd, data);
        
        echoByte = new byte[0];
        startRead();
        
        write(comData);
        try { Thread.sleep(waitEchoSetupTime); } catch (InterruptedException ex) { errorsLogger.error(ex.getMessage(), ex); }
        
        while((parseFail = !parseEcho(cnt)) && cnt < waitEchoCntLimit) {
            cnt++;
            waitEchoSetupTime += cnt * waitEchoIncreasedTime;
            write(comData);
            try { Thread.sleep(waitEchoSetupTime); } catch (InterruptedException ex) { errorsLogger.error(ex.getMessage(), ex); }
        }
        
        stopRead();
        
        if(parseFail){
            statusLogger.debug("COM Echo Data Fail");
            return false;
        } else {
            statusLogger.debug("COM Echo Data Pass");
            return true;
        }
    }

    
    private void write(byte[] msg){
        try {
            outputStream.write(msg);
            outputStream.flush();
            statusLogger.debug("Send : " + echoBytesToString(msg));
        } catch (IOException ex) {
            errorsLogger.error(ex.getMessage(), ex);
        }
    }    
    
    
    public void initWrite() throws Exception{
        try {
            outputStream = new BufferedOutputStream(serialPort.getOutputStream());
            System.out.println(String.format("Init %s Write Setting", portName));
            statusLogger.info(String.format("Init %s Write Setting", portName));
        } catch (IOException ex) {
            errorsLogger.error(ex.getMessage(), ex);
            throw new Exception(ex.getMessage(), ex);
        }
    }

    
    public void initRead() throws Exception{
        try {
            inputStream = new BufferedInputStream(serialPort.getInputStream());
            System.out.println(String.format("Init %s Read Setting", portName));
            statusLogger.info(String.format("Init %s Read Setting", portName));
        } catch (IOException ex) {
            errorsLogger.error(ex.getMessage(), ex);
            throw new Exception(ex.getMessage(), ex);
        }
        try{  
            serialPort.addEventListener(this);  
        } catch (TooManyListenersException ex){  
            errorsLogger.error(ex.getMessage(), ex);
            throw new Exception(ex.getMessage(), ex);
        }
        serialPort.notifyOnDataAvailable(true);
    }


    private void startRead(){
        serialPort.notifyOnDataAvailable(true);
        try { Thread.sleep(5); } catch (InterruptedException ex) { errorsLogger.error(ex.getMessage(), ex); }
    }
    
    
    private void stopRead(){
        serialPort.notifyOnDataAvailable(false);
    }
    
    
    public void close(){
        if(serialPort != null){
            serialPort.close();
            statusLogger.info(portName + " closed!");
        } else {
            statusLogger.info(portName + " Is Not Opened!");
        }
        try {
            inputStream.close();
            outputStream.close();
        } catch (IOException ex) {
            errorsLogger.error(ex.getMessage(), ex);
        }
    }    

    
    public String echoBytesToString(byte[] bytes){
        String cmdStr;
        String cmdLength, cmd, chksum, data, tmp;
        
        if(bytes.length < 3) {
            cmdStr = "Error Cmd / Echo Data, Data Length < 3";
            statusLogger.debug(cmdStr);
            return cmdStr;
        }
        
        cmdLength = Integer.toHexString(bytes[0]).toUpperCase();
        cmd = Integer.toHexString(bytes[1] & 0xFF);
        chksum = Integer.toHexString(bytes[bytes.length-1] & 0xFF).toUpperCase();
        
        if(cmdLength.length() <2){
            cmdLength = "0" + cmdLength;
        }
        
        byte[] dataByteAry;
        
        data = "";
        if(bytes[0] == 0){
            cmdStr = cmdLength + "_" + cmd + "_" + chksum;
        } else {
            dataByteAry = Arrays.copyOfRange(bytes, 2, bytes.length-1);
            if("31".equals(cmd)){
                for(int i=0; i<dataByteAry.length; i++){
                    data += Integer.toHexString(dataByteAry[i]);
                }
                if(data.length() <2){
                    data = "0" + data;
                }
            } else {
                for(int j=0; j<dataByteAry.length; j++){
                    data = new String(dataByteAry);
                }
            }
            cmdStr = cmdLength + "_" + cmd + "_" + data + "_" + chksum;
        }        
        
        return cmdStr;
    }
    
    
    @Override
    public void serialEvent(SerialPortEvent event){
        switch(event.getEventType()) {
            case SerialPortEvent.DATA_AVAILABLE: { dataAvailable(event);  break; }
            /* Other events, not implemented here ->
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY: { outputBufferEmpty(event); break; }
            case SerialPortEvent.BI: { breakInterrupt(event); break; }
            case SerialPortEvent.CD: { carrierDetect(event);  break; }
            case SerialPortEvent.CTS:{ clearToSend(event);    break; }
            case SerialPortEvent.DSR:{ dataSetReady(event);   break; }
            case SerialPortEvent.FE: { framingError(event);   break; }
            case SerialPortEvent.OE: { overrunError(event);   break; }
            case SerialPortEvent.PE: { parityError(event);    break; }
            case SerialPortEvent.RI: { ringIndicator(event);  break; }
            <- other events, not implemented here */
            default: { break; }
        }     
    }
    

    private void dataAvailable(SerialPortEvent event) {
        //for(int i=0; i<readByte.length; i++){
        //    readByte[i] = 0;
        //}
        byte[] readByte = new byte[1024];
        int len = 0;
        
        try{  
            len = inputStream.available();
            statusLogger.debug(len);
            inputStream.read(readByte);
            echoByte = new byte[len];
            System.arraycopy(readByte, 0, echoByte, 0, len);
            DataQueue.getInstance().offer(echoByte);
        } catch (IOException ex){  
            errorsLogger.error(ex.getMessage(), ex);
        }
    }


    public byte[] getEchoData(){
        return echoData;
    }
    
    public char getEchoCmd(){
        return echoCmd;
    }
    
    public byte[] getEchoBytes(){
        return echoByte;
    }
    
    public byte [] getCmdBytes(){
        return cmdByte;
    }
    
    public static void setWaitEchoParams(HashMap<String, String> dict){
        if(dict.containsKey("waitEchoSetupTime")) { waitEchoSetupTime = Integer.parseInt(dict.get("waitEchoSetupTime")); }
        if(dict.containsKey("waitEchoIncreasedTime")) { waitEchoIncreasedTime = Integer.parseInt(dict.get("waitEchoIncreasedTime")); }
        if(dict.containsKey("waitEchoCntLimit")) { waitEchoCntLimit = Integer.parseInt(dict.get("waitEchoCntLimit")); }
    }

}
