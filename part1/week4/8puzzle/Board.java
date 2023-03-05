import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ResizingArrayBag;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Arrays;

public class Board {

    private final short[] tiles;
    private final int dimension;
    private final int blankIndex;
    private final int hammingDistance;
    private final int manhattanDistanceSum;
    private Board twinBoard = null;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        int blankI = -1;
        int hammingDist = 0, manhattanDistSum = 0;
        int n = tiles.length;
        this.tiles = new short[n * n];
        this.dimension = n;

        // copy tiles, find blank tile and compute hamming and manhattan distance
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] == 0) { // blank tile specification
                    blankI = i * n + j;
                    continue;
                }
                hammingDist += ((tiles[i][j] == i * n + j + 1) ? 0 : 1);
                int row = (tiles[i][j] - 1) / n, col = (tiles[i][j] - 1) % n;
                manhattanDistSum += manhattanDistance(i, j, row, col);
                this.tiles[i * n + j] = (short) tiles[i][j];
            }
        }

        assert (blankI != -1);
        blankIndex = blankI;
        hammingDistance = hammingDist;
        manhattanDistanceSum = manhattanDistSum;
    }

    // string representation of this board
    public String toString() {
        int n = dimension;
        String tileFormat = (n > 3) ? " %2d" : " %1d"; // dimension = 2, 3 and others

        StringBuilder s = new StringBuilder(n + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++)
                s.append(String.format(tileFormat, tiles[i * n + j]));
            s.append("\n");
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return dimension;
    }

    // number of tiles out of place
    public int hamming() {
        return hammingDistance;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return manhattanDistanceSum;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hammingDistance == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (this == y) return true;
        if (y == null) return false;
        if (this.getClass() != y.getClass()) return false;

        Board that = (Board) y;
        if (dimension != that.dimension) return false;
        return Arrays.equals(tiles, that.tiles);
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        ResizingArrayBag<Board> bag = new ResizingArrayBag<>();
        int n = dimension;
        int blankRow = blankIndex / n;
        int blankCol = blankIndex % n;
        int[][] intTiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++)
                intTiles[i][j] = tiles[i * n + j];
        }
        // upper
        if (blankRow > 0) {
            swapTile(intTiles, blankRow, blankCol, blankRow - 1, blankCol);
            bag.add(new Board(intTiles));
            swapTile(intTiles, blankRow, blankCol, blankRow - 1, blankCol);
        }

        // bottom
        if (blankRow + 1 < n) {
            swapTile(intTiles, blankRow, blankCol, blankRow + 1, blankCol);
            bag.add(new Board(intTiles));
            swapTile(intTiles, blankRow, blankCol, blankRow + 1, blankCol);
        }

        // left
        if (blankCol > 0) {
            swapTile(intTiles, blankRow, blankCol, blankRow, blankCol - 1);
            bag.add(new Board(intTiles));
            swapTile(intTiles, blankRow, blankCol, blankRow, blankCol - 1);
        }

        // right
        if (blankCol + 1 < n) {
            swapTile(intTiles, blankRow, blankCol, blankRow, blankCol + 1);
            bag.add(new Board(intTiles));
            swapTile(intTiles, blankRow, blankCol, blankRow, blankCol + 1);
        }

        return bag;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        if (twinBoard != null) return twinBoard;

        int n = dimension;
        int blankRow = blankIndex / n;
        int blankCol = blankIndex % n;
        int[][] intTiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++)
                intTiles[i][j] = tiles[i * n + j];
        }

        int srcRow = blankRow, srcCol = blankCol;
        while (srcRow == blankRow && srcCol == blankCol) {
            srcRow = StdRandom.uniformInt(n);
            srcCol = StdRandom.uniformInt(n);
        }
        int dstRow = blankRow, dstCol = blankCol;
        while ((dstRow == blankRow && dstCol == blankCol) ||
                (dstRow == srcRow && dstCol == srcCol)) {
            dstRow = StdRandom.uniformInt(n);
            dstCol = StdRandom.uniformInt(n);
        }

        swapTile(intTiles, srcRow, srcCol, dstRow, dstCol);
        twinBoard = new Board(intTiles);
        return twinBoard;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        // for each command-line argument
        for (String filename : args) {
            // read in the board specified in the filename
            In in = new In(filename);
            int n = in.readInt();
            int[][] tiles = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++)
                    tiles[i][j] = in.readInt();
            }

            Board board = new Board(tiles);
            StdOut.println(board.toString());
            StdOut.println("hamming: " + board.hamming());
            StdOut.println("manhattan: " + board.manhattan());
            StdOut.println("twin: \n" + board.twin());

            StdOut.println("neighbors: \n");
            Iterable<Board> it = board.neighbors();
            for (Board b : it) {
                StdOut.println(b);
            }
        }
    }

    private int manhattanDistance(int srcRow, int srcCol, int dstRow, int dstCol) {
        return Math.abs(dstRow - srcRow) + Math.abs(dstCol - srcCol);
    }

    private void swapTile(int[][] intTiles, int srcRow, int srcCol, int dstRow, int dstCol) {
        int temp = intTiles[srcRow][srcCol];
        intTiles[srcRow][srcCol] = intTiles[dstRow][dstCol];
        intTiles[dstRow][dstCol] = temp;
    }
}
