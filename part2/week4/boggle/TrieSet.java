import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;

/**
 * This {@code TrieSet} class is adapted from {@code TrieSET} class and
 * {@code TrieSet} class in algs4.cs.princeton.edu.jar
 * <p>
 * The {@code TrieSet} class represents an ordered set of strings over
 * the 26 characters Alphabet.
 * It supports the usual <em>add</em> and <em>contains</em>.
 * It also provides character-based methods for finding all
 * strings in the set that <em>start with</em> a given prefix.
 * <p>
 * This implementation uses a ternary trie.
 * The <em>add</em> and <em>contains</em> methods
 * take time proportional to the length of the key (in the worst case).
 * Construction takes constant time.
 * <p>
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 * modified by @Yooj
 */
public class TrieSet implements Iterable<String> {
    private int n;       // number of keys in trie
    private Node root;   // root of TrieSet

    private static class Node {
        private char c;                 // character
        private Node left, mid, right;  // left, middle, and right sub-tries
        private boolean isString;       // value associated with string
    }

    /**
     * Initializes an empty set of strings.
     */
    public TrieSet() {
    }

    /**
     * Returns the number of strings in this set.
     *
     * @return the number of strings in this set
     */
    public int size() {
        return n;
    }

    /**
     * Is the set empty?
     *
     * @return {@code true} if the set is empty, and {@code false} otherwise
     */
    public boolean isEmpty() {
        return n == 0;
    }

    /**
     * Does the set contain the given key?
     *
     * @param key the key
     * @return {@code true} if this set contains {@code key} and
     * {@code false} otherwise
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public boolean contains(String key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        Node x = get(root, key, 0);
        if (x == null) return false;
        return x.isString;
    }

    // return sub-trie corresponding to given key
    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        char c = key.charAt(d);
        if (c < x.c) return get(x.left, key, d);
        else if (c > x.c) return get(x.right, key, d);
        else if (d < key.length() - 1) return get(x.mid, key, d + 1);
        else return x;
    }

    /**
     * Adds the key to the set if it is not already present.
     *
     * @param key the key to add
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void add(String key) {
        if (key == null) throw new IllegalArgumentException("argument to add() is null");
        root = add(root, key, 0);
    }

    private Node add(Node x, String key, int d) {
        char c = key.charAt(d);
        if (x == null) {
            x = new Node();
            x.c = c;
        }
        if (c < x.c) x.left = add(x.left, key, d);
        else if (c > x.c) x.right = add(x.right, key, d);
        else if (d < key.length() - 1) x.mid = add(x.mid, key, d + 1);
        else {
            if (!x.isString) n++;
            x.isString = true;
        }
        return x;
    }

    /**
     * Returns all the keys in the set, as an iterator.
     * To iterate over all the keys in a set named {@code set}, use the
     * foreach notation: {@code for (Key key : set)}.
     *
     * @return an iterator to all the keys in the set
     */
    public Iterator<String> iterator() {
        Queue<String> queue = new Queue<String>();
        collect(root, new StringBuilder(), queue);
        return queue.iterator();
    }

    /**
     * Returns all the keys in the set that start with {@code prefix}.
     *
     * @param prefix the prefix
     * @return all the keys in the set that start with {@code prefix},
     * as an iterable
     */
    public Iterable<String> keysWithPrefix(String prefix) {
        if (prefix == null)
            throw new IllegalArgumentException("calls keysWithPrefix() with null argument");
        Queue<String> queue = new Queue<String>();
        Node x = get(root, prefix, 0);
        if (x == null) return queue;
        if (x.isString) queue.enqueue(prefix);
        collect(x.mid, new StringBuilder(prefix), queue);
        return queue;
    }

    // all keys in sub-trie rooted at x with given prefix
    private void collect(Node x, StringBuilder prefix, Queue<String> queue) {
        if (x == null) return;
        // left collect
        collect(x.left, prefix, queue);
        // middle collect
        if (x.isString) queue.enqueue(prefix.toString() + x.c);
        collect(x.mid, prefix.append(x.c), queue);
        prefix.deleteCharAt(prefix.length() - 1);
        // right collect
        collect(x.right, prefix, queue);
    }

    /**
     * Unit tests the {@code TrieSet} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        // build set from file
        TrieSet st = new TrieSet();
        In in = new In(args[0]);
        while (!in.isEmpty()) {
            String key = in.readString();
            st.add(key);
        }

        // print results

        StdOut.println("keys(" + st.size() + ") :");
        for (String key : st)
            StdOut.println(key);
        StdOut.println();

        StdOut.println("keysWithPrefix(\"A\"):");
        for (String s : st.keysWithPrefix("A"))
            StdOut.println(s);
        StdOut.println();

        StdOut.println("keysWithPrefix(\"MI\"):");
        for (String s : st.keysWithPrefix("MI"))
            StdOut.println(s);
        StdOut.println();

        StdOut.println("keysWithPrefix(\"O\"):");
        for (String s : st.keysWithPrefix("O"))
            StdOut.println(s);
        StdOut.println();

        StdOut.println("keysWithPrefix(\"Y\"):");
        for (String s : st.keysWithPrefix("Y"))
            StdOut.println(s);
        StdOut.println();

    }
}

/******************************************************************************
 *  Copyright 2002-2022, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
