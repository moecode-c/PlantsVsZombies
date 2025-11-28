package pvz.model;

import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class HelmetZombie extends Zombie {
    public HelmetZombie() {
        super(10, 0.4, 200);
        elementImage = new ImageView(new Image("images/zombies/BucketheadZombie.gif"));
        elementImage.setFitWidth(135);
        elementImage.setFitHeight(120);
        elementImage.setPreserveRatio(true);
    }

    public HelmetZombie(int x, int y) {
        this();
        super.x = x;
        super.y = y;
        elementImage.setLayoutX(x);
        elementImage.setLayoutY(y);
    }
}
