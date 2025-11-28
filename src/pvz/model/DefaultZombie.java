package pvz.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class DefaultZombie extends Zombie {
    public DefaultZombie() {
        super(10, 1.0, 100);
    }
    public DefaultZombie(int posX, int posY) {
        this();
        appear(null, posX, posY);
    }
}
