package minesweeper;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Scanner;

public class Main extends Application {
    BoardPane boardPane;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Board board = new Board(10, 10, 8);
        board.populateBoard(0, 0);
        Scanner s = new Scanner(System.in);
        board.openSquare(0);
        board.printBoard();

        boardPane = new BoardPane(board);
        boardPane.setupBoard();

        stage.setScene(new Scene(boardPane,300,300));
        stage.sizeToScene();
        stage.show();

        stage.setMinHeight(stage.getHeight());
        stage.setMinWidth(stage.getWidth());
    }
}
