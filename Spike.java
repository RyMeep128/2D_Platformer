import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

public class Spike extends KillingObject {

	public Spike(int x, int y, int width, int height) {
		super(x, y, width, height, "Spike");	
	}
	public Spike(int x, int y) {
		super(x, y, Game.DEFAULT_GRID_SIZE, Game.DEFAULT_GRID_SIZE, "Spike");	
	}

	// Method to draw the spike relative to the camera's position
	public void draw(Graphics g, int xOffset) {
		super.draw(g, xOffset);
		if (this.objectImage == null) {
			g.setColor(Color.RED); // Set the color for the spike
			int[] xPoints = { x - xOffset, x + width / 2 - xOffset, x + width - xOffset };
			int[] yPoints = { y + height, y, y + height };
			g.fillPolygon(xPoints, yPoints, 3); // Draws a triangle shape for the spike relative to the camera
		}
	}

	/**
	 * Method to check collision with a KillableObjects instance and kill it if a
	 * collision is detected.
	 */
	public void checkCollision(KillableObject killable) {
		// Only process collisions if the object is still alive
		if (killable.isAlive() && hitDetection(killable)) {
			kill(killable);
		}
	}

	/**
	 * Update method to handle collisions with killable objects. This should be
	 * called in the game loop.
	 */
	public void update(KillableObject player, List<Enemy> enemies) {
		// Check collision with the player
		checkCollision(player);

		// Check collision with each enemy
		for (Enemy enemy : enemies) {
			checkCollision(enemy);
		}

	}

	@Override
	public void kill(KillableObject object) {
		object.die(); // Call the die method on the killable object
	}
}
