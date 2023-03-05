import edu.princeton.cs.algs4.Alphabet;
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final Alphabet ALPHABET = Alphabet.EXTENDED_ASCII;
    private static final char[] ALPHABET_SEQ = new char[ALPHABET.radix()];
    private static final int[] CHAR_INDICES = new int[ALPHABET.radix()];

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        int r = ALPHABET.radix();
        for (int i = 0; i < r; i++) {
            ALPHABET_SEQ[i] = (char) i;
            CHAR_INDICES[i] = i;
        }

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            BinaryStdOut.write((char) CHAR_INDICES[c]);
            for (int i = CHAR_INDICES[c]; i > 0; i--) {
                ALPHABET_SEQ[i] = ALPHABET_SEQ[i - 1];
                CHAR_INDICES[ALPHABET_SEQ[i]]++;
            }
            ALPHABET_SEQ[0] = c;
            CHAR_INDICES[c] = 0;
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        int r = ALPHABET.radix();
        for (int i = 0; i < r; i++) ALPHABET_SEQ[i] = (char) i;

        while (!BinaryStdIn.isEmpty()) {
            int index = BinaryStdIn.readChar();
            char c = ALPHABET_SEQ[index];
            BinaryStdOut.write(c);
            for (int i = index; i > 0; i--)
                ALPHABET_SEQ[i] = ALPHABET_SEQ[i - 1];
            ALPHABET_SEQ[0] = c;
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            encode();
        }
        else if (args[0].equals("+")) {
            decode();
        }
    }
}
