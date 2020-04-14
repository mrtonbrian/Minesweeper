package minesweeper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Board {
    private static final int[] dx = new int[]{-1, 0, 1};
    private static final int[] dy = new int[]{-1, 0, 1};

    private int rows, columns, mineCount;
    private int[][] board;
    private boolean[][] visibleBoard;
    private boolean[][] flaggedBoard;

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
    }

    public void openSquare(int square) {
        openSquare(toRow(square), toCol(square));
    }

    public void openSquare(int row, int col) {
        openSquare(row, col, false);
    }

    public void openSquare(int row, int col, boolean ignoreStartCheck) {
        // If Square Already Visited, Exit
        if (visibleBoard[row][col]) {
            return;
        }
        // Mark Square Visited
        visibleBoard[row][col] = true;
        // Stop If Square Is Adjacent To Bombs
        if (board[row][col] != 0 && !ignoreStartCheck) {
            return;
        }

        for (int xChange : dx) {
            for (int yChange : dy) {
                if ((isInbounds(row + yChange, col + xChange))) {
                    openSquare(row + yChange, col + xChange, false);
                }
            }
        }
    }

    public void addFlag(int square) { addFlag(toRow(square), toCol(square)); }
    public void addFlag(int row, int column) { flaggedBoard[row][column] = true; }

    public void removeFlag(int square) { addFlag(toRow(square), toCol(square)); }
    public void removeFlag(int row, int column) { flaggedBoard[row][column] = false; }

    private int countSurroundingMines(int square) {
        return countSurroundingMines(toRow(square), toCol(square));
    }

    private int countSurroundingMines(int row, int col) {
        int amountOfMines = 0;
        for (int xChange : dx) {
            for (int yChange : dy) {
                if ((isInbounds(row + yChange, col + xChange))
                        && board[row + yChange][col + xChange] == -1) {
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
        return rows * row + col;
    }

    private int toCol(int square) {
        return square % rows;
    }

    private int toRow(int square) {
        return square / rows;
    }

    public void reset() {
        this.board = new int[rows][columns];
        this.visibleBoard = new boolean[rows][columns];
        this.flaggedBoard = new boolean[rows][columns];
    }

    public void printBoard() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (visibleBoard[r][c]) {
                    if (board[r][c] != -1) {
                        System.out.print(board[r][c] + " ");
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
