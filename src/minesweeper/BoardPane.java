package minesweeper;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;

public class BoardPane extends GridPane {
    ImageSquare[][] squares;
    Board board;

    BoardPane(Board board) {
        setBoard(board);
        setupBoard();
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setupBoard() {
        squares = new ImageSquare[board.getRows()][board.getColumns()];

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getColumns(); c++) {
                squares[r][c] = new ImageSquare(new Image("file:images/blank.gif"));
                GridPane.setRowIndex(squares[r][c], r);
                GridPane.setColumnIndex(squares[r][c], c);

                int finalR = r;
                int finalC = c;
                squares[r][c].setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        board.openSquare(finalR, finalC);
                        updateDisplay();
                    } else if (e.getButton() == MouseButton.SECONDARY) {
                        board.toggleFlag(finalR, finalC);
                        updateDisplay();
                    }
                });
                getChildren().add(squares[r][c]);
            }
        }
    }

    public void updateDisplay() {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getColumns(); c++) {
                if (!board.isVisible(r, c) && !board.isFlagged(r, c)) {
                    squares[r][c].setImage(new Image("file:images/blank.gif"),
                            Math.min((int) (getWidth() / board.getColumns()), (int) (getHeight() / board.getRows())
                            ));
                } else if (board.isFlagged(r, c)) {
                    if (board.isGameOver() && board.getRowCol(r, c) >= 0) {
                        squares[r][c].setImage(new Image("file:images/bombmisflagged.gif"),
                                Math.min((int) (getWidth() / board.getColumns()), (int) (getHeight() / board.getRows())
                                ));
                    } else {
                        squares[r][c].setImage(new Image("file:images/bombflagged.gif"),
                                Math.min((int) (getWidth() / board.getColumns()), (int) (getHeight() / board.getRows())
                                ));
                    }
                } else {
                    if (board.getRowCol(r, c) >= 0) {
                        // Using concat because string.format() does not seem to work
                        squares[r][c].setImage(new Image("file:images/open" + board.getRowCol(r, c) + ".gif"),
                                Math.min((int) (getWidth() / board.getColumns()), (int) (getHeight() / board.getRows())
                                ));
                    } else {
                        if (board.getRowCol(r, c) == -2) {
                            squares[r][c].setImage(new Image("file:images/bombdeath.gif"),
                                    Math.min((int) (getWidth() / board.getColumns()), (int) (getHeight() / board.getRows())
                                    ));
                        } else {
                            squares[r][c].setImage(new Image("file:images/bombrevealed.gif"),
                                    Math.min((int) (getWidth() / board.getColumns()), (int) (getHeight() / board.getRows())
                                    ));
                        }
                    }
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
