import java.util.List;

public class Flyer extends Enemy{

	private Game game;
	public Game getGame() {
		return game;
	}


	public void setGame(Game game) {
		this.game = game;
	}


	private boolean madeEnemy = false;
	
	public boolean isMadeEnemy() {
		return madeEnemy;
	}


	public void setMadeEnemy(boolean madeEnemy) {
		this.madeEnemy = madeEnemy;
	}


	public Flyer(int x, int y, int width, int height, int speed, Game game) {
		super(x, y, width, height, speed, "Flyer");
		this.game = game;
	}
	
	
	/**
	 * If you use this you need to link the game to this on creation
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param speed
	 * @param name
	 */
	public Flyer(int x, int y, int width, int height, int speed, String name) {
		super(x, y, width, height, speed, name);
	}
	
	@Override
	public void update(List<Platform> platforms) {
		if (alive) {

			// Check for platform collisions after updating position
			checkPlatformCollision(platforms);

			move(platforms);

			// Update horizontal position (we can handle enemy movement later if needed)
			this.moveByBorders(game);
			
			abovePlayer(game.player);
			
			x += velocityX;

		}
	}
	
	
	private boolean createNewEnemy = false;
	private void abovePlayer(Player player) {
		int area = 20;
		List<Enemy> enemies = game.getEnemies();
		if( (x - area <= player.getX()) && ( x + area >= player.getX()) && !madeEnemy) {
			createNewEnemy = true;
			madeEnemy = true;
		}
		
	}
	
	public boolean checkCreateEnemy() {
		return createNewEnemy;
	}
	
	public void setcreateNewEnemy(boolean bool) {
		createNewEnemy = bool;
	}
	

}
