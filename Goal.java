import java.awt.Color;
import java.awt.Graphics;

public class Goal extends GameObject {

    public Goal(int x, int y, int width, int height) {
        super(x, y, width, height, "Goal");  // Use GameObject's constructor for position and size
    }

    // Draw the goal as a hollow yellow square
    public void draw(Graphics g, int xOffset) {
    	super.draw(g, xOffset);
    	if (this.objectImage == null) {
    		g.setColor(new Color(194, 233, 255));
    		g.fillRect(x- xOffset, y, width, height);
        	g.setColor(new Color(255, 247, 0));
            g.drawRect(x- xOffset, y, width, height);
        }
    }
}
