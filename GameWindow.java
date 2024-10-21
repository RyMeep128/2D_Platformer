import javax.swing.JFrame;

public class GameWindow extends JFrame {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GameWindow() {
        // Set the title of the window
        setTitle("2D Platformer Game");
        
        // Set the default close operation (exit the game when the window is closed)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Set the size of the window
        setSize(800, 600); // Width: 800px, Height: 600px
        
        // Make the window non-resizable
        setResizable(false);
        
        // Center the window on the screen
        setLocationRelativeTo(null);
        
    }
    
    public static void main(String[] args) {
        // Create a new instance of GameWindow
        GameWindow window = new GameWindow();
        
        
        
        
        // Create an instance of the Game class and add it to the window
        Game game = new Game(800,window);
        window.add(game);
        
        // Register mouse listeners
        game.addMouseListener(game);         // For clicks
        game.addMouseMotionListener(game);   // For dragging
        
        // Make the window visible
        window.setVisible(true);
        
        // Start the game
        game.start();
    }
}
