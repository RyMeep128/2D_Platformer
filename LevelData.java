import java.util.List;

public class LevelData {
    private Player player;
    private List<Platform> platforms;
    private List<Spike> spikes;
    private List<Enemy> enemies;
    private Goal goal;

    public LevelData(Player player, List<Platform> platforms, List<Spike> spikes, List<Enemy> enemies, Goal goal) {
        this.player = player;
        this.platforms = platforms;
        this.spikes = spikes;
        this.enemies = enemies;
        this.goal = goal;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public List<Spike> getSpikes() {
        return spikes;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public Goal getGoal() {
        return goal;
    }
}
