package minesweeper;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// Adapted from https://stackoverflow.com/a/32034675/8935887
public class ImageSquare extends ImageView {
    ImageSquare(Image image) {
        super(image);
    }

    public void setImage(Image image, int size) {
        setImage(image);
        setFitWidth(size);
        setFitHeight(size);
    }
}
