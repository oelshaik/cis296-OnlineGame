
import java.util.ArrayList;
import java.util.Random;

public class GameTile implements AllObjects
{
    String name;
    ArrayList<Player> players;
    Item item;
    
    public GameTile(String n)
    {
        name = n;
        players = new ArrayList<Player>();
        setItem();
    }
    
    public void clearItem()
    {
        item = null;
    }
    
    public void setItem()
    {
        Random rand = new Random();
        
        switch(rand.nextInt(3)) 
        {
            case 0: // Weapon
                item = new Item("Weapon",rollDice(3,6),1+rand.nextInt(4));
                break;
            case 1: // Armor
                item = new Item("Armor",rollDice(3,6),1+rand.nextInt(4));
                break;
            case 2: // Health Potion
                item = new Item("Potion",1,1+rand.nextInt(4));
                break; 
        }
        //System.out.println(item.type + " " + item.durability + " " + item.value);
    }
    
    public int rollDice(int rolls, int sides)
    {
        int total = 0;
        Random rand = new Random();
        for(int i = 0; i < rolls; i++)
        {
            total += 1 + rand.nextInt(sides);
        }
        return total;
    }
}