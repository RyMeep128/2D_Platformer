import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.List;

public class Enemy extends KillableObject {

	private boolean onGround;
	private int gravity;
	
	

	public Enemy(int x, int y, int width, int height, int speed) {
		super(x, y, width, height, speed, "Enemy");
		this.onGround = true; // Initially, the enemy is not on the ground
		this.gravity = 1; // Gravity value for falling
	}
	
	public Enemy(int x, int y, int width, int height, int speed, String name) {
		super(x, y, width, height, speed, name);
		this.onGround = true; // Initially, the enemy is not on the ground
		this.gravity = 1; // Gravity value for falling
	}
	
	public Enemy(int x, int y) {
		
		super(x, y, 16, 16, 2, "Enemy");
		this.onGround = true; // Initially, the enemy is not on the ground
		this.gravity = 1; // Gravity value for falling
	}
	
	public Enemy(int x, int y,int speed) {
		
		super(x, y, 16, 16, speed, "Enemy");
		this.onGround = true; // Initially, the enemy is not on the ground
		this.gravity = 1; // Gravity value for falling
	}


	@Override
	public void die() {
		alive = false;

	}

	// Method to kill the enemy
	public void kill() {
		alive = false; // Mark the enemy as dead
	}

	// Method to apply gravity to the enemy
	public void applyGravity() {
		if (!onGround) {
			velocityY += gravity; // Apply gravity if not grounded
		}
	}

	// Method to check for platform collisions for the enemy
	public void checkPlatformCollision(List<Platform> platforms) {
		onGround = false; // Reset onGround before checking collisions

		for (Platform platform : platforms) {
			// Detect if the enemy lands on top of the platform
			if ((y + height) <= platform.getY() && (y + height + velocityY) >= platform.getY() && // Enemy is falling
																									// onto the platform
					x + width > platform.getX() && x < platform.getX() + platform.getWidth()) { // Horizontal overlap
																								// with platform

				// Snap the enemy to the top of the platform
				y = platform.getY() - height;
				velocityY = 0; // Stop downward movement
				onGround = true; // Mark the enemy as grounded
				return; // Exit early to prevent further checks if top collision occurs
			}
		}
	}

	// Method to update the enemy's movement (gravity and platform collision)
	public void update(List<Platform> platforms) {
		if (alive) {
			// Apply gravity to the enemy
			applyGravity();

			// Update enemy's vertical position
			y += velocityY;

			// Check for platform collisions after updating position
			checkPlatformCollision(platforms);

			move(platforms);

			// Update horizontal position (we can handle enemy movement later if needed)
			x += velocityX;

		}
	}
	
	protected void moveByBorders(Game game) {
		int buffer = 100;
		int leftBorder = game.camera.getXOffset() - buffer;
		int rightBorder = game.gameWindow.getWidth() + leftBorder + buffer;
		if(this.x <= leftBorder) {
			this.moveRight(); 
		}
		if(this.x >= rightBorder) {
			this.moveLeft();
		}
	}



	

	// Method to draw the enemy relative to the camera's position
	public void draw(Graphics g, int xOffset) {
		if (alive) {
			super.draw(g, xOffset);
			if (this.objectImage == null) {
				g.setColor(new Color(171, 0, 162));
				g.fillRect(x - xOffset, y, width, height);
			}
		}
	}
}
