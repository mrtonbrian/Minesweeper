package minesweeper;

import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;

public class BoardPane extends GridPane {
    ImageButton[][] squares;
    Board board;

    BoardPane(Board board) {
        setBoard(board);
        setupBoard();
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setupBoard() {
        squares = new ImageButton[board.getRows()][board.getColumns()];

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getColumns(); c++) {
                squares[r][c] = new ImageButton();
                squares[r][c].setImage(new Image("file:images/blank.gif"));
                GridPane.setRowIndex(squares[r][c], r);
                GridPane.setColumnIndex(squares[r][c], c);

                getChildren().add(squares[r][c]);
            }
        }
    }

    public void updateDisplay() {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getColumns(); c++) {
                if (!board.isVisible(r, c)) {
                    squares[r][c].setImage(new Image("file:images/blank.gif"),
                                                         Math.min((int) (getWidth() / board.getColumns()), (int) (getHeight() / board.getRows())
                     ));
                } else {
                    // Using concat because string.format() does not seem to work
                    squares[r][c].setImage(new Image("file:images/open" + board.getRowCol(r,c) + ".gif"),
                                                        Math.min((int) (getWidth() / board.getColumns()), (int) (getHeight() / board.getRows())
                    ));
                }
            }
        }
    }

    @Override
    public void setWidth(double width) {
        super.setWidth(width);
        updateDisplay();
    }

    @Override
    public void setHeight(double height) {
        super.setHeight(height);
        updateDisplay();
    }
}
