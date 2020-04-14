package minesweeper;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// Adapted from https://stackoverflow.com/a/32034675/8935887
public class ImageButton extends Button {
    ImageView imageView = new ImageView();
    public void setImage(Image image, int size) {
        imageView.setImage(image);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        setMinSize(size,size);
        setMaxSize(size,size);

        this.getChildren().clear();
        this.getChildren().add(imageView);
        super.setGraphic(imageView);
    }
    public void setImage(Image image) {
        setImage(image, 16);
    }
}
