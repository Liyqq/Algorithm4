import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

public class BoggleSolver {
    private static final byte[] SOCRE_TABLE = { 0, 0, 0, 1, 1, 2, 3, 5, 11 };
    private static final int MAX_LENGTH = SOCRE_TABLE.length - 1;
    private final TernaryST<Byte> dict = new TernaryST<>();
    private int maxWordLen = -1;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        // build dictionary
        for (String word : dictionary) {
            maxWordLen = Math.max(maxWordLen, word.length());
            dict.put(word, getScore(word));
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        TrieSet validWords = new TrieSet();
        int rows = board.rows(), cols = board.cols();
        boolean[][] marked = new boolean[rows][cols];
        for (int r = 0; r < rows; r++) {
            Stopwatch timer = new Stopwatch();
            for (int c = 0; c < cols; c++)
                dfs(r, c, board, marked, new StringBuilder(), validWords);
            StdOut.println("innwe time" + timer.elapsedTime());
        }
        return validWords;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!dict.contains(word)) return 0;
        return dict.get(word);
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        Stopwatch timer = new Stopwatch();
        for (String word : solver.getAllValidWords(board)) {
            int s = solver.scoreOf(word);
            StdOut.println(word + " " + s);
            score += s;
        }
        StdOut.println("Score = " + score);
        StdOut.println("time = " + timer.elapsedTime());

    }

    private byte getScore(String word) {
        int length = Math.min(word.length(), MAX_LENGTH);
        return SOCRE_TABLE[length];
    }

    private void dfs(int r, int c, BoggleBoard board, boolean[][] marked,
                     StringBuilder sb, TrieSet validWords) {
        if (r < 0 || r >= board.rows()) return;
        if (c < 0 || c >= board.cols()) return;
        if (marked[r][c]) return;

        char letter = board.getLetter(r, c);
        sb.append(letter);
        if (letter == 'Q') sb.append('U');
        String str = sb.toString();
        // if (str.equals("X")) StdOut.println(str);
        // StdOut.println(r + " " + c);
        // StdOut.println(str);
        // StdOut.println();

        if (str.length() > 2) {
            if (str.length() > maxWordLen) return;
            if (!dict.hasKeysWithPrefix(str)) {
                sb.deleteCharAt(sb.length() - 1);
                if (letter == 'Q') sb.deleteCharAt(sb.length() - 1);
                return;
            }
            if (dict.contains(str)) validWords.add(str);
        }

        marked[r][c] = true;
        dfs(r - 1, c - 1, board, marked, sb, validWords);
        dfs(r - 1, c, board, marked, sb, validWords);
        dfs(r - 1, c + 1, board, marked, sb, validWords);
        dfs(r, c - 1, board, marked, sb, validWords);
        dfs(r, c + 1, board, marked, sb, validWords);
        dfs(r + 1, c - 1, board, marked, sb, validWords);
        dfs(r + 1, c, board, marked, sb, validWords);
        dfs(r + 1, c + 1, board, marked, sb, validWords);
        marked[r][c] = false;
        sb.deleteCharAt(sb.length() - 1);
        if (letter == 'Q') sb.deleteCharAt(sb.length() - 1);
    }
}
