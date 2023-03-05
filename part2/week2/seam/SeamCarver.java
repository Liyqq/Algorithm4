import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class SeamCarver {
    private static final double BORDER_ENERGY = 1000.0;
    private int[][] rgb;
    private double[][] energy;
    private int w, h;
    private boolean isOringinal = true;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("picture is null!");
        w = picture.width();
        h = picture.height();

        rgb = new int[w][h];
        for (int col = 0; col < w; col++)
            for (int row = 0; row < h; row++)
                rgb[col][row] = picture.getRGB(col, row);
        energy = new double[w][h];
        for (int col = 0; col < w; col++)
            for (int row = 0; row < h; row++)
                energy[col][row] = calculateEnergy(col, row);
    }

    // current picture
    public Picture picture() {
        transposeToOriginal();
        Picture p = new Picture(w, h);
        for (int col = 0; col < w; col++)
            for (int row = 0; row < h; row++)
                p.setRGB(col, row, rgb[col][row]);
        return p;
    }

    // width of current picture
    public int width() {
        return isOringinal ? w : h;
    }

    // height of current picture
    public int height() {
        return isOringinal ? h : w;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (!isOringinal) {
            int temp = x;
            x = y;
            y = temp;
        }
        if (x < 0 || x >= w || y < 0 || y >= h)
            throw new IllegalArgumentException("x or y is outside its prescribed range.");
        return energy[x][y];

    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transposeToTranspose();
        return findSeam();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        transposeToOriginal();
        return findSeam();
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        transposeToOriginal();
        if (h < 2)
            throw new IllegalArgumentException("height of the picture is less than or equal to 1");
        removeSeam(seam);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        transposeToTranspose();
        if (h < 2)
            throw new IllegalArgumentException("width of the picture is less than or equal to 1");
        removeSeam(seam);
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        StdOut.printf("image is %d columns by %d rows\n", picture.width(), picture.height());
        picture.show();
        SeamCarver sc = new SeamCarver(picture);

        StdOut.printf("Displaying produced picture(%d x %d)!\n", picture.width(), picture.height());
        Picture producedP = sc.picture();
        producedP.show();
    }

    private int[] findSeam() {
        // shortcut
        int[] shortcutSeam = shortcutSeam();
        if (shortcutSeam.length != 0) return shortcutSeam;

        // find all the lowest energy seam from col 0 to w-1
        int[][] comeFrom = new int[w][h];
        double[][] energySoFar = new double[w][h];
        for (int i = 0; i < w; i++) Arrays.fill(energySoFar[i], Double.POSITIVE_INFINITY);
        for (int i = 0; i < w; i++) energySoFar[i][0] = BORDER_ENERGY;
        // traverse as topological order
        for (int col = w - 1; col > -1; col--) {
            int r = 0, c = col;
            while (r < h && c < w) relax(r++, c++, comeFrom, energySoFar);
        }
        for (int row = 1; row < h; row++) {
            int r = row, c = 0;
            while (r < h && c < w) relax(r++, c++, comeFrom, energySoFar);
        }
        // find the lowest energy seam
        return findLowestSeam(comeFrom, energySoFar);
    }

    private void relax(int row, int col, int[][] comeFrom, double[][] energySoFar) {
        if (row == h - 1) return;
        double pixelEnergySoFar = energySoFar[col][row];
        // left
        if (col != 0)
            relaxPixel(col, row + 1, col - 1, pixelEnergySoFar, comeFrom, energySoFar);
        // middle
        relaxPixel(col, row + 1, col, pixelEnergySoFar, comeFrom, energySoFar);
        // right
        if (col != w - 1)
            relaxPixel(col, row + 1, col + 1, pixelEnergySoFar, comeFrom, energySoFar);
    }

    private void relaxPixel(int col, int rowAdj, int colAdj, double pixelEnergySoFar,
                            int[][] comeFrom,
                            double[][] energySoFar) {
        double adjPixelEnergy = energy[colAdj][rowAdj];
        double adjPixelEnergySoFar = energySoFar[colAdj][rowAdj];
        if (pixelEnergySoFar + adjPixelEnergy < adjPixelEnergySoFar) {
            energySoFar[colAdj][rowAdj] = pixelEnergySoFar + adjPixelEnergy;
            comeFrom[colAdj][rowAdj] = col;
        }
    }

    private int[] shortcutSeam() {
        if (w <= 3) {
            int[] seam = new int[h];
            if (w == 3) Arrays.fill(seam, 1);
            else Arrays.fill(seam, w - 1);
            return seam;
        }
        if (h <= 3) {
            int[] seam = new int[h];
            if (h == 3) {
                int minCol = 0;
                double minEnergy = BORDER_ENERGY;
                for (int i = 1; i < w; i++) {
                    if (energy[i][1] > minEnergy) continue;
                    minCol = i;
                    minEnergy = energy[i][1];
                }
                Arrays.fill(seam, minCol);
            }
            else Arrays.fill(seam, w - 1);
            return seam;
        }
        return new int[0];
    }

    private int[] findLowestSeam(int[][] comeFrom, double[][] energySoFar) {
        int lowestEnergyCol = 0, n = w, row = h - 1;
        double lowestEnergy = Double.POSITIVE_INFINITY;
        for (int c = 0; c < n; c++) {
            if (lowestEnergy > energySoFar[c][row]) {
                lowestEnergyCol = c;
                lowestEnergy = energySoFar[c][row];
            }
        }
        int[] seam = new int[h];
        for (int i = h - 1; i > -1; i--) {
            seam[i] = lowestEnergyCol;
            lowestEnergyCol = comeFrom[lowestEnergyCol][i];
        }
        return seam;
    }

    private void removeSeam(int[] seam) {
        validateSeam(seam, w, h);
        for (int c = 0; c < w; c++)
            System.arraycopy(rgb[c], seam[c] + 1, rgb[c], seam[c], h - seam[c] - 1);
        for (int c = 0; c < w; c++)
            System.arraycopy(energy[c], seam[c] + 1, energy[c], seam[c], h - seam[c] - 1);
        h--;

        // recalculate energy
        if (h + 1 == 2) {
            energy = new double[w][1];
            for (int c = 0; c < w; c++) energy[c][0] = BORDER_ENERGY;
        }
        else if (h + 1 == 3) {
            energy = new double[w][2];
            for (int c = 0; c < w; c++) Arrays.fill(energy[c], BORDER_ENERGY);
        }
        else {
            for (int c = 0; c < w; c++) {
                int row = seam[c];
                if (row != h) energy[c][row] = calculateEnergy(c, row);
                if (row == 0) continue;
                if (row != 1) energy[c][row - 1] = calculateEnergy(c, row - 1);
            }
        }
    }

    private void validateSeam(int[] seam, int validLength, int validBorder) {
        if (seam == null) throw new IllegalArgumentException("seam is null!");
        if (seam.length != validLength) throw new IllegalArgumentException("wrong length!");
        for (int i = 0; i < validLength; i++) {
            if (seam[i] < 0 || seam[i] >= validBorder)
                throw new IllegalArgumentException("seam entry is outside its prescribed range!");
        }
        for (int i = 1; i < validLength; i++) {
            int differ = seam[i - 1] - seam[i];
            if (differ > 1 || differ < -1)
                throw new IllegalArgumentException("two adjacent entries differ by more than 1!");
        }
    }

    private double calculateEnergy(int col, int row) {
        if (col == 0 || col + 1 == w || row == 0 || row + 1 == h) return BORDER_ENERGY;
        int dxSquare = deltaXSquare(col, row), dySquare = deltaYSquare(col, row);
        return Math.sqrt(dxSquare + dySquare);
    }

    private int deltaXSquare(int col, int row) {
        int rgbL = rgb[col - 1][row], rgbR = rgb[col + 1][row];
        int rL = (rgbL >> 16) & 0xFF, gL = (rgbL >> 8) & 0xFF, bL = rgbL & 0xFF;
        int rR = (rgbR >> 16) & 0xFF, gR = (rgbR >> 8) & 0xFF, bR = rgbR & 0xFF;
        int dxR = rL - rR, dxG = gL - gR, dxB = bL - bR;
        return dxR * dxR + dxG * dxG + dxB * dxB;
    }

    private int deltaYSquare(int col, int row) {
        int rgbU = rgb[col][row - 1], rgbL = rgb[col][row + 1];
        int rU = (rgbU >> 16) & 0xFF, gU = (rgbU >> 8) & 0xFF, bU = rgbU & 0xFF;
        int rL = (rgbL >> 16) & 0xFF, gL = (rgbL >> 8) & 0xFF, bL = rgbL & 0xFF;
        int dyR = rU - rL, dyG = gU - gL, dyB = bU - bL;
        return dyR * dyR + dyG * dyG + dyB * dyB;
    }

    private void transposeToOriginal() {
        if (isOringinal) return;
        transpose();
        isOringinal = true;
    }

    private void transposeToTranspose() {
        if (!isOringinal) return;
        transpose();
        isOringinal = false;
    }

    private void transpose() {
        int wNew = h, hNew = w;
        w = wNew;
        h = hNew;

        int[][] rgbNew = new int[wNew][hNew];
        for (int col = 0; col < wNew; col++)
            for (int row = 0; row < hNew; row++)
                rgbNew[col][row] = rgb[row][col];
        rgb = rgbNew;

        double[][] energyNew = new double[wNew][hNew];
        for (int col = 0; col < wNew; col++)
            for (int row = 0; row < hNew; row++)
                energyNew[col][row] = energy[row][col];
        energy = energyNew;
    }
}
