

import java.util.ArrayList;
import java.util.Random;


public interface AllObjects {
    
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

        public Player(int x, int y)
        {
            allowedToMove = true;
            xPosition = x;
            yPosition = y;
            weapon = null;
            armor = null;
            potion = null;
            team = null;
            type = null;
        }
    }
}
