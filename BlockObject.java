import java.awt.Color;
import java.awt.Graphics2D;

public class BlockObject extends GameObject {

	
    // Constructor for BlockObject
    public BlockObject(int x, int y, int width, int height) {
        super(x, y, width, height, "Block_Object");
    }
    
    public BlockObject(int x, int y) {
        super(x, y, Game.DEFAULT_GRID_SIZE, Game.DEFAULT_GRID_SIZE, "Block_Object");
    }

    // Override the draw method to render the block
    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.GRAY); // Default color for the block
        g2d.fillRect(x, y, width, height); // Draw the block as a filled rectangle
    }


    // Additional methods if you need specific behavior for BlockObject
    // For example, if you want this block to have special interactions, you can add methods here.
}
