package minesweeper;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    BoardPane boardPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Board board = new Board(19, 30, 0.2d);

        boardPane = new BoardPane(board);
        boardPane.setupBoard();

        stage.setScene(new Scene(boardPane, calculateBoardPaneSize(board.getColumns()), calculateBoardPaneSize(board.getRows())));
        stage.sizeToScene();
        stage.show();

        stage.setMinHeight(stage.getHeight());
        stage.setMinWidth(stage.getWidth());
    }

    int calculateBoardPaneSize(int quantity) {
        return quantity * 15 * 2;
    }
}
