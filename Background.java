import java.awt.Graphics;
import java.awt.Image;

public class Background {
	private Image backgroundImage;
	private int imageWidth;
	private int imageHeight;

	public Background(Image backgroundImage, int imageWidth, int imageHeight) {
		this.backgroundImage = backgroundImage;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	public void draw(Graphics g, int cameraX, int cameraY) {
		// Define parallax factor (background moves slower than the camera)
		double parallaxFactor = 0.5;

		// Calculate the background's position, applying the parallax factor
		int bgX = (int) (-cameraX * parallaxFactor) % imageWidth;
		int bgY = (int) (-cameraY * parallaxFactor) % imageHeight;

		// Draw the background repeatedly to fill the screen
		for (int x = bgX - imageWidth; x < g.getClipBounds().width; x += imageWidth) {
			for (int y = bgY - imageHeight; y < g.getClipBounds().height; y += imageHeight) {
				g.drawImage(backgroundImage, x, y, null);
			}
		}
	}
}
