import edu.princeton.cs.algs4.MergeX;
import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {
    private final int n;
    private final int[] index;

    private class CircularSuffix implements Comparable<CircularSuffix> {
        private final String s;
        private final int start;

        public CircularSuffix(String s, int start) {
            this.s = s;
            this.start = start;
        }

        public int compareTo(CircularSuffix other) {
            int len = s.length();
            int i = start, j = other.start;
            do {
                int cmp = Character.compare(s.charAt(i), other.s.charAt(j));
                if (cmp != 0) return cmp;
                i = (i + 1) % len;
                j = (j + 1) % len;
            } while (i != start);
            return 0;
        }
    }

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException("the argument s is null");

        n = s.length();
        index = new int[n];

        CircularSuffix[] cs = new CircularSuffix[n];
        for (int i = 0; i < n; i++) cs[i] = new CircularSuffix(s, i);
        MergeX.sort(cs);
        for (int i = 0; i < n; i++) index[i] = cs[i].start;
    }

    // length of s
    public int length() {
        return n;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= n)
            throw new IllegalArgumentException("i is outside its prescribed range");
        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        String s = "*************";
        CircularSuffixArray csa = new CircularSuffixArray(s);
        StdOut.println();
        for (int i = 0; i < csa.length(); i++) {
            StdOut.printf("%d ", csa.index(i));
        }
        StdOut.println();
    }
}
