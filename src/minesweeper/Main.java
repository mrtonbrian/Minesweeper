package minesweeper;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {
    BoardPane boardPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Board board = new Board(20, 20, 0.12d);

        BorderPane mainContainer = new BorderPane();

        boardPane = new BoardPane(board);
        boardPane.setWidth(calculateBoardPaneSize(board.getColumns()));
        boardPane.setMinWidth(boardPane.getWidth());
        boardPane.setHeight(calculateBoardPaneSize(board.getRows()));
        boardPane.setMinHeight(boardPane.getHeight());

        HBox topRow = new HBox();
        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> {
            boardPane.resetBoardPane();
        });

        topRow.getChildren().add(resetButton);
        topRow.setAlignment(Pos.CENTER);

        mainContainer.setTop(topRow);
        mainContainer.setCenter(boardPane);

        stage.setScene(new Scene(mainContainer));
        stage.sizeToScene();
        stage.show();

        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
    }

    int calculateBoardPaneSize(int quantity) {
        return quantity * 15 * 2;
    }
}
