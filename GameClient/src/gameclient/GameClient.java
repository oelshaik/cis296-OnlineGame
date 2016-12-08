
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameClient extends Application implements AllObjects  {
  
  // Input and output streams from/to server
  private ObjectInputStream fromServer;
  private ObjectOutputStream toServer;
  private final String host = "localhost";
  private String myName = "";
  private VBox root;
  private boolean registered = false;
  private Player myPlayer;
  private GameTile myTile;
  private Text playerHP; 
  private Text playerName;
  private Text playerIntel;
  private Text playerstr;
  private Text nextUpdateText;
  private int timeLeft;
  private ArrayList myTurn;
  private Button moveUp;
  private Button moveDown;
  private Button moveLeft;
  private Button moveRight;
  
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
    //registerScreen();
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

void registerScreen()
{
    Platform.runLater(() -> {
        

        root.getChildren().clear();
        root.setAlignment(Pos.CENTER);

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(20,20,20,20));
        hbox.setAlignment(Pos.CENTER);
        HBox hbox2 = new HBox();
        hbox2.setPadding(new Insets(20,20,20,20));
        hbox2.setAlignment(Pos.CENTER);

        final ToggleGroup classGroup = new ToggleGroup();
        final ToggleGroup teamGroup = new ToggleGroup();

        RadioButton rb1 = new RadioButton("Fighter ");
        rb1.setToggleGroup(classGroup);
        rb1.setUserData("Fighter");
        rb1.setSelected(true);

        RadioButton rb2 = new RadioButton("Healer ");
        rb2.setUserData("Healer");
        rb2.setToggleGroup(classGroup);

        RadioButton rb3 = new RadioButton("Wizard ");
        rb3.setUserData("Wizard");
        rb3.setToggleGroup(classGroup);

        RadioButton rb4 = new RadioButton("Mystic ");
        rb4.setToggleGroup(teamGroup);
        rb4.setUserData("Mystic");
        rb4.setSelected(true);

        RadioButton rb5 = new RadioButton("Valor ");
        rb5.setToggleGroup(teamGroup);
        rb5.setUserData("Valor");

        RadioButton rb6 = new RadioButton("Instinct ");
        rb6.setToggleGroup(teamGroup);
        rb6.setUserData("Instinct");

        root.getChildren().add(new Text(10, 50, "Select Class"));
        hbox.getChildren().add(rb1);
        hbox.getChildren().add(rb2);
        hbox.getChildren().add(rb3);
        root.getChildren().add(hbox);
        root.getChildren().add(new Text(10, 50, "Select Team"));
        hbox2.getChildren().add(rb4);
        hbox2.getChildren().add(rb5);
        hbox2.getChildren().add(rb6);
        root.getChildren().add(hbox2);

        Button registerbtn = new Button();
        registerbtn.setText("Register");
        root.getChildren().add(registerbtn);

        registerbtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                    try{
                    //toServer.writeChars(inputWordtf.getText());
                        //System.out.println(classGroup.getSelectedToggle().getUserData().toString());
                        //System.out.println(teamGroup.getSelectedToggle().getUserData().toString());
                        myPlayer = new Player("");
                        myPlayer.type = classGroup.getSelectedToggle().getUserData().toString();
                        myPlayer.team = teamGroup.getSelectedToggle().getUserData().toString();
                        registered = true;
                    }catch(Exception e)
                    {
                    }
                    //System.out.println(inputWordtf.getText());
                }
        });
    
    });
}

void loadUI()
{          
    Platform.runLater(() -> {
        root.getChildren().clear();
        root.setAlignment(Pos.CENTER);

        playerName = new Text("Name : ");
        playerHP = new Text("HP : ");
        playerIntel = new Text("Intelligence : ");
        nextUpdateText = new Text("Time Left : ");
        moveUp = new Button("Move Up");
        moveDown = new Button("Move Down");
        moveRight = new Button("Move Right");
        moveLeft = new Button("Move Left");
        
        moveUp.setDisable(true);
        moveDown.setDisable(true);
        moveRight.setDisable(true);
        moveLeft.setDisable(true);
        
        VBox playerInfo = new VBox();

        
        playerInfo.getChildren().add(playerName);
        playerInfo.getChildren().add(playerHP);
        playerInfo.getChildren().add(playerIntel);
        playerInfo.getChildren().add(nextUpdateText);
        playerInfo.getChildren().add(moveUp);
        playerInfo.getChildren().add(moveDown);
        playerInfo.getChildren().add(moveLeft);
        playerInfo.getChildren().add(moveRight);
        
        root.getChildren().add(playerInfo);
     });
}

void updateUI()
{
    Platform.runLater(() -> {
        playerName.setText("Name : " + myPlayer.name);
        playerHP.setText("HP : " + myPlayer.health );
        playerIntel.setText("Intelligence : " + myPlayer.intel);
    });
}

void updateTime()
{
    Platform.runLater(() -> {
        nextUpdateText.setText("Time Left : " + timeLeft);
    });
}

class HandleServer implements Runnable {
    private void handleMessage(ArrayList msg)
    {
        String code;
        code = (String)msg.get(0);
        System.out.println(code);
        switch(code)
        {
            case "update":
                myPlayer = (Player)msg.get(1);
                myTile = (GameTile)msg.get(2);
                registered = true;
                updateUI();
                break;
            case "nextUpdate":
                timeLeft = (int)msg.get(1);
                updateTime();
                break;
        }
    }
    
    public void run()
    {
        try 
        {
            ArrayList outMessage;
            ArrayList message;
            // Create a socket to connect to the server
            Socket socket = new Socket(host, 8001);    
            toServer = new ObjectOutputStream(socket.getOutputStream());
            fromServer = new ObjectInputStream(socket.getInputStream());
            
            while(myName.equals(""))
            {
                Thread.sleep(100);
                
            }
            
            toServer.writeObject(myName); // send name 
            message = (ArrayList)fromServer.readObject(); // wait for response
            handleMessage(message);
            
            if(!registered)
            {
                registerScreen();
                while(!registered)
                {
                    Thread.sleep(100);
                }
                outMessage = new ArrayList();
                outMessage.add("register");
                outMessage.add(myPlayer.team);
                outMessage.add(myPlayer.type);
                toServer.writeObject(outMessage);
            }
            
            loadUI();
            // logged in at this point
            while(true)
            {
                outMessage = null;
                toServer.writeObject(outMessage);
                message = (ArrayList)fromServer.readObject();
                handleMessage(message);
                
                
//                for(Object o : message)
//                {
//                    System.out.println(o);
//                }
//                System.out.println();
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

