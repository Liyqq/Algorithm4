import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int R = 256;

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(s);

        int n = s.length();
        int first = -1;
        char[] buffer = new char[n];
        for (int i = 0; i < n; i++) {
            int index = csa.index(i);
            if (index == 0) {
                first = i;
                continue;
            }
            buffer[i] = s.charAt(index - 1);
        }
        buffer[first] = s.charAt(n - 1);

        BinaryStdOut.write(first);
        for (char c : buffer) BinaryStdOut.write(c);
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String s = BinaryStdIn.readString();
        char[] t = s.toCharArray();
        int n = t.length;
        int[] count = new int[R + 1];
        char[] aux = new char[n];
        int[] next = new int[n];

        for (int i = 0; i < n; i++) // count frequencies
            count[t[i] + 1]++;
        for (int r = 0; r < R; r++) // compute cumulates
            count[r + 1] += count[r];
        for (int i = 0; i < n; i++) {
            aux[count[t[i]]] = t[i];
            next[count[t[i]]++] = i;
        }
        int nxt = first;
        for (int i = 0; i < n; i++, nxt = next[nxt])
            BinaryStdOut.write(aux[nxt]);
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            transform();

        }
        else if (args[0].equals("+")) {
            inverseTransform();
        }
    }
}
