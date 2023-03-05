import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.LinkedList;

public class NoOptimizedSAP {
    private final Digraph digraph;
    private final boolean[] markedV, markedW;
    private final int[] distV, distW;

    // constructor takes a digraph (not necessarily a DAG)
    public NoOptimizedSAP(Digraph G) {
        digraph = new Digraph(G);
        int v = digraph.V();
        markedV = new boolean[v]; // default false
        markedW = new boolean[v];
        distV = new int[v];
        distW = new int[v];
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        if (v == w) return 0;
        return findAncestorOrLength(v, w, true);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        if (v == w) return v;
        return findAncestorOrLength(v, w, false);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (!validateVertices(v)) return -1;
        if (!validateVertices(w)) return -1;
        HashSet<Integer> set = new HashSet<>();
        for (int t : v) set.add(t);
        for (int t : w) if (set.contains(t)) return 0;
        return findAncestorOrLength(v, w, true);
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (!validateVertices(v)) return -1;
        if (!validateVertices(w)) return -1;
        HashSet<Integer> set = new HashSet<>();
        for (int t : v) set.add(t);
        for (int t : w) if (set.contains(t)) return t;
        return findAncestorOrLength(v, w, false);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        NoOptimizedSAP sap = new NoOptimizedSAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

    private int findAncestorOrLength(int v, int w, boolean distance) {
        distV[v] = 0;
        distW[w] = 0;
        markedV[v] = true;
        markedW[w] = true;
        Queue<Integer> qV = new Queue<>(), qW = new Queue<>();
        qV.enqueue(v);
        qW.enqueue(w);
        return bfs(qV, qW, distance);
    }


    private int findAncestorOrLength(Iterable<Integer> v, Iterable<Integer> w, boolean distance) {
        for (int t : v) distV[t] = 0;
        for (int t : w) distW[t] = 0;
        for (int t : v) markedV[t] = true;
        for (int t : w) markedW[t] = true;
        Queue<Integer> qV = new Queue<>(), qW = new Queue<>();
        for (int t : v) qV.enqueue(t);
        for (int t : w) qW.enqueue(t);
        return bfs(qV, qW, distance);
    }

    private int bfs(Queue<Integer> qV, Queue<Integer> qW, boolean distance) {
        LinkedList<Integer> modifiedV = new LinkedList<>(), modifiedW = new LinkedList<>();
        for (int v : qV) modifiedV.add(v);
        for (int w : qW) modifiedW.add(w);

        int ancestor = -1, shortestLen = Integer.MAX_VALUE;
        int distSoFarV = 0, distSoFarW = 0;
        while (!qV.isEmpty() || !qW.isEmpty()) {
            while (!qV.isEmpty()) {
                if (distSoFarV + 1 == distV[qV.peek()]) break; // only search same level

                int pioneer = qV.dequeue();
                for (int adjV : digraph.adj(pioneer)) {
                    if (markedW[adjV]) {
                        int tmpLen = distW[adjV] + distSoFarV + 1;
                        if (tmpLen < shortestLen) {
                            ancestor = adjV;
                            shortestLen = tmpLen;
                        }
                    }
                    if (markedV[adjV]) continue; // had searched
                    markedV[adjV] = true;
                    distV[adjV] = distSoFarV + 1;
                    qV.enqueue(adjV);
                    modifiedV.add(adjV);
                }
            }
            distSoFarV++;

            while (!qW.isEmpty()) {
                if (distSoFarW + 1 == distW[qW.peek()]) break; // only search same level

                int pioneer = qW.dequeue();
                for (int adjW : digraph.adj(pioneer)) {
                    if (markedV[adjW]) {
                        int tmpLen = distV[adjW] + distSoFarW + 1;
                        if (tmpLen < shortestLen) {
                            ancestor = adjW;
                            shortestLen = tmpLen;
                        }
                    }
                    if (markedW[adjW]) continue; // had searched
                    markedW[adjW] = true;
                    distW[adjW] = distSoFarW + 1;
                    qW.enqueue(adjW);
                    modifiedW.add(adjW);
                }
            }
            distSoFarW++;
        }
        reinitial(modifiedV, modifiedW);
        if (ancestor == -1) return -1; // not found
        return distance ? shortestLen : ancestor;
    }

    private void reinitial(Iterable<Integer> modifiedV, Iterable<Integer> modifiedW) {
        for (int mV : modifiedV) markedV[mV] = false;
        for (int mW : modifiedW) markedW[mW] = false;
    }

    private void validateVertex(int v) {
        int diV = digraph.V();
        if (v < 0 || v >= diV) throw new IllegalArgumentException(
                "vertex " + v + " is not between 0 and " + (diV - 1));
    }

    private boolean validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) throw new IllegalArgumentException("argument is null");
        int vertexCount = 0;
        for (Integer v : vertices) {
            vertexCount++;
            if (v == null) throw new IllegalArgumentException("vertex is null");
            validateVertex(v);
        }
        return vertexCount != 0;
    }
}
