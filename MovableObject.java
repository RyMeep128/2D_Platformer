import java.util.List;

public class MovableObject extends GameObject {
    protected int velocityX; // Horizontal velocity
    protected int velocityY; // Vertical velocity
    protected int speed;
    protected int startX, startY;  // Starting positions moved here
    boolean moveRight, moveLeft;

    public MovableObject(int x, int y, int width, int height, int speed, String name) {
        super(x, y, width, height, name);
        this.speed = speed;
        this.velocityX = 0;
        this.velocityY = 0;
        this.startX = x;  // Initialize startX
        this.startY = y;  // Initialize startY
        this.moveRight = true;
    }
    
    // Getters for startX and startY
    public int getStartX() {
        return startX;
    }
   
    public int getStartY() {
        return startY;
    }
    
 // Check if player has fallen off the screen (add this check in the update loop)
    public boolean hasFallenOffScreen(int screenHeight) {
        return getY() > screenHeight;
    }
    
    
    
    

    // Method to detect collision with a platform
    public String detectCollision(Platform platform) {
        // Top collision (landing on platform)
        if ((y + height + velocityY) >= platform.getY() && 
            y + height <= platform.getY() && 
            x + width > platform.getX() && 
            x < platform.getX() + platform.getWidth()) {
            return "top";
        }

        // Left collision (hitting the right side of the platform)
        if (x + width + velocityX > platform.getX() && // Check right side of the player overlaps left side of the platform
            x + width <= platform.getX() + 1 && // Ensure it's a real side collision (within a small tolerance)
            y + height > platform.getY() && 
            y < platform.getY() + platform.getHeight()) {
            return "left";
        }

        // Right collision (hitting the left side of the platform)
        if (x + velocityX < platform.getX() + platform.getWidth() && // Check left side of the player overlaps right side of the platform
            x >= platform.getX() + platform.getWidth() - 1 && // Ensure it's a real side collision (within a small tolerance)
            y + height > platform.getY() && 
            y < platform.getY() + platform.getHeight()) {
            return "right";
        }

        // Bottom collision (hitting the bottom of the platform while jumping)
        if (y + velocityY <= platform.getY() + platform.getHeight() && 
            y >= platform.getY() && 
            x + width > platform.getX() && 
            x < platform.getX() + platform.getWidth()) {
            return "bottom";
        }

        // No collision
        return "none";
    }
    
    @Override
    public String toWrite(String objectType) {
        return objectType + "," + startX + "," + startY + "," + width + "," + height + ","+name+"\n";
    }
    
    public void move(List<Platform> platforms) {
		if (moveRight)
			moveRight();
		else if (moveLeft)
			moveLeft();

		for (Platform platform : platforms) {
			 if (x + width + velocityX >= platform.getX() && x + width <= platform.getX() && // Right side of player hitting left side of platform
		                y + height > platform.getY() && y < platform.getY() + platform.getHeight()) { // Vertical overlap
		                x = platform.getX() - width; // Snap the player to the left side of the platform
		                moveLeft(); // Stop further movement in the right direction

		                continue; // Skip the rest of the loop for this platform
		            }

		            // Right side collision detection (Player's left side hitting the right side of the platform)
		            if (x + velocityX <= platform.getX() + platform.getWidth() && x >= platform.getX() + platform.getWidth() && // Left side of player hitting right side of platform
		                y + height > platform.getY() && y < platform.getY() + platform.getHeight()) { // Vertical overlap
		                x = platform.getX() + platform.getWidth(); // Snap the player to the right side of the platform
		                moveRight(); // Stop further movement in the left direction
		                continue; // Skip the rest of the loop for this platform
		            }
		}
	}
    
	// Method to move the player right
	public void moveRight() {
		velocityX = speed; // Move right by applying positive horizontal velocity
		moveLeft = false;
		moveRight = true;
	}

	// Method to move the player left
	public void moveLeft() {
		moveLeft = true;
		moveRight = false;
		velocityX = -speed; // Move left by applying negative horizontal velocity
	}
    
	public int getVelocityX() {
		return velocityX;
	}

	public void setVelocityX(int velocityX) {
		this.velocityX = velocityX;
	}

	public int getVelocityY() {
		return velocityY;
	}

	public void setVelocityY(int velocityY) {
		this.velocityY = velocityY;
	}

	public void stopMoving() {
		velocityX = 0;
		velocityY = 0;
	}
}
