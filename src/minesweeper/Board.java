package minesweeper;

import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Board {
    private static final int[] dx = new int[]{-1, 0, 1};
    private static final int[] dy = new int[]{-1, 0, 1};

    private final int rows;
    private final int columns;
    private final int mineCount;
    private int[][] board;
    private boolean[][] visibleBoard;
    private boolean[][] flaggedBoard;

    private int openedSquares;
    private Globals.GameState gameState;
    private long gameStartTime;
    private long gameEndTime;

    public Board(int rows, int columns, int mineCount) {
        // Argument Checking
        if (mineCount >= (rows * columns)) {
            throw new IllegalArgumentException("There Must Be Less Mines Than Squares");
        }
        this.rows = rows;
        this.columns = columns;
        this.mineCount = mineCount;

        reset();
    }

    public Board(int rows, int columns, double density) {
        if (density >= 1) {
            throw new IllegalArgumentException("There Must Be Less Mines Than Squares");
        }
        this.rows = rows;
        this.columns = columns;
        this.mineCount = (int) (density * rows * columns);
        System.out.println(mineCount);

        reset();
    }

    // Note That Populate Board Excludes 1 Square (First Square) From Being A Mine
    public void populateBoard(int rowExclude, int colExclude) {
        List<Integer> allSquares = IntStream.range(0, rows * columns).boxed().collect(Collectors.toList());
        allSquares.remove(rowColToSquare(rowExclude, colExclude));

        // Randomly Put Mines Everywhere
        Collections.shuffle(allSquares);
        int i;
        for (i = 0; i < mineCount; i++) {
            board[toRow(allSquares.get(i))][toCol(allSquares.get(i))] = -1;
        }

        // Add Back Assured Non-Mine
        allSquares.add(rowColToSquare(rowExclude, colExclude));
        // Fill In Numbers
        while (i < allSquares.size()) {
            board[toRow(allSquares.get(i))][toCol(allSquares.get(i))] = countSurroundingMines(allSquares.get(i));
            i++;
        }

        gameStartTime = System.currentTimeMillis();
    }

    public void openSquare(int square, ArrayList<Pair<Integer, Integer>> squaresOpened) {
        openSquare(toRow(square), toCol(square), squaresOpened);
    }

    public void openSquare(int row, int col, ArrayList<Pair<Integer, Integer>> squaresOpened) {
        openSquare(row, col, squaresOpened, false);
    }

    public void openSquare(int row, int col, ArrayList<Pair<Integer, Integer>> squaresOpened, boolean ignoreStartCheck) {
        // Initialize Game if We Haven't Already
        if (gameState == Globals.GameState.NOT_STARTED) {
            gameState = Globals.GameState.IN_PROGRESS;
            populateBoard(row, col);
        } else if (gameState != Globals.GameState.IN_PROGRESS) {
            // Either Won / Lost Already, Return Early
            return;
        }
        // If Square Already Visited, Exit
        if (visibleBoard[row][col] || flaggedBoard[row][col]) {
            return;
        }
        // Mark Square Visited
        visibleBoard[row][col] = true;
        // Add Square To OpenedSquares
        squaresOpened.add(new Pair<>(row, col));

        // Set Square to -2 If it is Death Bomb
        if (board[row][col] == -1) {
            board[row][col] = -2;
            gameState = Globals.GameState.LOSS;

            // When Dead, Reveal All Bombs
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    if (board[r][c] < 0) {
                        visibleBoard[r][c] = true;
                    }
                }
            }

            gameEndTime = System.currentTimeMillis();
            return;
        }

        // If Square was Able to Be Opened, Increment OpenedSquares
        openedSquares++;

        // Check Win
        checkWin();

        // Stop If Square Is Adjacent To Bombs
        if (board[row][col] != 0 && !ignoreStartCheck) {
            return;
        }

        for (int xChange : dx) {
            for (int yChange : dy) {
                if ((isInbounds(row + yChange, col + xChange))) {
                    openSquare(row + yChange, col + xChange, squaresOpened, false);
                }
            }
        }
    }

    public void chordSquare(int square, ArrayList<Pair<Integer, Integer>> openedSquares) {
        chordSquare(toRow(square),toCol(square), openedSquares);
    }

    public void chordSquare(int row, int col, ArrayList<Pair<Integer, Integer>> openedSquares) {
        if (isVisible(row, col) && getRowCol(row, col) >= 0) {
            int surroundingFlaggedSquares = 0;
            for (int xChange : dx) {
                for (int yChange : dy) {
                    if ((isInbounds(row + yChange, col + xChange)) && isFlagged(row + yChange,col + xChange)) {
                        surroundingFlaggedSquares++;
                    }
                }
            }
            if (surroundingFlaggedSquares == getRowCol(row, col)) {
                for (int xChange : dx) {
                    for (int yChange : dy) {
                        if ((isInbounds(row + yChange, col + xChange))) {
                            openSquare(row + yChange, col + xChange, openedSquares, false);
                        }
                    }
                }
            }
        }
    }

    public void addFlag(int square) {
        addFlag(toRow(square), toCol(square));
    }

    public void addFlag(int row, int column) {
        flaggedBoard[row][column] = true;
    }

    public void removeFlag(int square) {
        addFlag(toRow(square), toCol(square));
    }

    public void removeFlag(int row, int column) {
        flaggedBoard[row][column] = false;
    }

    public boolean isFlagged(int square) {
        return isFlagged(toRow(square), toCol(square));
    }

    public boolean isFlagged(int row, int col) {
        return flaggedBoard[row][col];
    }

    public void toggleFlag(int square) {
        toggleFlag(toRow(square), toCol(square));
    }

    public void toggleFlag(int row, int col) {
        if (!isVisible(row, col)) if (isFlagged(row, col)) removeFlag(row, col);
        else addFlag(row, col);
    }

    private int countSurroundingMines(int square) {
        return countSurroundingMines(toRow(square), toCol(square));
    }

    private int countSurroundingMines(int row, int col) {
        int amountOfMines = 0;
        for (int xChange : dx) {
            for (int yChange : dy) {
                if ((isInbounds(row + yChange, col + xChange))
                        && getRowCol(row + yChange,col + xChange) == -1) {
                    amountOfMines++;
                }
            }
        }

        return amountOfMines;
    }

    private boolean isInbounds(int row, int col) {
        return ((row >= 0) && (row < rows) && (col >= 0) && (col < columns));
    }

    public boolean isVisible(int square) {
        return isVisible(toRow(square), toCol(square));
    }

    public boolean isVisible(int row, int column) {
        return visibleBoard[row][column];
    }

    public int getSquare(int square) {
        return getRowCol(toRow(square), toCol(square));
    }

    public int getRowCol(int row, int col) {
        return board[row][col];
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    private int rowColToSquare(int row, int col) {
        return columns * row + col;
    }

    private int toCol(int square) {
        return square % columns;
    }

    private int toRow(int square) {
        return square / columns;
    }

    public void reset() {
        this.board = new int[rows][columns];
        this.visibleBoard = new boolean[rows][columns];
        this.flaggedBoard = new boolean[rows][columns];

        openedSquares = 0;

        gameState = Globals.GameState.NOT_STARTED;
        gameStartTime = -1;
        gameEndTime = -1;
    }

    public Globals.GameState getGameState() {
        return gameState;
    }

    private void checkWin() {
        // If We Have Opened Every Square
        if (openedSquares == (rows * columns) - mineCount) {
            // Set gameState to win
            gameState = Globals.GameState.WIN;

            // If We Haven't Set Game End Time Yet, Set It
            if (gameEndTime == -1) {
                gameEndTime = System.currentTimeMillis();
            }
        }
    }

    public long getGameDuration() {
        if (gameState == Globals.GameState.IN_PROGRESS || gameState == Globals.GameState.NOT_STARTED) {
            return -1;
        }
        return gameEndTime - gameStartTime;
    }

    public long timeSinceStart() {
        return System.currentTimeMillis() - gameStartTime;
    }

    public void printBoard() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (isVisible(r,c)) {
                    if (getRowCol(r, c) != -1) {
                        System.out.print(getRowCol(r,c) + " ");
                    } else {
                        System.out.print("* ");
                    }
                } else {
                    System.out.print("# ");
                }
            }
            System.out.println();
        }
    }
}
