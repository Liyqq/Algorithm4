import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycleX;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;

public class WordNet {
    private final HashMap<String, Integer> word2SingleID = new HashMap<>();
    private final HashMap<String, Bag<Integer>> word2MultiID = new HashMap<>();
    private final HashMap<Integer, String> id2SingleWord = new HashMap<>();
    private final HashMap<Integer, String[]> id2MultiWord = new HashMap<>();

    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        validate(synsets);
        validate(hypernyms);

        buildWordIDMap(synsets);
        Digraph digraph = buildDigraph(hypernyms);
        if (!isRootedDAG(digraph))
            throw new IllegalArgumentException("input does not correspond to a rooted DAG!");
        sap = new SAP(digraph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return word2SingleID.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        validate(word);
        return word2SingleID.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        validateNoun(nounA);
        validateNoun(nounB);
        if (nounA.equals(nounB)) return 0;

        int idA = word2SingleID.get(nounA), idB = word2SingleID.get(nounB);
        if (idA != -1 && idB != -1) return sap.length(idA, idB);

        Bag<Integer> idABag = new Bag<Integer>();
        idABag.add(idA);
        if (idA == -1) idABag = word2MultiID.get(nounA);
        Bag<Integer> idBBag = new Bag<Integer>();
        idBBag.add(idB);
        if (idB == -1) idBBag = word2MultiID.get(nounB);
        return sap.length(idABag, idBBag);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        validateNoun(nounA);
        validateNoun(nounB);

        int ancestor;
        int idA = word2SingleID.get(nounA), idB = word2SingleID.get(nounB);
        if (idA != -1 && idB != -1) ancestor = sap.ancestor(idA, idB);
        else {
            Bag<Integer> idABag = new Bag<Integer>();
            idABag.add(idA);
            if (idA == -1) idABag = word2MultiID.get(nounA);
            Bag<Integer> idBBag = new Bag<Integer>();
            idBBag.add(idB);
            if (idB == -1) idBBag = word2MultiID.get(nounB);
            ancestor = sap.ancestor(idABag, idBBag);
        }
        
        if (id2SingleWord.containsKey(ancestor)) return id2SingleWord.get(ancestor);
        StringBuilder s = new StringBuilder();
        for (String str : id2MultiWord.get(ancestor)) {
            s.append(str);
            s.append(" ");
        }
        s.deleteCharAt(s.length() - 1);
        return s.toString();
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        StdOut.println(wordnet.sap("worm", "bird"));
    }

    private Digraph buildDigraph(String hypernyms) {
        Digraph digraph = new Digraph(id2SingleWord.size() + id2MultiWord.size());
        In in = new In(hypernyms);
        while (in.hasNextLine()) {
            String[] a = in.readLine().split(",");
            int synsetID = Integer.parseInt(a[0]);
            for (int i = 1; i < a.length; i++) {
                int hypernymID = Integer.parseInt(a[i]);
                digraph.addEdge(synsetID, hypernymID);
            }
        }
        return digraph;
    }

    private void buildWordIDMap(String synsets) {
        // build the map of word to id and teh map of id to word
        In in = new In(synsets);
        while (in.hasNextLine()) {
            String[] a = in.readLine().split(",");
            int synsetID = Integer.parseInt(a[0]);
            String[] words = a[1].split(" ");

            // word to id map
            for (String w : words) {
                if (!word2SingleID.containsKey(w)) word2SingleID.put(w, synsetID);
                else if (word2SingleID.get(w) == -1) word2MultiID.get(w).add(synsetID);
                else {
                    Bag<Integer> bag = new Bag<>();
                    bag.add(word2SingleID.get(w));
                    bag.add(synsetID);
                    word2MultiID.put(w, bag);
                    word2SingleID.put(w, -1);
                }
            }

            // id to word map
            if (words.length == 1) id2SingleWord.put(synsetID, words[0]);
            else id2MultiWord.put(synsetID, words);
        }
    }

    private boolean isRootedDAG(Digraph diG) {
        DirectedCycleX finder = new DirectedCycleX(diG);
        if (finder.hasCycle()) return false;
        int rootCount = 0;
        for (int v = 0; v < diG.V(); v++) {
            if (diG.outdegree(v) == 0) rootCount++;
            if (rootCount > 1) return false;
        }
        return true;
    }

    private void validate(String s) {
        if (s == null) throw new IllegalArgumentException("argument is null!");
    }

    private void validateNoun(String noun) {
        if (!isNoun(noun)) throw new IllegalArgumentException("noun is not a WordNet noun!");
    }
}
