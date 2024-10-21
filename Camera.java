public class Camera {

    private int xOffset;
    private int screenWidth;

    public Camera(int screenWidth) {
        this.screenWidth = screenWidth;
        this.xOffset = 0; // Initial offset is 0, meaning the camera starts at the far left of the level
    }

    // Update the camera position based on the player's position
    public void update(Player player) {
        // Center the camera around the player, keeping the player relatively centered
        xOffset = player.getX() - screenWidth / 2 + player.getWidth() / 2;
    }

    // Get the current x offset of the camera (used for drawing objects relative to the camera)
    public int getXOffset() {
        return xOffset;
    }
}
