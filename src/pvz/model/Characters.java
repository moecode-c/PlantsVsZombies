package pvz.model;

import javafx.application.Platform;
import javafx.scene.layout.Pane;

/**
 * Base class for any entity that can appear on the yard grid (plants, zombies, projectiles, etc.).
 */
public abstract class Characters extends MainElements implements Runnable {
    protected int health;
    private volatile boolean alive;

    public Characters() {
    }

    public Characters(int x, int y, int health) {
        super(x, y);
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /** Apply damage and clamp health without forcing subclasses to duplicate the logic. */
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            setAlive(false);
            Platform.runLater(() -> disappear(Yard.root));
        }
    }

    @Override
    public abstract void appear(Pane root);

    @Override
    public abstract void disappear(Pane root);
}
