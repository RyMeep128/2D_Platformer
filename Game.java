import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Game extends JPanel
		implements Runnable, KeyListener, MouseWheelListener, MouseMotionListener, MouseListener {

	/**
	 * Things to add: add boxes Enemies moving Collectables: keys and doors, or
	 * something
	 * 
	 * 
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Thread gameThread;
	private boolean isRunning = false;
	private boolean isResetting = false; // Flag to handle resetting
	private boolean showHelp = false; // Show help information by default
	private boolean loading = false;
	public static final int DEFAULT_GRID_SIZE = 32;
	private Image backgroundImage;
	private boolean newEnemyFlag = false;

	private BufferStrategy bufferStrategy;
	protected String currentLevelPath;

	private int gridSize = DEFAULT_GRID_SIZE; // Size of each grid cell (32x32)

	// Create the player object
	protected Player player;

	private Goal goal = null; // There will be only one goal at a time

	// Create a list of platforms
	private List<Platform> platforms;

	private List<Spike> spikes; // Add a list for spikes

	private List<Enemy> enemies; // Add a list for enemies

	// Level editor state
	private String currentSelection = "Player Spawn"; // Start with Player Spawn
	protected boolean isLevelEditorVisible = true; // Track if the level editor is visible
	private String[] items = { "Player Spawn", "Spikes", "Enemies", "Goal", "Ground", "Block", "Flyer"}; // Items
																											// for
	// cycling in
	// the level //
	// editor
	private int currentItemIndex = 0; // Track the current item being selected
	// For platform dragging
	private Platform currentPlatform = null; // Track the platform being dragged
	private boolean isDraggingPlatform = false; // Is the user dragging to resize the platform?

	// Flags for movement input
	private boolean moveLeft = false;
	private boolean moveRight = false;

	private static final int DEFAULT_GROUND_HEIGHT = 450;
	protected final String DEFAULT_LEVEL = "tutorialLevels/default_level.txt";
	private static final int DEFAULT_LIVES = 3;

	protected Camera camera;

	private LevelData currentLevelData;
	protected GameWindow gameWindow;
	private Background background;

	// Constructor
	public Game(int screenWidth, GameWindow gameWindow) {
		this.gameWindow = gameWindow;

		try {
			backgroundImage = ImageIO.read(new File("assets/Background.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		background = new Background(backgroundImage, screenWidth, gameWindow.getHeight());
		// image file name

		// Initialize the camera
		camera = new Camera(screenWidth);
		try {
			loadLevel(DEFAULT_LEVEL); // TRying to get this to work
		} catch (FileNotFoundException ex) {

		}

		// Add key listener to capture keyboard inputs
		setFocusable(true);
		addKeyListener(this);
		addMouseWheelListener(this);
		addMouseMotionListener(this); // Register mouse motion listener
		requestFocusInWindow(); // Ensures the game panel is focused and can receive input events

	}

	public void nextLevel(String levelPath) throws FileNotFoundException {
		setLoading(true); // Set the game to loading mode
		Thread loadingThread = new Thread(new LevelLoader(this, levelPath));
		loadingThread.start();
		try {
			loadingThread.join(); // Wait for the loading thread to finish
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		setLoading(false); // Ensure the game is set to running mode again
		currentLevelPath = levelPath; // Update the current level path after successfully loading

	}

	// This method will start the game loop
	public void start() {
		// Set the background color of the game canvas
		gameWindow.createBufferStrategy(3);
		bufferStrategy = gameWindow.getBufferStrategy();
		isRunning = true;
		gameThread = new Thread(this);
		gameThread.start();
	}

	// The game loop method
	@Override
	public void run() {
		while (isRunning) {
			if (loading) {
				// Use the buffer strategy to draw the loading screen
				Graphics2D g2d = (Graphics2D) bufferStrategy.getDrawGraphics();
				drawLoadingScreen(g2d);
				g2d.dispose();
				bufferStrategy.show(); // Show the drawn frame
			} else {
				if (!isResetting) {
					// Update game state (handle movement, gravity, and jumping)
					update();

					// Repaint the game screen
					repaint();
				}
			}
			// Add a short delay to control the frame rate
			try {
				Thread.sleep(16); // Roughly 60 FPS
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	int timer = 0;
	public void timer() {
		if(timer>=500) {
			for (Enemy enemy : enemies) {
				if(enemy instanceof Flyer)
					((Flyer)enemy).setMadeEnemy(false);
			}
			timer=0;
		}else timer++;
	}

	// Game loop and update method
	public void update() {

		int buffer = 200; // buffer to allow for a little more freedom
		int cameraLeft = camera.getXOffset() - buffer;
		int cameraRight = camera.getXOffset() + 800 + buffer;
		int cameraTop = 0;
		int cameraBottom = 0 + 800;

		// Make sure player is not null during the game update cycle
		if (player != null && !isResetting) {
			
			timer();
//			try {
			player.update(platforms, spikes, enemies, goal, this);
//			} catch (Exception e) {
			// TODO Auto-generated catch block
//				e.printStackTrace();
//				this.isRunning = false;
//			} // Update player and other game objects

			if (!isLevelEditorVisible) {
				// Update each spike with the player and enemies
				for (Spike spike : spikes) {
					if (spike.isVisible(cameraLeft, cameraRight, cameraTop, cameraBottom))
						spike.update(player, enemies);
				}
				int creationX = -10;
				int creationY = -10;

				// Update all enemies
				for (Enemy enemy : enemies) {
					if (enemy.isVisible(cameraLeft, cameraRight, cameraTop, cameraBottom))
						enemy.update(platforms);
					if (enemy.hasFallenOffScreen(getHeight())) {
						enemies.remove(enemy);
						break;
					}
					if(enemy instanceof Flyer) {
						if(((Flyer)enemy).checkCreateEnemy()) {
							newEnemyFlag = true;
							creationX = enemy.getX();
							creationY = enemy.getY() + enemy.getHeight();
							((Flyer) enemy).setcreateNewEnemy(false);
						}
					}
				}
				if(newEnemyFlag) {
					enemies.add(new Enemy(creationX, creationY, 4));
					newEnemyFlag = false;
					
				}
				

			}

			// Handle player movement based on input
			if (moveLeft) {
				player.moveLeft();
			} else if (moveRight) {
				player.moveRight();
			} else {
				player.stopMoving(); // Stop horizontal movement when no input is detected
			}
			// Update the camera to follow the player
			camera.update(player);

		}

	}

	public void enemyCheck() {
		// Update enemies and remove any that have died
		Iterator<Enemy> enemyIterator = enemies.iterator();
		while (enemyIterator.hasNext()) {
			Enemy enemy = enemyIterator.next();
			if (!enemy.isAlive()) {
				enemyIterator.remove(); // Remove the enemy if it is not alive
			} else {
				enemy.update(platforms); // Update the enemy's position and behavior if it is alive
			}
		}

	}

	public void drawGrid(Graphics2D g2d) {
		g2d.setColor(Color.LIGHT_GRAY); // Set the color for the grid lines
		for (int x = 0; x < getWidth(); x += DEFAULT_GRID_SIZE) {
			g2d.drawLine(x, 0, x, getHeight());
		}
		for (int y = 0; y < getHeight(); y += DEFAULT_GRID_SIZE) {
			g2d.drawLine(0, y, getWidth(), y);
		}
	}

	public void resetGame() {
		// Set the flag to true to prevent updates during reset
		isResetting = true;

		player.setGame(this);
		moveLeft = false;
		moveRight = false;
		// Check if the player object was loaded correctly
		if (player.equals(null)) {
			// If no player was loaded, create a default player to avoid
			// NullPointerException
			player = new Player(50, 350, 50, 50, 5, 1, this); // Default player position and settings
		}

		// Reset movable objects to their starting positions
		if (player != null) {
			player.setX(player.getStartX());
			player.setY(player.getStartY());
		}
		for (Enemy enemy : enemies) {
			enemy.setX(enemy.getStartX());
			enemy.setY(enemy.getStartY());
		}

		// Reset any other game state as needed

		// After reset is complete, allow updates again
		isResetting = false;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		int xOffset = camera.getXOffset();
		int cameraLeft = camera.getXOffset();
		int cameraRight = camera.getXOffset() + 800;
		int cameraTop = 0;
		int cameraBottom = 0 + 600;

		if (backgroundImage == null) {
			setBackground(new Color(194, 233, 255));
		}
		// Draw the background image (adjust width and height as needed)
		if (backgroundImage != null) {
			background.draw(g, xOffset, 0);

		}

		// Draw platforms
		for (Platform platform : platforms) {
			if (platform.isVisible(cameraLeft, cameraRight, cameraTop, cameraBottom))
				platform.draw(g, xOffset);
		}

		// Draw spikes
		for (Spike spike : spikes) {
			if (spike.isVisible(cameraLeft, cameraRight, cameraTop, cameraBottom))
				spike.draw(g, xOffset);
		}

		// Draw enemies
		for (Enemy enemy : enemies) {
			if (enemy.isVisible(cameraLeft, cameraRight, cameraTop, cameraBottom))
				enemy.draw(g, xOffset);
		}

		if (goal != null) {
			goal.draw(g, camera.getXOffset());
		}

		// Draw player
		player.draw(g, xOffset);
		// Only display level editor elements if the editor is visible

		// Draw the grid lines for the level editor
		if (isLevelEditorVisible) {

//			drawGrid(g2d);
			DrawCurrentItem((Graphics2D) g);
		}

		// Draw the relevant help information
		if (showHelp) {
			if (isLevelEditorVisible) {
				drawLevelEditorHelp(g2d);
			} else {
				drawGameplayHelp(g2d);
			}
		}

	}

	private void DrawCurrentItem(Graphics2D g) {
		String[] selectionText = { "Current Selection:", };
		selectionText = appendArrays(selectionText, items);

		int boxX = 0; // Center the box horizontally
		int boxY = 10; // Center the box vertically
		int lineHeight = 20; // Height between lines of text
		int boxWidth = 150;
		int buffer = lineHeight;
		int boxHeight = selectionText.length * lineHeight + buffer;
		int startY = boxY + 20; // Adjusted starting y-coordinate for the help text

		g.setColor(new Color(0, 0, 0, 150)); // Semi-transparent background
		g.fillRect(boxX, boxY, boxWidth, boxHeight);

		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.PLAIN, 14));

		for (int i = 0; i < selectionText.length; i++) {
			g.drawString(selectionText[i], boxX + 10, startY + (i * lineHeight));
		}

		g.setColor(new Color(255, 255, 82, 150)); // Semi-transparent background
		g.fillRect(boxX, startY + lineHeight * currentItemIndex + 5, boxWidth, lineHeight);

	}

	public static String[] appendArrays(String[] array1, String[] array2) {
		// Create a new array with length equal to the sum of the lengths of both arrays
		String[] result = new String[array1.length + array2.length];

		// Copy the elements of the first array into the result array
		System.arraycopy(array1, 0, result, 0, array1.length);

		// Copy the elements of the second array into the result array after the first
		System.arraycopy(array2, 0, result, array1.length, array2.length);

		return result;
	}

	private void drawGameplayHelp(Graphics2D g2d) {
		int boxWidth = 300;
		int boxHeight = 140;
		int boxX = (getWidth() - boxWidth) / 2; // Center the box horizontally
		int boxY = 0; // Center the box vertically

		g2d.setColor(new Color(0, 0, 0, 150)); // Semi-transparent background
		g2d.fillRect(boxX, boxY, boxWidth, boxHeight);

		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Arial", Font.PLAIN, 14));

		int startY = boxY + 20; // Adjusted starting y-coordinate for the movement help text
		int lineHeight = 20; // Height between lines of text

		String[] movementText = { "MOVEMENT CONTROLS:", "---------------------------", "Use Arrow Keys or WASD to move",
				"Press SPACE to jump", "Press F3 to switch to Editor", "Press H to hide/show this help" };

		for (int i = 0; i < movementText.length; i++) {
			g2d.drawString(movementText[i], boxX + 10, startY + (i * lineHeight));
		}
	}

	private void drawLevelEditorHelp(Graphics2D g2d) {
		String[] helpText = { "LEVEL EDITOR HELP:", "---------------------------", "Press F3 to toggle level editor",
				"Scroll Mouse Wheel to cycle items", "Left-Click: Place the selected item",
				"Left-Click and Hold: Drag to resize platforms", "Right-Click: Delete the front-most item",
				"Press | to save your current Level", "Press L to load a Level", "Press H to hide/show this help" };

		int lineHeight = 20; // Height between lines of text
		int boxWidth = 300;
		int buffer = lineHeight;
		int boxHeight = helpText.length * lineHeight + buffer;
		int boxX = (getWidth() - boxWidth) / 2; // Center the box horizontally
		int boxY = 0; // Center the box vertically

		g2d.setColor(new Color(0, 0, 0, 150)); // Semi-transparent background
		g2d.fillRect(boxX, boxY, boxWidth, boxHeight);

		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Arial", Font.PLAIN, 14));

		int startY = boxY + 20; // Adjusted starting y-coordinate for the help text

		for (int i = 0; i < helpText.length; i++) {
			g2d.drawString(helpText[i], boxX + 10, startY + (i * lineHeight));
		}
	}

	// Helper method to check if the mouse click is over an object
	private boolean isMouseOverObject(int mouseX, int mouseY, GameObject object) {
		if (mouseX >= object.getX() && mouseX <= object.getX() + object.getWidth() && mouseY >= object.getY()
				&& mouseY <= object.getY() + object.getHeight()) {
			return true;
		} else {
			return false;
		}
	}

	// Delete the topmost item that the mouse is hovering over
	private void deleteItem(int adjustedX, int adjustedY, int actualMouseX, int actualMouseY) {
		System.out.println("Mouse at (" + actualMouseX + "," + actualMouseY + ") Adjusted and mapped to (" + adjustedX
				+ "," + adjustedY + ")");
//		switch (currentSelection) {
//
//		case ("Spikes"):
		// Iterate through the lists in reverse to remove the front-most object first
		for (int i = spikes.size() - 1; i >= 0; i--) {
			Spike spike = spikes.get(i);
			if (isMouseOverObject(actualMouseX, actualMouseY, spike)) {
				spikes.remove(i);
				markCellFree(adjustedX, adjustedY);
				return; // Exit after deleting the top-most object
			}
		}
//			break;
//		case ("Ground"):
//		case ("Platforms"):
//		case ("Block"):
		for (int i = platforms.size() - 1; i >= 0; i--) {
			Platform platform = platforms.get(i);
			if (isMouseOverObject(actualMouseX, actualMouseY, platform)) {
				platforms.remove(i);
				markCellFree(adjustedX, adjustedY);
				return;
			}
		}
//			break;
//		case ("Enemies"):
		for (int i = enemies.size() - 1; i >= 0; i--) {
			Enemy enemy = enemies.get(i);
			if (isMouseOverObject(actualMouseX, actualMouseY, enemy)) {
				enemies.remove(i);
				markCellFree(adjustedX, adjustedY);
				return;
			}
		}
//			break;
//		case ("Goal"):
		// Check for goal deletion
		if (goal != null && isMouseOverObject(actualMouseX, actualMouseY, goal)) {
			markCellFree(adjustedX, adjustedY);
			goal = null;
		}
//			break;
//		}
//		System.out.println("Deleted object at: (" + mouseX + ", " + mouseY + ")");
	}

	// Helper method to merge adjacent grounds
	private void mergeAdjacentGrounds() {
		for (int i = 0; i < platforms.size(); i++) {
			Platform p1 = platforms.get(i);
			if (p1.getY() == DEFAULT_GROUND_HEIGHT) { // Check only ground platforms
				for (int j = i + 1; j < platforms.size(); j++) {
					Platform p2 = platforms.get(j);
					if (p2.getY() == DEFAULT_GROUND_HEIGHT) { // Ensure both are ground platforms

						// Check if p1 and p2 are adjacent or overlapping
						if ((p1.getX() + p1.getWidth() >= p2.getX() && p1.getX() <= p2.getX() + p2.getWidth())
								|| (p2.getX() + p2.getWidth() >= p1.getX() && p2.getX() <= p1.getX() + p1.getWidth())) {

							// Merge them into a larger platform
							int newX = Math.min(p1.getX(), p2.getX());
							int newWidth = Math.max(p1.getX() + p1.getWidth(), p2.getX() + p2.getWidth()) - newX;

							Platform mergedGround = new Platform(newX, DEFAULT_GROUND_HEIGHT, newWidth, 50, "Ground"); // Height
							// is
							// fixed
							platforms.add(mergedGround);

							// Remove the original platforms
							platforms.remove(p1);
							platforms.remove(p2);

							return; // Stop after merging one pair of platforms
						}
					}
				}
			}
		}
	}

	// Utility method to check if the mouse is over an item (spike, platform, enemy,
	// or player)
	private boolean isMouseOverItem(GameObject item, int mouseX, int mouseY) {
		return mouseX >= item.getX() && mouseX <= item.getX() + item.getWidth() && mouseY >= item.getY()
				&& mouseY <= item.getY() + item.getHeight();
	}

	// Helper method to find the closest platform under a given (x, y) position
	private Platform findClosestPlatform(int x, int y) {
		Platform closestPlatform = null;
		int closestDistance = Integer.MAX_VALUE;

		for (Platform platform : platforms) {
			if (x >= platform.getX() && x <= platform.getX() + platform.getWidth()) {
				// Platform is directly beneath the spike
				int distance = platform.getY() - y;
				if (distance >= 0 && distance < closestDistance) {
					closestDistance = distance;
					closestPlatform = platform;
				}
			}
		}
		return closestPlatform;
	}

	// Trigger the save popup menu
	public void triggerSavePopup() {
		// Show input dialog to enter a file name
		String fileName = JOptionPane.showInputDialog(null, "Enter file name for saving:", "Save Level",
				JOptionPane.QUESTION_MESSAGE);

		// If the user clicks Cancel or doesn't provide a file name
		if (fileName == null || fileName.trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Save canceled.", "Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// Add .txt extension if it's not provided
		if (!fileName.endsWith(".txt")) {
			fileName += ".txt";
		}

		// Save the level using the provided file name
		String filePath = Player.getStageFolder() + fileName;
		File file = new File(filePath);

		if (file.exists()) {
			// Ask for confirmation if the file already exists
			int overwriteConfirm = JOptionPane.showConfirmDialog(null, "File already exists. Overwrite?",
					"Confirm Overwrite", JOptionPane.YES_NO_OPTION);
			if (overwriteConfirm == JOptionPane.NO_OPTION) {
				JOptionPane.showMessageDialog(null, "Save canceled.", "Info", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}

		// Save the level using the file name
		LevelSaver.saveLevel(platforms, spikes, enemies, player, goal, filePath);

		// Notify the user that the level has been saved
		JOptionPane.showMessageDialog(null, "Level saved successfully as: " + fileName, "Save Complete",
				JOptionPane.INFORMATION_MESSAGE);
	}

	// Load the level using the LevelReader
	public void loadLevel(String fileName) throws FileNotFoundException {
		// Load the level data from the file

		currentLevelData = LevelReader.readLevel(fileName, this);
		if (this.player != null) {
			Player newPlayer = currentLevelData.getPlayer();
			this.player.setSpawn(newPlayer.getStartX(), newPlayer.getStartY());
			this.player.setPosition(newPlayer.getStartX(), newPlayer.getStartY());
		} else
			this.player = currentLevelData.getPlayer();
		this.platforms = currentLevelData.getPlatforms();
		this.spikes = currentLevelData.getSpikes();
		this.enemies = currentLevelData.getEnemies();
		for (Enemy enemy : enemies) {
			if(enemy instanceof Flyer)
				((Flyer)enemy).setGame(this);
		}
		
		this.goal = currentLevelData.getGoal();
		this.currentLevelPath = fileName;
		resetGame();
	}

	// Trigger the load popup menu
	public void triggerLoadPopup() {
		// Show input dialog to enter a file name
		String fileName = JOptionPane.showInputDialog(null, "Enter file name to load:", "Load Level",
				JOptionPane.QUESTION_MESSAGE);

		// If the user clicks Cancel or doesn't provide a file name
		if (fileName == null || fileName.trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Load canceled.", "Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// Add .txt extension if it's not provided
		if (!fileName.endsWith(".txt")) {
			fileName += ".txt";
		}

		// Check if the file exists
		String filePath = "levels/" + fileName;
		File file = new File(filePath);

		if (!file.exists()) {
			JOptionPane.showMessageDialog(null, "File not found: " + fileName, "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Load the level using the file name
		try {
			loadLevel(filePath);
		} catch (FileNotFoundException ex) {

		}

		// Notify the user that the level has been loaded
		JOptionPane.showMessageDialog(null, "Level loaded successfully from: " + fileName, "Load Complete",
				JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (isLevelEditorVisible) {
			// Scroll the mouse wheel up or down to cycle through items
			int notches = e.getWheelRotation();
			if (notches > 0) {
				currentItemIndex = (currentItemIndex + 1) % items.length; // Scroll forward
			} else if (notches < 0) {
				currentItemIndex = (currentItemIndex - 1 + items.length) % items.length; // Scroll backward
			}

			// Update the current selection based on the scrolled item
			currentSelection = items[currentItemIndex];
		}
	}

	// Key listener methods to detect key presses and releases
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		// Toggle level editor visibility when F3 is pressed
		if (keyCode == KeyEvent.VK_F3) {
			isLevelEditorVisible = !isLevelEditorVisible; // Toggle the editor visibility
		}

		// If left arrow or 'A' is pressed, move left
		if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
			moveLeft = true;
		}

		// If right arrow or 'D' is pressed, move right
		if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
			moveRight = true;
		}

		// If spacebar or 'W' is pressed, initiate jumping (if on the ground)
		if ((keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_K || keyCode == KeyEvent.VK_UP)
				&& player.isOnGround()) {
			player.jump();
		}

		// "L" button pressed (key code for 'L')
		if (e.getKeyCode() == KeyEvent.VK_L) {
			triggerLoadPopup(); // Show the load popup
		}

		// "|" button pressed (key code 220 for the "|" key)
		if (e.getKeyCode() == KeyEvent.VK_BACK_SLASH) {
			triggerSavePopup(); // Show the save popup
		}

		if (e.getKeyCode() == KeyEvent.VK_H) {
			showHelp = !showHelp; // Toggle help display
		}

		if (isLevelEditorVisible)
			if (e.getKeyCode() == KeyEvent.VK_MINUS) {
				platforms.removeAll(platforms);
				enemies.removeAll(enemies);
				spikes.removeAll(spikes);
				occupiedCells.removeAll(occupiedCells);
				platforms.add(new Platform(player.getX(), player.getY() + gridSize));
				markCellOccupied(player.getX(), player.getY() + gridSize);
			}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();

		// Stop moving left when the key is released
		if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
			moveLeft = false;
			player.stopMoving();
		}

		// Stop moving right when the key is released
		if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
			moveRight = false;
			player.stopMoving();
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Not used but required for KeyListener interface
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// Snap the current mouse position to the grid
		if (isLevelEditorVisible) {

			int mouseX = snapToGrid(e.getX() + camera.getXOffset());
			int mouseY = snapToGrid(e.getY());
			int actualMouseX = e.getX() + camera.getXOffset();
			int actualMouseY = e.getY();
			if (isLeftMousePressed) {// Check if the new position is different from the initial one to
				// avoid re-placing
//				System.out.println("Cell occupied before placing: " + isCellOccupied(mouseX, mouseY)+" ("+mouseX+","+mouseY+")");
				if (!isCellOccupied(mouseX, mouseY)) {
					placeObjectAt(mouseX, mouseY, actualMouseX, actualMouseY);
				}
			} else if (isRightMousePressed) {
				// Handle deleting objects when dragging with the right mouse button
				deleteItem(mouseX, mouseY, actualMouseX, actualMouseY);
			}
		}

	}

	private void placeObjectAt(int adjustedX, int adjustedY, int actualMouseX, int actualMouseY) {
		System.out.println("Mouse at (" + actualMouseX + "," + actualMouseY + ") Adjusted and mapped to (" + adjustedX
				+ "," + adjustedY + ")");
		switch (currentSelection) {
		case "Platforms":
			if (!isDraggingPlatform) {
				currentPlatform = new Platform(adjustedX, adjustedY, 1, 20);
				isDraggingPlatform = true;
			}
			break;
		case "Goal":
			markCellFree(goal.getX(), goal.getY());
			goal = new Goal(adjustedX, adjustedY, 32, 32); // Example size for the goal
			markCellOccupied(adjustedX, adjustedY);
			break;
		case "Spikes":
			 // Snap the spike to the closest platform
			Platform closestPlatform = findClosestPlatform(adjustedX, adjustedY);
			if (closestPlatform != null) {
				int spikeY = closestPlatform.getY() - DEFAULT_GRID_SIZE; // Position the spike on top of the platform
				spikes.add(new Spike(adjustedX, spikeY));
				markCellOccupied(adjustedX, adjustedY);
			}

			break;
		case "Enemies":
			// Add a new enemy
			enemies.add(new Enemy(adjustedX, adjustedY, 64, 32, 2)); // Example size for enemy
			markCellOccupied(adjustedX, adjustedY);
			break;
		case "Flyer":
			enemies.add(new Flyer(adjustedX, adjustedY, 32, 32, 2, this));
			break;

		case "Ground":
			placeGround(adjustedX, adjustedY);
			break;
		case "Player Spawn":
			// Update player spawn position (centered on mouse click)
			markCellFree(player.getX(), player.getY());
			player.setPosition(adjustedX - player.getWidth() / 2, adjustedY - player.getHeight() / 2);
			player.setSpawn(adjustedX - player.getWidth() / 2, adjustedY - player.getHeight() / 2);
			markCellOccupied(adjustedX, adjustedY);
			break;
		case "Block":
			platforms.add(new Platform(adjustedX, adjustedY));
			markCellOccupied(adjustedX, adjustedY);
			break;

		// Other cases for items
		}

	}

	private void placeGround(int adjustedX, int adjustedY) {
		if (isCellOccupied(adjustedX, adjustedY - gridSize)) {
			if (isGroundInGridCell(adjustedX, adjustedY - gridSize)
					|| isDirtInGridCell(adjustedX, adjustedY - gridSize)) {
				platforms.add(new Platform(adjustedX, adjustedY, "Dirt"));
				if (isCellOccupied(adjustedX, adjustedY + gridSize)
						&& isGroundInGridCell(adjustedX, adjustedY + gridSize)) {
					platforms.remove(getPlatformInGridCell(adjustedX, adjustedY + gridSize));
					platforms.add(new Platform(adjustedX, adjustedY + gridSize, "Dirt"));
				}
			}
		} else if (isCellOccupied(adjustedX, adjustedY + gridSize)
				&& isGroundInGridCell(adjustedX, adjustedY + gridSize)) {
			platforms.remove(getPlatformInGridCell(adjustedX, adjustedY + gridSize));
			platforms.add(new Platform(adjustedX, adjustedY + gridSize, "Dirt"));
			platforms.add(new Platform(adjustedX, adjustedY, "Ground"));
		} else {
			platforms.add(new Platform(adjustedX, adjustedY, "Ground"));
		}
		markCellOccupied(adjustedX, adjustedY);

	}

	public boolean isGroundInGridCell(int gridX, int gridY) {
		for (Platform platform : platforms) {
			if (platform.name.equals("Ground"))
				if (platform.isInGridCell(gridX, gridY, gridSize)) {
					return true;
				}
		}
		return false; // Return null if no object is found in the specified grid cell
	}

	public boolean isDirtInGridCell(int gridX, int gridY) {
		for (Platform platform : platforms) {
			if (platform.name.equals("Dirt"))
				if (platform.isInGridCell(gridX, gridY, gridSize)) {
					return true;
				}
		}
		return false; // Return null if no object is found in the specified grid cell
	}

	public Platform getPlatformInGridCell(int gridX, int gridY) {
		for (Platform platform : platforms) {
			if (platform.isInGridCell(gridX, gridY, gridSize)) {
				return platform;
			}
		}
		return null; // Return null if no object is found in the specified grid cell
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	private int snapToGrid(int coordinate) {
		return (coordinate / gridSize) * gridSize;
	}

	@Override
	public void mousePressed(MouseEvent e) {
//		int adjustedX = e.getX() + camera.getXOffset();
//		int adjustedY = e.getY();

		int adjustedX = snapToGrid(e.getX() + camera.getXOffset());
		int adjustedY = snapToGrid(e.getY());

		int actualMouseX = e.getX() + camera.getXOffset(); // Use the actual mouse position, not the snapped position
		int actualMouseY = e.getY();

		if (isLevelEditorVisible)
			if (e.getButton() == MouseEvent.BUTTON1) {
				isLeftMousePressed = true;
//				System.out.println("Cell occupied before placing: " + isCellOccupied(mouseX, mouseY)+" ("+mouseX+","+mouseY+")");
				if (!isCellOccupied(adjustedX, adjustedY)) {
					placeObjectAt(adjustedX, adjustedY, actualMouseX, actualMouseY);
					markCellOccupied(adjustedX, adjustedY);

				}

			} else if (e.getButton() == MouseEvent.BUTTON3) {
				// Right-click: delete the topmost item
				System.out.println("Right button clicked");
				isRightMousePressed = true;
				deleteItem(adjustedX, adjustedY, actualMouseX, actualMouseY);

			}
	}

	private boolean isLeftMousePressed = false;
	private boolean isRightMousePressed = false;

	@Override
	public void mouseReleased(MouseEvent e) {
		if (isLevelEditorVisible)
//			if (isDraggingPlatform && currentPlatform != null) {
//				// Finalize platform placement
//				platforms.add(currentPlatform);
//				isDraggingPlatform = false;
//				mergeAdjacentGrounds();
//				currentPlatform = null; // Reset the current platform
//			}
			if (SwingUtilities.isLeftMouseButton(e)) {
				isLeftMousePressed = false;
			} else if (SwingUtilities.isRightMouseButton(e)) {
				isRightMousePressed = false;
			}
	}
//	public void handleKeyPress(KeyEvent e, Graphics g) {
//	    if (activeTextBox != null) {
//	        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
//	            activeTextBox.removeLastCharacter(g);
//	        } else if (Character.isLetterOrDigit(e.getKeyChar()) || e.getKeyChar() == ' ') {
//	            activeTextBox.appendText(e.getKeyChar(), g);
//	        }
//	    }
//	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void restartGame() {
		try {
			System.out.println(player.resetLevel);
			loadLevel(player.resetLevel);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void drawLoadingScreen(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.setColor(Color.WHITE);
		g2d.drawString("Loading...", getWidth() / 2 - 30, getHeight() / 2);
	}

	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	private Set<String> occupiedCells = new HashSet<>();

	// Check if a cell is occupied
	private boolean isCellOccupied(int gridX, int gridY) {
		System.out.println("Checking to see if this cell is occupied: " + gridX + "," + gridY + " "
				+ occupiedCells.contains(gridX + "," + gridY));
		return occupiedCells.contains(gridX + "," + gridY);
	}

	// Mark a cell as occupied
	private void markCellOccupied(int gridX, int gridY) {
		System.out.println("Marking cell as occupied: " + gridX + "," + gridY);
		occupiedCells.add(gridX + "," + gridY);
	}

	// Remove a cell from the occupied set
	private void markCellFree(int gridX, int gridY) {
		System.out.print("Marking cell as free: " + gridX + "," + gridY);
		occupiedCells.remove(gridX + "," + gridY);
		System.out.println(" " + occupiedCells.contains(gridX + "," + gridY));
	}

	public List<Enemy> getEnemies() {
		return enemies;
	}

}
