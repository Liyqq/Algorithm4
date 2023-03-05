import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        if (wordnet == null) throw new IllegalArgumentException("argument is null!");
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        if (nouns == null) throw new IllegalArgumentException("argument is null!");
        String leastRelated = "";
        int n = nouns.length, distance = -1;
        for (int i = 0; i < n; i++) {
            int tmpDistanceSum = 0;
            for (int j = 0; j < n; j++)
                tmpDistanceSum += wordnet.distance(nouns[i], nouns[j]);
            if (tmpDistanceSum > distance) {
                distance = tmpDistanceSum;
                leastRelated = nouns[i];
            }
        }
        return leastRelated;
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
