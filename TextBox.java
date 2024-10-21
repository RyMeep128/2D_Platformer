import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TextBox extends GameObject {
    private StringBuilder text;      // Stores the raw text
    private List<String> lines;      // Stores the wrapped lines of text
    private boolean isActive;
    private int minWidth;
    private int maxWidth;

    public TextBox(int x, int y, int width, int height) {
        super(x, y, width, height, "TextBox");
        this.text = new StringBuilder();
        this.lines = new ArrayList<>();
        this.isActive = false;
        this.minWidth = width;       // Minimum width for the box
        this.maxWidth = 200;         // Maximum width for wrapping
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }

    // Append text and resize if necessary
    public void appendText(char c, Graphics g) {
        if (isActive) {
            text.append(c);
            wrapText(g);
            resizeToFitText(g);
        }
    }

    // Remove last character (for backspace) and resize
    public void removeLastCharacter(Graphics g) {
        if (isActive && text.length() > 0) {
            text.deleteCharAt(text.length() - 1);
            wrapText(g);
            resizeToFitText(g);
        }
    }

    // Wrap the text to fit within the TextBox's width
    private void wrapText(Graphics g) {
        lines.clear();  // Clear previous lines
        FontMetrics metrics = g.getFontMetrics();

        String[] words = text.toString().split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            String testLine = line + word + " ";
            if (metrics.stringWidth(testLine) > maxWidth) {
                lines.add(line.toString());
                line = new StringBuilder(word + " ");
            } else {
                line.append(word).append(" ");
            }
        }

        // Add the last line
        lines.add(line.toString());
    }

    // Resize the TextBox based on the wrapped lines
    private void resizeToFitText(Graphics g) {
        FontMetrics metrics = g.getFontMetrics();
        int textHeight = metrics.getHeight();
        width = Math.max(minWidth, maxWidth);
        height = textHeight * lines.size() + 10;  // Add padding to height
    }

    
    public void draw(Graphics g) {
        // Adjust size before drawing based on text width
        resizeToFitText(g);

        // Draw the box
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);

        // Draw the border
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        // Draw the wrapped text inside the box
        g.setColor(Color.BLACK);
        FontMetrics metrics = g.getFontMetrics();
        int lineHeight = metrics.getHeight();
        int yPosition = y + lineHeight;

        for (String line : lines) {
            g.drawString(line, x + 5, yPosition);
            yPosition += lineHeight;  // Move down for each new line
        }
    }
}
