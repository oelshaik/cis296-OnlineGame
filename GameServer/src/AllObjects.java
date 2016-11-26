

import java.util.ArrayList;
import java.util.Random;


public interface AllObjects   {
    
    public final int GRID_SIZE = 25;
    public final int UPDATE_RATE = 60;
    
    public class GameTile
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


    }
    
    public class Item 
    {
        public String type;
        public int durability;
        public int value;

        public Item(String t, int d, int v)
        {
            type = t;
            durability = d;
            value = v;
        }
    }
    
    public class Player
    {
        public boolean allowedToMove;
        public String name;
        public int xPosition;
        public int yPosition;
        public String type;
        public String team;
        public Item weapon;
        public Item armor; 
        public Item potion; 
        public int health;
        public int agi;
        public int str;
        public int intel;
      
        public Player(String name)
        {
            this.name = name;
            allowedToMove = true;
            weapon = null;
            armor = null;
            potion = null;
            team = null;
            type = null;
            health = 20;
            xPosition = rollDice(1,GRID_SIZE);
            yPosition = rollDice(1, GRID_SIZE);
            agi = rollDice(3,6);
            str = rollDice(3,6);
            intel = rollDice(3,6);
        }
        
        public void setAttrs(String team, String type)
        {
            this.team = team;
            this.type = type;
            
            if(type.equals("wizard"))
            {
                str -= 3;
                intel += 3;
            }
            else if(type.equals("healer"))
            {
                intel += 3;
            }
            else // type == fighter
            {
                str += 3;
                intel -= 3;
            }
                        
        }
    }
    
    public static int rollDice(int rolls, int sides)
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
