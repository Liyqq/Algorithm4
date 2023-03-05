import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int n;
    private WeightedQuickUnionUF wufTopBottom;
    private WeightedQuickUnionUF wufTop;
    private boolean[][] siteMap;  // record site status
    private int openedSiteCount = 0;  // number of opened site


    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) throw new IllegalArgumentException("n must be greater than 0!");
        this.n = n;
        wufTopBottom = new WeightedQuickUnionUF(n * n + 2); // add 2 for top and bottom virtual site
        wufTop = new WeightedQuickUnionUF(n * n + 1); // only add top virtual site
        siteMap = new boolean[n][n]; // not record virtual sites as they are opened all the time
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        // bounds check
        validate(row, col);

        // open site
        if (isOpen(row, col)) return;
        siteMap[row - 1][col - 1] = true;
        openedSiteCount++;

        // union adjacent open site
        int idx = (row - 1) * n + col; // index of UF
        if (row != 1 && isOpen(row - 1, col)) { // upper
            wufTopBottom.union(idx, idx - n);
            wufTop.union(idx, idx - n);
        }
        if (row != n && isOpen(row + 1, col)) { // lower
            wufTopBottom.union(idx, idx + n);
            wufTop.union(idx, idx + n);
        }
        if (col != 1 && isOpen(row, col - 1)) { // left
            wufTopBottom.union(idx, idx - 1);
            wufTop.union(idx, idx - 1);
        }
        if (col != n && isOpen(row, col + 1)) { // right
            wufTopBottom.union(idx, idx + 1);
            wufTop.union(idx, idx + 1);
        }

        // union with top and bottom virtual site
        if (row == 1) {
            wufTopBottom.union(idx, 0);
            wufTop.union(idx, 0);
        }
        if (row == n) wufTopBottom.union(idx, n * n + 1);
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        validate(row, col);
        return siteMap[row - 1][col - 1];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        validate(row, col);
        int idx = (row - 1) * n + col; // index of UF
        return wufTop.find(idx) == wufTop.find(0); // is connected with top virtual site
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openedSiteCount;
    }

    // does the system percolate?
    public boolean percolates() {
        return wufTopBottom.find(0) == wufTopBottom.find(n * n + 1);
    }

    // test client (optional)
    public static void main(String[] args) {
    }

    // (row, col) bounds check
    private void validate(int row, int col) {
        if (row < 1 || row > n)
            throw new IllegalArgumentException("row index " + row + " is not between 1 and " + n);
        if (col < 1 || col > n)
            throw new IllegalArgumentException("col index " + col + " is not between 1 and " + n);
    }
}
