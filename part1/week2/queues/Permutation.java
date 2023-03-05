import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Permutation {
    public static void main(String[] args) {
        if (args.length == 1) {
            int k = Integer.parseInt(args[0]);
            if (k == 0) return;
            RandomizedQueue<String> randomizedQueue = new RandomizedQueue<>();

            double count = 0;
            while (!StdIn.isEmpty()) {
                String item = StdIn.readString();
                if (k == randomizedQueue.size()) {
                    if (StdRandom.bernoulli(k / ++count)) {
                        randomizedQueue.dequeue();
                        randomizedQueue.enqueue(item);
                    }
                    continue;
                }
                randomizedQueue.enqueue(item);
                count++;
            }
            for (String item : randomizedQueue) StdOut.println(item);
        }
    }
}
