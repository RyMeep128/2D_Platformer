public class KillableObject extends MovableObject {
	
    protected boolean alive;

    public KillableObject(int x, int y, int width, int height, int speed, String name) {
        super(x, y, width, height, speed, name);
        this.alive = true;
    }

    // Method to handle death logic, can be overridden by subclasses like Player or Enemy
    public void die() {
    }
    
    // Method to check if the enemy is alive
    public boolean isAlive() {
        return alive;
    }

    // Other common methods for killable objects...
}
