import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class LevelSaver {

    public static void saveLevel(List<Platform> platforms, List<Spike> spikes, List<Enemy> enemies, Player player, Goal goal, String filePath) {
        // Create the levels folder if it doesn't exist
        File folder = new File(Player.getStageFolder().split("/")[0]);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File levelFile = new File(filePath);   

        try (FileWriter writer = new FileWriter(levelFile)) {
        	
        	writer.write(filePath);
            // Save player spawn position
            writer.write(player.toWrite("Player"));

            // Save platforms
            for (Platform platform : platforms) {
                writer.write(platform.toWrite("Platform"));
            }

            // Save spikes
            for (Spike spike : spikes) {
                writer.write(spike.toWrite("Spike"));
            }

            // Save enemies
            for (Enemy enemy : enemies) {
            	if(enemy instanceof Flyer)
            		writer.write(((Flyer)enemy).toWrite("Flyer"));
            	else writer.write(enemy.toWrite("Enemy"));
            }

            // Save goal
            if (goal != null) {
                writer.write(goal.toWrite("Goal"));
            }
          
            

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
