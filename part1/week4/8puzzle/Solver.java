import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.ResizingArrayBag;
import edu.princeton.cs.algs4.ResizingArrayStack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private final ResizingArrayStack<Board> solutionBoards = new ResizingArrayStack<>();

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException("initial argument is null!");

        SearchNode goalNode = aStarSearch(new SearchNode(initial, 0, null));
        while (goalNode != null) {
            solutionBoards.push(goalNode.board);
            goalNode = goalNode.prev;
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return !solutionBoards.isEmpty();
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return solutionBoards.size() - 1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return (solutionBoards.isEmpty() ? null : solutionBoards);
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

    private SearchNode aStarSearch(SearchNode initial) {
        SearchNode initialTwin = new SearchNode(initial.board.twin(), 0, null);
        MinPQ<SearchNode> minPQ = new MinPQ<>(), minPQTwin = new MinPQ<>();
        minPQ.insert(initial);
        minPQTwin.insert(initialTwin);

        SearchNode currentSearchNode = null;
        SearchNode currentSearchNodeTwin = null;
        while (!minPQ.isEmpty() && !minPQTwin.isEmpty()) {
            currentSearchNode = minPQ.delMin();
            if (currentSearchNode.isGoal()) break;
            currentSearchNodeTwin = minPQTwin.delMin();
            if (currentSearchNodeTwin.isGoal()) break;

            for (SearchNode s : currentSearchNode.neighbors()) {
                if (s.boardEquals(currentSearchNode.prev)) continue; // critical optimization
                minPQ.insert(s);
            }
            for (SearchNode s : currentSearchNodeTwin.neighbors()) {
                if (s.boardEquals(currentSearchNodeTwin.prev)) continue;
                minPQTwin.insert(s);
            }
        }
        return (currentSearchNode == null) ? null :
               (currentSearchNode.isGoal() ? currentSearchNode : null);
    }

    private class SearchNode implements Comparable<SearchNode> {
        private final Board board;
        private final int priority;
        private final SearchNode prev;

        public SearchNode(Board board, int moveStep, SearchNode prev) {
            this.board = board;
            this.prev = prev;
            priority = board.manhattan() + moveStep;
        }

        public Iterable<SearchNode> neighbors() {
            ResizingArrayBag<SearchNode> bag = new ResizingArrayBag<SearchNode>();
            int moves = priority - board.manhattan();
            for (Board b : board.neighbors())
                bag.add(new SearchNode(b, moves + 1, this));
            return bag;
        }

        public boolean isGoal() {
            return board.isGoal();
        }

        public int compareTo(SearchNode that) {
            return Integer.compare(priority, that.priority);
        }

        public boolean boardEquals(SearchNode that) {
            if (that == null) return false;
            if (this == that) return true;
            if (prev == that.prev) return false;
            return board.equals(that.board);
        }
    }
}
