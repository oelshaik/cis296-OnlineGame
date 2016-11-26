
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameClient extends Application implements AllObjects  {
  
  // Input and output streams from/to server
  private ObjectInputStream fromServer;
  private ObjectOutputStream toServer;
  private final String host = "localhost";
  private String myName = "";
  private VBox root;
  
  @Override
  public void start(Stage primaryStage) {

    root = new VBox();
    
    // Create a scene and place it in the stage
    Scene scene = new Scene(root, 320, 350);
    primaryStage.setResizable(false);
    primaryStage.setTitle("GameClient"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage   
    
    // Connect to the server
    new Thread(new HandleServer()).start();
    
    loginScreen();
  }

void loginScreen()
{
    root.getChildren().clear();
    root.setAlignment(Pos.CENTER);
    HBox hbox = new HBox();
    hbox.setPadding(new Insets(20,20,20,20));
    hbox.setAlignment(Pos.CENTER);
    
    TextField inputWordtf = new TextField ();
    inputWordtf.setPromptText("username");
    Button startbtn = new Button();
    startbtn.setText("Login/Register");
    
    hbox.getChildren().add(inputWordtf);
    hbox.getChildren().add(startbtn);
    root.getChildren().add(hbox);
    
    startbtn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try{
            //toServer.writeChars(inputWordtf.getText());
            myName = inputWordtf.getText();
            }catch(Exception e)
            {
            }
            //System.out.println(inputWordtf.getText());
        }
    });
    
    startbtn.requestFocus();
}

class HandleServer implements Runnable {
    private void handleMessage(ArrayList msg)
    {
        
    }
    public void run()
    {
        try 
        {
            // Create a socket to connect to the server
            Socket socket = new Socket(host, 8001);    
            toServer = new ObjectOutputStream(socket.getOutputStream());
            fromServer = new ObjectInputStream(socket.getInputStream());
            
            while(myName.equals(""))
            {
                Thread.sleep(100);
                
            }
            toServer.writeObject(myName);
            ArrayList message;
            while(true)
            {
                message = (ArrayList)fromServer.readObject();
                for(Object o : message)
                {
                    System.out.println(o);
                }
                System.out.println();
            }
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
    }
}
 
  public static void main(String[] args) {
    launch(args);
  }
}

