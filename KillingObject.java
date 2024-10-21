public class KillingObject extends GameObject {

    public KillingObject(int x, int y, int width, int height, String name) {
        super(x, y, width, height, name);
    }

    // Method to handle the logic when a killable object touches this killing object
    public void kill(KillableObject object) {
        object.die();
    }
    
    

    // Other common methods for killing objects...
}
