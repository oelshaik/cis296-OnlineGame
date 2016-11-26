import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class GameServer extends Application implements  AllObjects {
  private GameTile[][] masterGrid =  new GameTile[GRID_SIZE][GRID_SIZE]; // lock needed to modify
  private LinkedList<ArrayList> moveQueue = new LinkedList<ArrayList>(); // lock needed to modify
  private ArrayList<HandlePlayer> playerHandlers = new ArrayList<HandlePlayer>(); 
  private ArrayList<Player> allPlayers = new ArrayList<Player>(); 
  private Semaphore threadLock = new Semaphore(1,true); 
  private int secondsLeft = UPDATE_RATE;
  
  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
    
    initGrid();
    readFromDB();
    TextArea taLog = new TextArea();
    // Create a scene and place it in the stage
    Scene scene = new Scene(new ScrollPane(taLog), 450, 200);
    primaryStage.setTitle("Game Server"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage

    new Thread( () -> {
      try {
        // Create a server socket
        ServerSocket serverSocket = new ServerSocket(8001);
        Platform.runLater(() -> taLog.appendText(new Date() + ": Server started at socket 8001\n"));
        new Thread(new TimerClass()).start(); // start timer
        
        while (true) {
          Platform.runLater(() -> taLog.appendText(new Date() +": Wait for players to join session  \n"));
  
          Socket newPlayer = serverSocket.accept();
  
          Platform.runLater(() -> {
            taLog.appendText(new Date() + ": New player joined session \n");
            taLog.appendText("Player's IP address" +newPlayer.getInetAddress().getHostAddress() + '\n');
            taLog.appendText(": Start a thread for session \n");
            new Thread(new HandlePlayer(newPlayer)).start();
          });
        }
      }
      catch(IOException ex) {
        ex.printStackTrace();
      }
    }).start();
  }
  
  public void executeMoves()
  {
      ArrayList command;
      while(!moveQueue.isEmpty())
      {
          command = (ArrayList)moveQueue.poll();
          for(Object o : command)
          {
              System.out.print(o + " ");
          }
          System.out.println();
      }
  }
  
  public void updatePlayers()
  {
      for(int i = 0; i < allPlayers.size(); i++)
      {
          allPlayers.get(i).allowedToMove = true;
      }
      for(int i = 0; i < playerHandlers.size(); i++)
      {
          playerHandlers.get(i).sendUpdate = true;
      }
  }
  
  private void initGrid()
  {
      for(int i = 0; i < GRID_SIZE; i++)
      {
          for(int j = 0; j < GRID_SIZE; j++)
          {
              masterGrid[i][j] = new GameTile("abc");
          }
      }
  }
  
  private void addPlayerToGrid()
  {
      //TODO
  }
  
  private void removePlayerFromGrid()// maybe not needed
  {
      //TODO
  }
  
  // copy info from arrays to DB
  private void saveToDB()   
  {
      
      
  }
  
  // copy info from DB to local arrays
  private void readFromDB()
  {
      
      
  }
  
  public void playerSearch(String playerName)
  {
      
  }
  
  public void playerAttack(String playerA, String playerB)
  {
      
  }

  
  public int findPlayer(String lookupName)
  {
      for(int i = 0; i < allPlayers.size(); i++)
      {
          if(allPlayers.get(i).name.equals(lookupName))
          {
              return i;
          }
      }
      return -1;
  }
  
  
  class TimerClass implements Runnable {
      public void run()
      {
          try{
            while(true)
            {
                while(secondsLeft > 0)
                {
                   secondsLeft--;
                   Thread.sleep(1000);
                }
                secondsLeft = UPDATE_RATE;
                threadLock.acquire(1);
                executeMoves();
                updatePlayers();
                saveToDB();
                threadLock.release(1); 
            }
          }
          catch(Exception e)
          {
              e.printStackTrace();
          }
      }
  }
  
  class HandlePlayer implements Runnable {
    private ObjectInputStream fromPlayer;
    private ObjectOutputStream toPlayer;
    private Socket playerSocket;
    int indexOfPlayer;
    public boolean sendUpdate = true;
    public boolean registered = true;
    
    public HandlePlayer(Socket ps)
    {
        playerSocket = ps;
    }
    
    public void handleInput(ArrayList input)
    {
        if(((String)input.get(0)).equals("register"))
        {
            String team = (String)input.get(1);
            String type = (String)input.get(2);
            allPlayers.get(indexOfPlayer).setAttrs(team,type); // initializes everything about player
            // addPlayerToGrid();
            registered = true;
            sendUpdate = true;
        }
        else // starts with turn
        {
            moveQueue.add(input);
            allPlayers.get(indexOfPlayer).allowedToMove = false;
        }
    }
    
    @Override
    public void run()
    {
        try
        {
            ArrayList message;
            toPlayer = new ObjectOutputStream(playerSocket.getOutputStream());
            fromPlayer = new ObjectInputStream(playerSocket.getInputStream());
            String name = (String)fromPlayer.readObject(); // blocking poll waiting for name
            indexOfPlayer = findPlayer(name);
            
            //System.out.println(name);
            if(indexOfPlayer == -1 || allPlayers.get(indexOfPlayer).type == null)// new Player
            {
                Player newplayer = new Player(name);
                allPlayers.add(newplayer);
                indexOfPlayer = allPlayers.size()-1;
                registered = false;
                message = new ArrayList();
                message.add("newPlayer");
                toPlayer.writeObject(message);
            }
           
            while(true)
            {
                if(sendUpdate)
                {
                    message = new ArrayList();
                    message.add("update");
                    message.add(allPlayers.get(indexOfPlayer));
                    message.add(masterGrid[allPlayers.get(indexOfPlayer).yPosition][allPlayers.get(indexOfPlayer).xPosition]);
                    toPlayer.writeObject(message);
                    sendUpdate = false;
                }
                if(fromPlayer.available() > 0) // pending message from client
                {
                    //message = new ArrayList();
                    message = (ArrayList)fromPlayer.readObject();
                    message.add(0,allPlayers.get(indexOfPlayer).name);
                    threadLock.acquire(1);
                    handleInput(message);
                    threadLock.release(1);
                }
                
                if(registered)
                { 
                    message = new ArrayList();
                    message.clear();
                    message.add("canMove");
                    message.add(allPlayers.get(indexOfPlayer).allowedToMove);
                    toPlayer.writeObject(message);

                    message = new ArrayList();
                    message.add("nextUpdate");
                    message.add(secondsLeft);
                    toPlayer.writeObject(message);
                }
                Thread.sleep(500); // tick rate
            }
        }
        catch(Exception i)
        {
            //i.printStackTrace();
            System.out.println("Client disconnected");
        }
    }
  }
  
 
  public static void main(String[] args) {
    launch(args);
  }
}