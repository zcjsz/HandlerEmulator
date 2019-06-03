package controller;

import monitor.DataQueue;
import java.net.URL;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import monitor.PortManager;

public class FXMLExample extends Application{
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        URL url = getClass().getResource("/gui/FXMLExample.fxml");
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root, 800, 400);
        
        primaryStage.setTitle("FXML Welcome");
        primaryStage.setScene(scene);
        primaryStage.show();
               
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                PortManager.getInstance().closeAllPort();
                byte[] bytes = new byte[3];
                bytes[0] = bytes[1] = bytes[2] = 0x20;
                DataQueue.getInstance().offer(bytes);
                System.out.println("Window closed");
            }
        });
        
        
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
