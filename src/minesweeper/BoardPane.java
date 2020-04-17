package minesweeper;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.ArrayList;

public class BoardPane extends GridPane {
    private ImageSquare[][] squares;
    private Board board;
    private int numClicks;
    private boolean shownEndScreen;

    BoardPane(Board board) {
        setBoard(board);

        resetBoardPane();
    }

    public void resetBoardPane() {
        this.getChildren().clear();
        setupBoard();
        board.reset();
        numClicks = 0;
        shownEndScreen = false;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setupBoard() {
        squares = new ImageSquare[board.getRows()][board.getColumns()];

        this.setOnMousePressed(e -> {
            numClicks++;
        });

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getColumns(); c++) {
                squares[r][c] = new ImageSquare(new Image("file:images/blank.gif"));
                GridPane.setRowIndex(squares[r][c], r);
                GridPane.setColumnIndex(squares[r][c], c);

                int finalR = r;
                int finalC = c;
                squares[r][c].setOnMousePressed(e -> {
                    if (board.getGameState() == Globals.GameState.IN_PROGRESS || board.getGameState() == Globals.GameState.NOT_STARTED) {
                        ArrayList<Pair<Integer, Integer>> squares = new ArrayList<>();
                        if (e.getButton() == MouseButton.PRIMARY) {
                            if (board.isVisible(finalR, finalC)) {
                                board.chordSquare(finalR, finalC, squares);
                            } else {
                                board.openSquare(finalR, finalC, squares);
                            }
                            updateDisplay(squares);
                        } else if (e.getButton() == MouseButton.SECONDARY) {
                            board.toggleFlag(finalR, finalC);
                            squares.add(new Pair<>(finalR, finalC));
                            updateDisplay(squares);
                        }
                    }
                });
                getChildren().add(squares[r][c]);
            }
        }
    }

    public void updateDisplay(ArrayList<Pair<Integer, Integer>> squaresToUpdate) {
        for (Pair<Integer, Integer> square : squaresToUpdate) {
            // We're using Pair<> like std::pair
            // Note that Key / Value Syntax Doesn't Really Make Sense though
            int r = square.getKey();
            int c = square.getValue();
            if (!board.isVisible(r, c) && !board.isFlagged(r, c)) {
                squares[r][c].setImage(new Image("file:images/blank.gif"),
                        Math.min((int) (getWidth() / board.getColumns()), (int) (getHeight() / board.getRows())
                        ));
            } else if (board.isFlagged(r, c)) {
                if (board.getGameState() == Globals.GameState.LOSS && board.getRowCol(r, c) >= 0) {
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

        if (!shownEndScreen && board.getGameState() == Globals.GameState.LOSS) {
            Alert gameLoss = new Alert(Alert.AlertType.ERROR);
            gameLoss.setTitle("Game Over!");
            gameLoss.setHeaderText("You Clicked On A Bomb :(");
            gameLoss.setContentText(String.format("You Lasted %.2f Seconds With %d Clicks\nClick on reset to play again :)", board.getGameDuration() / 1000., numClicks));
            Platform.runLater(gameLoss::showAndWait);
            shownEndScreen = true;
        } else if (!shownEndScreen && board.getGameState() == Globals.GameState.WIN) {
            Alert gameWin = new Alert(Alert.AlertType.INFORMATION);
            gameWin.setTitle("Game Over");
            gameWin.setHeaderText("You Win!");
            gameWin.setContentText(String.format("You Finished in %.2f Seconds With %d Clicks\nClick on reset to play again :)", board.getGameDuration() / 1000., numClicks));
            Platform.runLater(gameWin::showAndWait);
            shownEndScreen = true;
        }
    }

    @Override
    public void setWidth(double width) {
        super.setWidth(width);
        resizeBoard();
    }

    @Override
    public void setHeight(double height) {
        super.setHeight(height);
        resizeBoard();
    }

    private void resizeBoard() {
        ArrayList<Pair<Integer, Integer>> squares = new ArrayList<>();
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getColumns(); c++) {
                squares.add(new Pair<>(r, c));
            }
        }
        updateDisplay(squares);
    }
}
