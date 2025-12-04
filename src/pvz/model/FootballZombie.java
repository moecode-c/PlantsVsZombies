package pvz.model;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import pvz.util.AssetLoader;

public class FootballZombie extends Zombie {
    public FootballZombie() {
        super(20, 0.8, 225);
        ImageView view = new ImageView(AssetLoader.loadImage("images/zombies/FootballZombie.gif"));
        view.setFitWidth(120);
        view.setFitHeight(125);
        view.setPreserveRatio(true);
        this.elementImage = view;
    }

    public FootballZombie(int x, int y) {
        this();
        this.x = x;
        this.y = y;
    }

        @Override
    public void appear(Pane root)
    {
        //to be implemented
    }
}
