package pvz.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Potato extends Plant {
    public Potato() {
        super(50, 20, 300);
    }

    public Potato(int posX, int posY) {
        this();
        super.x = posX;
        super.y = posY;
        sprite = new ImageView(new Image("images/plants/potato.gif"));
        sprite.setFitWidth(59);
        sprite.setFitHeight(66);
        sprite.setPreserveRatio(true);
        sprite.setLayoutX((posX - sprite.getFitWidth() / 2));
        sprite.setLayoutY((posY - sprite.getFitHeight() / 2) - 10);
    }

    @Override
    public void takeDamage(int amount) {
        super.takeDamage(amount);
        double percent = (double) this.health / 300;
        if (percent <= 0.30) {
            sprite.setImage(new Image("images/plants/potato-cracked2.gif"));
        } else if (percent <= 0.75) {
            sprite.setImage(new Image("images/plants/potato-cracked1.gif"));
        }
        System.out.println("Potato damaged: " + amount + " HP left: " + this.health);
    }

    @Override
    public void run() {
        // Defensive plant, no active behavior
    }
}
