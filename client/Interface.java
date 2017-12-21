
package client;



import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Interface extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        //AuthPanel authPanel = new AuthPanel();
        ClientPanel clientPanel = new ClientPanel();
        Group root = new Group();
        root.getChildren().add(clientPanel);
        Scene scene = new Scene (root, 260, 160);
        stage.setTitle("Mon chat");
        stage.setScene(scene);
        stage.show();
      
       
    
         }
    public static void main (String[] args){
        Application.launch(Interface.class , args);
        
    }
    
    
}
