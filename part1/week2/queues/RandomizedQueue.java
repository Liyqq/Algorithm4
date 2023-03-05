import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    // initial capacity of underlying resizing a
    private static final int INIT_CAPACITY = 8;

    private Item[] a;
    private int n;

    // construct an empty randomized queue
    public RandomizedQueue() {
        a = (Item[]) new Object[INIT_CAPACITY];
        n = 0;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return n;
    }

    // resize the underlying array holding the elements
    private void resize(int capacity) {
        assert capacity > n;

        Item[] copy = (Item[]) new Object[capacity];
        for (int i = 0; i < n; i++) {
            copy[i] = a[i];
        }
        a = copy;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException("item is null!");
        if (n == a.length) resize(2 * a.length);
        if (n == 0) {
            a[n++] = item;
            return;
        }

        // swap and store
        int randomInterchangeIndex = StdRandom.uniformInt(n + 1);
        if (n == randomInterchangeIndex) { // no interchange
            a[n++] = item;
            return;
        }
        a[n++] = a[randomInterchangeIndex];
        a[randomInterchangeIndex] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException("RandomizedQueue underflow!");

        Item item = a[--n];
        a[n] = null; // to avoid loitering
        if (n > 0 && n == a.length / 4) resize(a.length / 2);

        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException("RandomizedQueue underflow!");

        int randomIndex = StdRandom.uniformInt(n);
        return a[randomIndex];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomArrayIterator();
    }

    private class RandomArrayIterator implements Iterator<Item> {
        private int current;
        private Item[] shuffledArray;

        public RandomArrayIterator() {
            current = 0;
            shuffledArray = (Item[]) new Object[n];
            for (int i = 0; i < n; i++) shuffledArray[i] = a[i];
            StdRandom.shuffle(shuffledArray);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext() {
            return current != shuffledArray.length;
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            return shuffledArray[current++];
        }

    }

    // unit testing (required)
    public static void main(String[] args) {
        // randomly call enqueue, dequeue and sample on both first and last end
        RandomizedQueue<Integer> randomizedQueue = new RandomizedQueue<>();
        int randomOperationCount = 20; // default 20 times
        String[] opStr = { "enqueue", "dequeue", "sample" };
        if (args.length == 1) randomOperationCount = Integer.parseInt(args[0]);

        int count = 0;
        while (count++ < randomOperationCount) {
            int op = StdRandom.uniformInt(opStr.length);
            int item = StdRandom.uniformInt(1000);

            StdOut.printf("Op#: %6d -- OpType: %7s -- randomItem: %3d\n", count, opStr[op], item);
            try {
                StdOut.printf("OpReturn: %3d\n", testOpHelper(randomizedQueue, op, item));
            }
            catch (NoSuchElementException nsee) {
                StdOut.printf("\033[1;31mExceptionMsg: %s\033[0m\n", nsee.getMessage());
            }

            StdOut.printf("RandomizedQueue(size=%6d): ", randomizedQueue.size());
            for (int i : randomizedQueue) {
                StdOut.printf("%3d ", i);
            }
            StdOut.println();
            StdOut.println();
        }
        int trials = 12000;
        double abProp = testRandomness(trials);
        StdOut.printf("RandomTest(T=%8d): AB=%8f -- BA=%8f \n", trials, abProp, 1 - abProp);

    }

    private static int testOpHelper(RandomizedQueue<Integer> randomizedQueue, int operation,
                                    int item) {
        switch (operation) {
            case 0: // enqueue
                randomizedQueue.enqueue(item);
                return -1;
            case 1: // randomly dequeue
                return randomizedQueue.dequeue();
            case 2: // sample
                return randomizedQueue.sample();
            default:
                return -1;
        }
    }

    private static double testRandomness(int trials) {
        double count = 0;
        for (int i = 0; i < trials; i++) {
            RandomizedQueue<String> rq = new RandomizedQueue<>();
            rq.enqueue("A");
            rq.enqueue("B");
            String res = rq.dequeue();
            res += rq.dequeue();
            if (res.equals("AB")) count++;
        }
        return count / trials;
    }
}


