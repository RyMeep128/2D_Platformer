import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GameObject {
    protected int x; // X position of the object
    protected int y; // Y position of the object
    protected int width;
    protected int height;
    protected String name;
    protected Image objectImage;
 // Camera's visible bounds

    
    // Constructor
    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = "Default Creation";
        
    }
    
    public boolean isVisible(int cameraLeft, int cameraRight, int cameraTop, int cameraBottom) {
        return (x + width >= cameraLeft && x <= cameraRight) && // Horizontal visibility
               (y + height >= cameraTop && y <= cameraBottom);  // Vertical visibility
    }
    
    // Constructor
    public GameObject(int x, int y, int width, int height, String name) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
        try {
            objectImage = ImageIO.read(new File("assets/"+name+".png"));
        } catch (IOException e) {
            System.err.println("Error loading object image.");
            e.printStackTrace();
        }
    }
    
    // toWrite method to return the formatted string
    public String toWrite(String objectType) {
        return objectType + "," + x + "," + y + "," + width + "," + height + ","+name+"\n";
    }
    
    public void setX(int newX) {
    	this.x = newX;
    }
    public void setY(int newY) {
    	this.y = newY;
    }
    
    /**
     * Checks if this GameObject is within the specified grid cell.
     * @param gridX The x-coordinate of the top-left corner of the grid cell.
     * @param gridY The y-coordinate of the top-left corner of the grid cell.
     * @param cellSize The size of each grid cell.
     * @return true if the GameObject overlaps with the specified grid cell, false otherwise.
     */
    public boolean isInGridCell(int gridX, int gridY, int cellSize) {
        // Calculate the bounds of the grid cell
        int cellRight = gridX + cellSize;
        int cellBottom = gridY + cellSize;

        // Check if the GameObject intersects with the grid cell
        boolean intersects = (x < cellRight && x + width > gridX) && (y < cellBottom && y + height > gridY);

        return intersects;
    }
    
    public void draw(Graphics g, int xOffset) {
        if (objectImage != null) {
            g.drawImage(objectImage, x - xOffset, y, width, height, null);
        } else {
            // Draw a simple Pink rectangle if the image fails to load
            g.setColor(Color.PINK);
            g.fillRect(x- xOffset, y, width, height);
        }
    }

    // Method to check if this object is colliding with another object
    public boolean hitDetection(GameObject other) {
        return this.x < other.x + other.width &&
               this.x + this.width > other.x &&
               this.y < other.y + other.height &&
               this.y + this.height > other.y;
    }

    // Method to detect specific sides of the collision
    public String detectCollisionSide(GameObject other) {
        if (this.hitDetection(other)) {
            // Check top collision (this is below other)
            if (this.y + this.height <= other.y + other.height / 2) {
                return "top";
            }
            // Check bottom collision (this is above other)
            if (this.y >= other.y + other.height / 2) {
                return "bottom";
            }
            // Check right collision (this is to the left of other)
            if (this.x + this.width <= other.x + other.width / 2) {
                return "right";
            }
            // Check left collision (this is to the right of other)
            if (this.x >= other.x + other.width / 2) {
                return "left";
            }
        }
        return "none"; // No collision detected
    }
    
    public boolean isInside( GameObject outer) {
        // Get the boundaries of the inner object
        int innerLeft = this.getX();
        int innerRight = this.getX() + this.getWidth();
        int innerTop = this.getY();
        int innerBottom = this.getY() + this.getHeight();

        // Get the boundaries of the outer object
        int outerLeft = outer.getX();
        int outerRight = outer.getX() + outer.getWidth();
        int outerTop = outer.getY();
        int outerBottom = outer.getY() + outer.getHeight();

        // Check if the inner object is completely inside the outer object's boundaries
        return ((innerLeft >= outerLeft && innerRight <= outerRight)) ||
               ((innerTop >= outerTop && innerBottom <= outerBottom));
    }


    // Getters for position and size
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
