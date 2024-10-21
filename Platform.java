import java.awt.Color;
import java.awt.Graphics;

public class Platform extends MovableObject {

	// Constructor for the platform
	public Platform(int x, int y, int width, int height) {
		super(x, y, width, height,0,"Default_Platform");
	}
	
    public Platform(int x, int y) {
        super(x, y, Game.DEFAULT_GRID_SIZE, Game.DEFAULT_GRID_SIZE,0, "Block_Object");
    }
    
    public Platform(int x, int y, String name) {
        super(x, y, Game.DEFAULT_GRID_SIZE, Game.DEFAULT_GRID_SIZE,0, name);
    }
    
    public Platform(int x, int y, int speed, String name) {
        super(x, y, Game.DEFAULT_GRID_SIZE, Game.DEFAULT_GRID_SIZE,speed, name);
    }

	public Platform(int x, int y, int width, int height, String name) {
		super(x, y, width, height,0, name);
	}

	// Method to draw the platform relative to the camera's position
	public void draw(Graphics g, int xOffset) {
		super.draw(g, xOffset);
		if (this.objectImage == null) {
			switch (name) {
			case ("Default_Platform"):
				g.setColor(new Color(104, 129, 143)); // Set the color for the platform
				break;
			case ("Ground"):
				g.setColor(new Color(0, 66, 22));// RIGHT here, trying to change the colors of different platforms
				break;
			case ("Block_Object"):
				g.setColor(Color.ORANGE);
				break;
			default:
				g.setColor(new Color(0, 66, 22));
				break;
			}

			g.fillRect(x - xOffset, y, width, height); // Draws the platform relative to the camera
		}
	}

	public void setWidth(int newWidth) {
		width = newWidth;
	}

	public void setX(int newX) {
		this.x = newX;

	}

	// Platforms do not need special behavior for now, just use the basic GameObject
	// functionality
}
