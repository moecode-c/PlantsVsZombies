package pvz.model;

import javafx.scene.image.ImageView;

import pvz.util.AssetLoader;

public class Potato_Christmas extends Potato {
    public Potato_Christmas() {
        super();
    }

    public Potato_Christmas(int x, int y) {
        super(x, y);
        ImageView sprite = new ImageView(AssetLoader.loadImage("images/plants/potato_christmas.png"));
        sprite.setFitWidth(59);
        sprite.setFitHeight(66);
        sprite.setPreserveRatio(true);
        sprite.setLayoutX((x - sprite.getFitWidth() / 2));
        sprite.setLayoutY((y - sprite.getFitHeight() / 2) - 10);
        setSprite(sprite);
    }
}
