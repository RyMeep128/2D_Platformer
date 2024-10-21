import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LevelReader {

    public static LevelData readLevel(String fileName,Game game) throws FileNotFoundException {
        File file = new File(fileName);
        List<Platform> platforms = new ArrayList<>();
        List<Spike> spikes = new ArrayList<>();
        List<Enemy> enemies = new ArrayList<>();
        List<GameObject> gameObjects = new ArrayList<>();
        Player player = null;
        Goal goal = null;
        boolean firstPass = true;
        String resetLevel="";

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {       
                String line = scanner.nextLine();
                String[] data = line.split(",");
                if(firstPass) {
                	resetLevel=line;
                	firstPass = false;
                	continue;
                }
                
                String objectType = data[0];
                int x = Integer.parseInt(data[1]);
                int y = Integer.parseInt(data[2]);
                int width = Integer.parseInt(data[3]);
                int height = Integer.parseInt(data[4]);        
                String name = data[5];             

                switch (objectType) {
                    case "Player":
                    	System.out.println(resetLevel);
                        player = new Player(x, y, width, height, 5, 1, null);  // Pass the appropriate parameters for the game reference
                    	player.resetLevel = resetLevel;
                        break;
                    case "Platform":
                        platforms.add(new Platform(x, y, width, height,name));
                        break;
                    case "Spike":
                        spikes.add(new Spike(x, y, width, height));
                        break;
                    case "Enemy":
                        enemies.add(new Enemy(x, y, width, height, 2,name));  // Adjust the speed as needed
                        break;
                    case "Flyer":
                    	enemies.add(new Flyer(x, y, width, height, 2,name)); 
                    	break;
                    case "Goal":
                        goal = new Goal(x, y, width, height);
                        break;
//                    case "TextBox":
//                    		String text = name;
//                            TextBox textBox = new TextBox(x, y, width, height);
//                            textBox.appendText(text);  // Restore the text
//                            gameObjects.add(textBox);
//                        break;

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Return a new LevelData object that holds all the game objects
        return new LevelData(player, platforms, spikes, enemies, goal);
    }
}
