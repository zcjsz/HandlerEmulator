package gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import config.EnvConfig;
import config.HandlerStatus;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import monitor.PortManager;


public class FXMLExampleHandler implements Initializable {

    @FXML private Menu port;
    @FXML private MenuItem com1;
    @FXML private MenuItem com2;
    @FXML private MenuItem com3;
    @FXML private MenuItem com4;
    @FXML private Button btnOpen;
    @FXML private Button btnClose;
    @FXML private CheckBox ckboxHandlerReady;
    @FXML private CheckBox ckboxReadyToTest;
    @FXML private CheckBox ckboxSocket1;
    @FXML private CheckBox ckboxSocket2;
    @FXML private Button show;
    
    @FXML
    private void menuCom1Click(ActionEvent event) {
        port.setText(com1.getText());
        EnvConfig.getInstance().setPortName(com1.getText());
    }
    
    @FXML
    private void menuCom2Click(ActionEvent event) {
        port.setText(com2.getText());
        EnvConfig.getInstance().setPortName(com2.getText());
    }
    
    @FXML
    private void menuCom3Click(ActionEvent event) {
        port.setText(com3.getText());
        EnvConfig.getInstance().setPortName(com3.getText());
    }
    
    @FXML
    private void menuCom4Click(ActionEvent event) {
        port.setText(com4.getText());
        EnvConfig.getInstance().setPortName(com4.getText());
    }
    
    @FXML
    private void btnOpenClick(ActionEvent event) {
        PortManager.getInstance().openPort(EnvConfig.getInstance().getPortName());
    }

    @FXML
    private void btnCloseClick(ActionEvent event) {
        PortManager.getInstance().closePort(EnvConfig.getInstance().getPortName());
    }
    
    @FXML
    private void ckboxHandlerReadyClick(ActionEvent event) {
        HandlerStatus.getInstance().setHandlerReady(((CheckBox) event.getTarget()).isSelected());
    }

    @FXML
    private void ckboxReadyToTestClick(ActionEvent event) {
        HandlerStatus.getInstance().setReadyToTest(((CheckBox) event.getTarget()).isSelected());
    }
    
    @FXML
    private void ckboxSocket1Click(ActionEvent event) {
        HandlerStatus.getInstance().setSocket1Ready(((CheckBox) event.getTarget()).isSelected());
    }

    @FXML
    private void ckboxSocket2Click(ActionEvent event) {
        HandlerStatus.getInstance().setSocket2Ready(((CheckBox) event.getTarget()).isSelected());
    }
    
    @FXML
    private void showClick(ActionEvent event) {
        HandlerStatus config = HandlerStatus.getInstance();
        boolean handlerReady = config.isHandlerReady();
        boolean readyToTest = config.isReadyToTest();
        boolean socket1 = config.isSocket1Ready();
        boolean socket2 = config.isSocket2Ready();
        System.out.println(String.format("HandlerReady - %b, ReadyToTest - %b, Socke1 - %b, Socket2 - %b", handlerReady, readyToTest, socket1, socket2));
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    
}
