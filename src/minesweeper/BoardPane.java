package minesweeper;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class BoardPane extends GridPane {
    public static HashMap<String, Image> IMAGES = null;
    private ImageSquare[][] squares;
    private Board board;
    private int numClicks;
    private boolean shownEndScreen;

    BoardPane(Board board) {
        initializeImages();

        setBoard(board);

        resetBoardPane();

        ChangeListener<Number> heightListener = (observableValue, number, t1) -> {
            if (Math.min((int) (getWidth() / board.getColumns()), (int) (t1.intValue() / board.getRows())) !=
                    Math.min((int) (getWidth() / board.getColumns()), (int) (number.intValue() / board.getRows())
                    )) {
                resizeBoard();
            }
        };

        ChangeListener<Number> widthListener = (observableValue, number, t1) -> {
            if (Math.min((t1.intValue() / board.getColumns()), (int) (getHeight() / board.getRows())) !=
                    Math.min((int) (number.intValue() / board.getColumns()), (int) (getHeight() / board.getRows())
                    )) {
                resizeBoard();
            }
        };

        this.widthProperty().addListener(widthListener);
        this.heightProperty().addListener(heightListener);
    }

    private void initializeImages() {
        if (IMAGES == null) {
            IMAGES = new HashMap<>();
            IMAGES.put("BLANK", new Image("file:images/blank.gif"));
            IMAGES.put("MISFLAGGED", new Image("file:images/bombmisflagged.gif"));
            IMAGES.put("FLAGGED", new Image("file:images/bombflagged.gif"));
            IMAGES.put("DEATH", new Image("file:images/bombdeath.gif"));
            IMAGES.put("REVEALED", new Image("file:images/bombrevealed.gif"));

            for (int i = 0; i <= 8; i++) {
                IMAGES.put("OPEN" + i, new Image("file:images/open" + i + ".gif"));
            }
        }
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
                squares[r][c] = new ImageSquare(IMAGES.get("BLANK"), Math.min((int) (getWidth() / board.getColumns()), (int) (getHeight() / board.getRows())));
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
                squares[r][c].setImage(IMAGES.get("BLANK"),
                        Math.min((int) (getWidth() / board.getColumns()), (int) (getHeight() / board.getRows())
                        ));
            } else if (board.isFlagged(r, c)) {
                if (board.getGameState() == Globals.GameState.LOSS && board.getRowCol(r, c) >= 0) {
                    squares[r][c].setImage(IMAGES.get("MISFLAGGED"),
                            Math.min((int) (getWidth() / board.getColumns()), (int) (getHeight() / board.getRows())
                            ));
                } else {
                    squares[r][c].setImage(IMAGES.get("FLAGGED"),
                            Math.min((int) (getWidth() / board.getColumns()), (int) (getHeight() / board.getRows())
                            ));
                }
            } else {
                if (board.getRowCol(r, c) >= 0) {
                    squares[r][c].setImage(IMAGES.get("OPEN" + board.getRowCol(r, c)),
                            Math.min((int) (getWidth() / board.getColumns()), (int) (getHeight() / board.getRows())
                            ));
                } else {
                    if (board.getRowCol(r, c) == -2) {
                        squares[r][c].setImage(IMAGES.get("DEATH"),
                                Math.min((int) (getWidth() / board.getColumns()), (int) (getHeight() / board.getRows())
                                ));
                    } else {
                        squares[r][c].setImage(IMAGES.get("REVEALED"),
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
    }

    @Override
    public void setHeight(double height) {
        super.setHeight(height);
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
