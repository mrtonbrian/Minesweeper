package minesweeper;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    BoardPane boardPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Board board = new Board(20, 20, 0.12d);

        VBox mainContainer = new VBox();

        boardPane = new BoardPane(board);
        boardPane.setWidth(calculateBoardPaneSize(board.getColumns()));
        boardPane.setMinWidth(boardPane.getWidth());
        boardPane.setHeight(calculateBoardPaneSize(board.getRows()));
        boardPane.setMinHeight(boardPane.getHeight());
        boardPane.setupBoard();

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> {
            boardPane.resetBoardPane();
        });


        mainContainer.getChildren().addAll(resetButton,boardPane);
        mainContainer.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(mainContainer));
        stage.sizeToScene();
        stage.show();

        stage.setMinHeight(stage.getHeight());
        stage.setMinWidth(stage.getWidth());
    }

    int calculateBoardPaneSize(int quantity) {
        return quantity * 15 * 2;
    }
}
