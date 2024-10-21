import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Player extends KillableObject {

	private boolean onGround; // Check if the player is on the ground
	private int gravity; // The amount of gravity applied to the player\
	private int lives;
	private final static int DEFAULT_LIVES = 3;
	private final static int DEFAULT_POINTS = 10;
	private final static double DEFAULT_JUMP_MODIFIER = .75;
	protected static String stageFolder = "tutorialLevels/";
	protected String resetLevel;
	private static int levelCount = 1;
	private static int stageCount = 0;
	private boolean stageChecked = false;
	private Game game;
	private int score; // Track the player's score
	private Image heartImage;

	public Player(int x, int y, int width, int height, int speed, int gravity, Game game) {
		super(x, y, width, height, speed, "Player");
		this.gravity = gravity;
		this.score = 0; // Default score
		this.onGround = false; // Initially, the player is in the air
		this.lives = DEFAULT_LIVES;
		this.game = game;
		try {
			heartImage = ImageIO.read(new File("assets/Heart.png"));
		} catch (IOException e) {
			System.err.println("Error loading object image.");
			e.printStackTrace();
		}
	}

	// Method to check if the player is on the ground
	public boolean isOnGround() {
		return onGround;
	}

	// Increase the player's score when an enemy is killed
	public void increaseScore(int points) {
		score += points;
	}

	// Method to lose a life and reset the player
	public void loseLife() {
		lives--;
		resetPosition(); // Reset to starting position
		if (lives <= 0) {
			triggerGameOver(); // Trigger game over logic
			// Add game-over logic here
		}
	}

	public void reset() {
		this.onGround = false; // Initially, the player is in the air
		this.lives = DEFAULT_LIVES;
		this.score = 0;
	}

	// Trigger the game-over popup
	private void triggerGameOver() {
		int response = JOptionPane.showOptionDialog(null, "GAME OVER\nScore: " + score, // Display the score in the
																						// game-over message
				"Game Over", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
				new String[] { "Restart", "Exit" }, "Restart");

		if (response == 0) {
			reset();
			game.restartGame(); // Restart the game
		} else {
			System.exit(0); // Exit the program
		}
	}

	// Method to draw the player relative to the camera's position
	public void draw(Graphics g, int xOffset) {
		super.draw(g, xOffset);
		if (this.objectImage == null) {
			g.setColor(new Color(0, 128, 201));
			g.fillRect(x - xOffset, y, width, height);
		}

		for (int i = 0; i < lives; i++) {
			int heartX = 700 + (i * 30); // Adjust for screen-relative positioning
			int heartY = 30; // Fixed position for vertical alignment
			if (heartImage != null) {
				g.drawImage(heartImage, heartX, heartY, 20, 20, null);
			} else {
				g.setColor(Color.PINK);
				g.fillOval(heartX, heartY, 20, 20); // Draw blue circle (radius 20)
			}
		}

		// Draw the score as white text below the lives
		g.setColor(Color.WHITE);
		g.drawString("Score: " + score, 700, 80); // Adjust for top-right position

		g.drawString(x + " , " + y, x - xOffset, y - height); // debug statement
	}

	// Method to make the player jump
	public void jump() {
		if (onGround) {
			velocityY = -15; // Apply upward velocity (jumping)
			onGround = false; // Player is no longer grounded
		}
	}

	// Method to move the player right
	public void moveRight() {
		velocityX = speed; // Move right by applying positive horizontal velocity
	}

	// Method to move the player left
	public void moveLeft() {
		velocityX = -speed; // Move left by applying negative horizontal velocity
	}

	// Method to stop the player's horizontal movement
	public void stopMoving() {
		velocityX = 0; // Stop horizontal movement
	}

	// Collision detection method
	private boolean isColliding(GameObject a, GameObject b) {
		return a.getX() < b.getX() + b.getWidth() && a.getX() + a.getWidth() > b.getX()
				&& a.getY() < b.getY() + b.getHeight() && a.getY() + a.getHeight() > b.getY();
	}

	// Method to force the the player to jump
	public void forceJump() {
		onGround = true; // Flags the player as touching ground so they can jump
		jump();
	}

	public int getSpawnX() {
		return this.startX;
	}

	// Method to force the the player to jump by a certain strength
	public void forceJump(double jumpStrength) {
		onGround = true; // Flags the player as touching ground so they can jump
		jump(jumpStrength);
	}

	// Method to make the player jump
	public void jump(double jumpStrength) {
		if (onGround) {
			velocityY = (int) (-15 * jumpStrength); // Apply upward velocity (jumping)
			onGround = false; // Player is no longer grounded
		}
	}

	public void checkEnemyKill(List<Enemy> enemies) {
		List<Enemy> enemiesToRemove = new ArrayList<>(); // Collect enemies to remove after iteration

		for (Enemy enemy : enemies) {
			// Check if the player is colliding from above
			if (isCollidingFromAbove(this, enemy)) {
				enemiesToRemove.add(enemy); // Mark for removal
				increaseScore(DEFAULT_POINTS); // Award 10 points for killing an enemy
				forceJump(DEFAULT_JUMP_MODIFIER);
			} else if (isCollidingWithEnemy(this, enemy)) {
				loseLife(); // Handle side and other collisions
				break;

			}
		}

		// Safely remove all marked enemies after iteration
		enemies.removeAll(enemiesToRemove);
	}


	// Example method to check if the player is colliding from above
	private boolean isCollidingFromAbove(GameObject player, GameObject enemy) {
		return player.getY() + player.getHeight() <= enemy.getY() && // Player's bottom is above the enemy
				player.getY() + player.getHeight() + velocityY >= enemy.getY() && // Player is moving downward
				player.getX() + player.getWidth() > enemy.getX() && // Horizontal overlap
				player.getX() < enemy.getX() + enemy.getWidth(); // Horizontal overlap
	}

	// General collision method to check if player is touching an enemy (from side,
	// bottom, etc.)
	private boolean isCollidingWithEnemy(GameObject player, GameObject enemy) {
		return player.getX() + player.getWidth() > enemy.getX() && player.getX() < enemy.getX() + enemy.getWidth()
				&& player.getY() + player.getHeight() > enemy.getY()
				&& player.getY() < enemy.getY() + enemy.getHeight();
	}

	// Method to check for platform collisions
	public void checkPlatformCollision(List<Platform> platforms) {
		// Reset onGround before checking collisions
		onGround = false;
		try {
			for (Platform platform : platforms) {

				// Side collision detection comes first to handle side collisions while grounded
				// Left side collision detection (Player's right side hitting the left side of
				// the platform)
				if (x + width + velocityX >= platform.getX() && x + width <= platform.getX() && // Right side of player
																								// hitting left side of
																								// platform
						y + height > platform.getY() && y < platform.getY() + platform.getHeight()) { // Vertical
																										// overlap
					x = platform.getX() - width; // Snap the player to the left side of the platform
					velocityX = 0; // Stop further movement in the right direction
					continue; // Skip the rest of the loop for this platform
				}

				// Right side collision detection (Player's left side hitting the right side of
				// the platform)
				if (x + velocityX <= platform.getX() + platform.getWidth() && x >= platform.getX() + platform.getWidth()
						&& // Left
							// side
							// of
							// player
							// hitting
							// right
							// side
							// of
							// platform
						y + height > platform.getY() && y < platform.getY() + platform.getHeight()) { // Vertical
																										// overlap
					x = platform.getX() + platform.getWidth(); // Snap the player to the right side of the platform
					velocityX = 0; // Stop further movement in the left direction
					continue; // Skip the rest of the loop for this platform
				}

				// Top collision detection (when landing on a platform)
				if ((y + height) <= platform.getY() && (y + height + velocityY) >= platform.getY() && // Player is
																										// falling
																										// onto the
																										// platform
						x + width > platform.getX() && x < platform.getX() + platform.getWidth()) { // Horizontal
																									// overlap
																									// with platform

					// Snap the player to the top of the platform
					y = platform.getY() - height;
					velocityY = 0; // Stop downward movement
					onGround = true; // Mark the player as grounded
				}

				// Bottom collision detection (hitting the bottom of the platform while jumping)
				if (y + velocityY <= platform.getY() + platform.getHeight() && y >= platform.getY() && // Player's head
																										// is
																										// near the
																										// bottom
																										// of the
																										// platform
						x + width > platform.getX() && x < platform.getX() + platform.getWidth()) { // Horizontal
																									// overlap
					y = platform.getY() + platform.getHeight(); // Snap the player to below the platform
					velocityY = 0; // Stop upward movement
				}
			}

		} catch (Exception ex) {
			ex.getStackTrace();
		}
	}

//    // Check for collisions with spikes using an iterator
//    public void checkSpikeCollision(List<Spike> spikes) {
//        Iterator<Spike> iterator = spikes.iterator();
//        while (iterator.hasNext()) {
//            Spike spike = iterator.next();
//            if (isColliding(this, spike)) {
//                loseLife();
//                break;  // Exit after one life is lost
//            }
//        }
//    }

	@Override
	public void die() {
		alive = false;
		loseLife(); // Use the existing lose life logic
	}

	// Method to reset the player's position (when they "die")
	public void resetPosition() {
		this.x = startX;
		this.y = startY;
		velocityX = 0;
		velocityY = 0;
		onGround = false;
		alive = true;
	}

	public int getLives() {
		return lives;
	}

	public void setSpawn(int newStartX, int newStartY) {
		startX = newStartX;
		startY = newStartY;
	}

	// Method to apply gravity
	public void applyGravity() {
		if (!onGround) {
			velocityY += gravity; // Apply gravity effect when not on the ground
		}
	}

	// Add a setter for lives if needed
	public void setLives(int lives) {
		this.lives = lives;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		this.velocityX = 0;
		this.velocityY = 0;
	}

	// Method to update the player's movement
	public void update(List<Platform> platforms, List<Spike> spikes, List<Enemy> enemies, Goal goal, Game game) {
		// Apply gravity before collision detection to allow upward movement
		applyGravity();

		// Update player's vertical position first to handle jumps and gravity
		y += velocityY;

		// Check for platform collisions after updating position
		checkPlatformCollision(platforms);

		// checks to see if the player entered a platform diagnoally
		for (Platform platform : platforms) {
			snapPlayerToTopIfInside(platform);
		}
	
		// Check for enemy collisions after updating position\
		if (!game.isLevelEditorVisible) {
			if (hasFallenOffScreen(game.getHeight())) {
				die();
			}
			
			checkEnemyKill(enemies);

			if (goal != null)
				checkGoal(goal, game);// New code Testing
		}

		// Update horizontal position after collision detection
		x += velocityX;

	}

	public void snapPlayerToTopIfInside(GameObject object) {
		// Calculate player bounds
		int playerLeft = this.getX();
		int playerRight = this.getX() + this.getWidth();
		int playerTop = this.getY();
		int playerBottom = this.getY() + this.getHeight();

		// Calculate object bounds
		int objectLeft = object.getX();
		int objectRight = object.getX() + object.getWidth();
		int objectTop = object.getY();
		int objectBottom = object.getY() + object.getHeight();

		// Check if the player is intersecting with the object
		boolean isIntersectingHorizontally = (playerRight > objectLeft && playerLeft < objectRight);
		boolean isIntersectingVertically = (playerBottom > objectTop && playerTop < objectBottom);

		// If the player is inside the object, snap to the top of the object
		if (isIntersectingHorizontally && isIntersectingVertically) {
			// Only snap if the player is falling or moving downward (velocityY > 0)
			if (this.getVelocityY() > 0) {
				this.setY(objectTop - this.getHeight() - 2); // Snap player to the top of the object
				this.setVelocityY(0); // Stop downward movement
			}
		}
	}

	private void checkGoal(Goal goal, Game game) {
		if (hitDetection(goal)) {
			String nextLevel;
			if (game.currentLevelPath.equals(game.DEFAULT_LEVEL)) {
				nextLevel = stageFolder + "level_1.txt";
			} else {
				String first = game.currentLevelPath.split("_")[0];
				first = first.split("/")[1];
				nextLevel = stageFolder+first.split("_")[0] + "_" + (++levelCount) + ".txt";
//                System.out.println(debug +","+ nextLevel);
			}
			// quick check to confirm that the file exists
//            System.out.println("Trying to load "+nextLevel);
			File file = new File(nextLevel);
			if(!file.exists() && !stageChecked) {
				stageFolder = "stage_" + (++stageCount) + "/";
				stageChecked = true;
				System.out.println("In this if statement"+stageFolder);
				checkGoal(goal,game);
				return;
			}
				
			if (!file.exists()) {
				nextLevel = resetLevel; // if it doesn't move to the default level
				System.out.println(resetLevel);
				levelCount = 1;
			}
			try {
//            	System.out.println("Actually loading "+nextLevel);
				game.nextLevel(nextLevel);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getStageFolder() {
		return stageFolder;
	}

	public static void setStageFolder(String stageFolder) {
		Player.stageFolder = stageFolder;
	}

	public void setGame(Game game) {
		this.game = game;

	}
}
