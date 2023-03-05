import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private int n;      // number of elements on deque
    private Node head;  // begin of the deque
    private Node tail;  // end of the deque

    private class Node {
        private Item item;
        private Node prev, next;
    }

    // construct an empty deque
    public Deque() {
        head = null;
        tail = null;
        n = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the deque
    public int size() {
        return n;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException("item is null!");

        Node oldHead = head;
        head = new Node();
        head.item = item;
        head.prev = null;
        head.next = oldHead;
        if (isEmpty()) tail = head; // empty specified check
        else oldHead.prev = head;
        n++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException("item is null!");

        Node oldTail = tail;
        tail = new Node();
        tail.item = item;
        tail.prev = oldTail;
        tail.next = null;
        if (isEmpty()) head = tail; // empty specified check
        else oldTail.next = tail;
        n++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("Deque underflow!");

        Item item = head.item;
        head = head.next;
        n--;
        if (isEmpty()) tail = null;
        else head.prev = null;

        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException("Deque underflow!");

        Item item = tail.item;
        tail = tail.prev;
        n--;
        if (isEmpty()) head = null;
        else tail.next = null;

        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DoubleLinkedIterator();
    }

    private class DoubleLinkedIterator implements Iterator<Item> {
        private Node current = head;

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        // randomly operate add and remove operations on both first and last end
        Deque<Integer> deque = new Deque<>();
        int randomOperationCount = 20; // default 20 times
        String[] opStr = { "addFirst", "addLast", "removeFirst", "removeLast" };
        if (args.length == 1) randomOperationCount = Integer.parseInt(args[0]);

        int count = 0;
        while (count++ < randomOperationCount) {
            int op = StdRandom.uniformInt(opStr.length);
            int item = StdRandom.uniformInt(1000);

            StdOut.printf("Op#: %6d -- OpType: %11s -- randomItem: %3d\n", count, opStr[op], item);
            try {
                StdOut.printf("OpReturn: %3d\n", testOpHelper(deque, op, item));
            }
            catch (NoSuchElementException nsee) {
                StdOut.printf("\033[1;31mExceptionMsg: %s\033[0m\n", nsee.getMessage());
            }

            StdOut.printf("Deque(size=%6d): ", deque.size());
            for (int i : deque) {
                StdOut.printf("%3d ", i);
            }
            StdOut.println();
            StdOut.println();
        }

    }

    private static int testOpHelper(Deque<Integer> deque, int operation, int item) {
        switch (operation) {
            case 0: // addFirst
                deque.addFirst(item);
                return -1;
            case 1: // addLast
                deque.addLast(item);
                return -1;
            case 2: // removeFirst
                return deque.removeFirst();
            case 3: // removeLast
                return deque.removeLast();
            default:
                return -1;
        }
    }
}
