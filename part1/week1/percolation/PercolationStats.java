import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private double meanRes, stddevRes, confidenceLoRes, confidenceHiRes;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0) throw new IllegalArgumentException("n must be greater than 0!");
        if (trials <= 0) throw new IllegalArgumentException("trials must be greater than 0!");

        simulate(n, trials);
    }

    // sample mean of percolation threshold
    public double mean() {
        return meanRes;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return stddevRes;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return confidenceLoRes;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return confidenceHiRes;
    }

    // test client (see below)
    public static void main(String[] args) {
        if (args.length == 2) {
            int n = Integer.parseInt(args[0]);
            int trials = Integer.parseInt(args[1]);
            PercolationStats ps = new PercolationStats(n, trials);

            StdOut.printf("mean\t\t\t = %f\n", ps.mean());
            StdOut.printf("stddev\t\t\t = %f\n", ps.stddev());
            StdOut.printf("95%% confidence interval  = [%f, %f]\n", ps.confidenceLo(),
                          ps.confidenceHi());
        }
    }

    // main simulate
    private void simulate(int n, int trials) {
        // trials simulations
        double[] thresholds = new double[trials];
        for (int i = 0; i < trials; i++) thresholds[i] = simulateOnce(n);

        // compute statistics
        meanRes = StdStats.mean(thresholds);
        stddevRes = StdStats.stddev(thresholds);

        double sqrtT = Math.sqrt(trials);
        double temp = 1.96 * stddevRes / sqrtT;
        confidenceLoRes = meanRes - temp;
        confidenceHiRes = meanRes + temp;
    }

    // helper: simulate once
    private double simulateOnce(int n) {
        Percolation p = new Percolation(n);
        while (!p.percolates()) {
            int row = StdRandom.uniformInt(n) + 1;
            int col = StdRandom.uniformInt(n) + 1;
            p.open(row, col);
        }
        return (double) p.numberOfOpenSites() / (n * n);
    }
}
