import java.io.FileNotFoundException;

public class LevelLoader implements Runnable {
    private Game game;
    private String levelFileName;

    public LevelLoader(Game game, String levelFileName) {
        this.game = game;
        this.levelFileName = levelFileName;
    }

    @Override
    public void run() {
        try {
			game.loadLevel(levelFileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Method that loads the level data
        game.setLoading(false); // Set the loading flag to false when done
    }
}
