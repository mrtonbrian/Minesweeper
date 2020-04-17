package minesweeper;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
