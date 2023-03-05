/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *  Dependencies: none
 *
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Comparator;

public class Point implements Comparable<Point> {

    private final int x;     // x-coordinate of this point
    private final int y;     // y-coordinate of this point

    /**
     * Initializes a new point.
     *
     * @param x the <em>x</em>-coordinate of the point
     * @param y the <em>y</em>-coordinate of the point
     */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point
     * to standard draw.
     *
     * @param that the other point
     */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0). For completeness, the slope is defined to be
     * +0.0 if the line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertical;
     * and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     *
     * @param that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(Point that) {
        if (compareTo(that) == 0) return Double.NEGATIVE_INFINITY;
        if (x == that.x) return Double.POSITIVE_INFINITY;
        if (y == that.y) return +0.0;
        return (double) (y - that.y) / (x - that.x);
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param that the other point
     * @return the value <tt>0</tt> if this point is equal to the argument
     * point (x0 = x1 and y0 = y1);
     * a negative integer if this point is less than the argument
     * point; and a positive integer if this point is greater than the
     * argument point
     */
    public int compareTo(Point that) {
        if (y < that.y) return -1;
        if (y > that.y) return +1;
        return Integer.compare(x, that.x);
    }

    /**
     * Compares two points by the slope they make with this point.
     * The slope is defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder() {
        return new SlopeOrder();
    }

    private class SlopeOrder implements Comparator<Point> {
        public int compare(Point p1, Point p2) {
            double slopeOfPToP1 = Point.this.slopeTo(p1);
            double slopeOfPToP2 = Point.this.slopeTo(p2);
            return Double.compare(slopeOfPToP1, slopeOfPToP2);
        }
    }

    /**
     * Returns a string representation of this point.
     * This method is provided for debugging;
     * your program should not rely on the format of the string representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
        testCompare();
        testSlopeCompute();
        testSloperOrder();
    }

    // Point compareTo method Unit test
    private static void testCompare() {
        Point[] points = { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 0) };
        int[][] compareResult = {
                { 0, -1, -1, -1 },
                { +1, 0, -1, +1 },
                { +1, +1, 0, +1 },
                { +1, -1, -1, 0 }
        };

        for (int i = 0; i < points.length; i++) {
            boolean passed = true;
            for (int j = 0; j < points.length; j++) {
                boolean equalToRes = true;
                int compareRes = points[i].compareTo(points[j]);
                if (compareResult[i][j] == -1) equalToRes = compareRes < 0;
                else if (compareResult[i][j] == +1) equalToRes = compareRes > 0;
                else if (compareResult[i][j] == 0) equalToRes = compareRes == 0;
                if (!equalToRes) {
                    StdOut.printf("\nresult of %s compare with %s except %d but observed %d\n",
                                  points[i].toString(), points[j].toString(), compareResult[i][j],
                                  compareRes);
                    passed = false;
                }
            }
            String s = points[i].toString() + " compare with other points test ==> ";
            s += (passed ? "\033[1;32m PASSED \033[0m" : "\033[1;31m FAILED \033[0m");
            StdOut.println(s);
        }
    }

    // Point slopeTo method Unit test
    private static void testSlopeCompute() {
        Point[] points = { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 0) };
        double[][] slopeResult = {
                { Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, +1.0, +0.0 },
                { Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, +0.0, -1.0 },
                { +1.0, +0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY },
                { +0.0, -1.0, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY }
        };

        for (int i = 0; i < points.length; i++) {
            boolean passed = true;
            for (int j = 0; j < points.length; j++) {
                double slopeRes = points[i].slopeTo(points[j]);
                if (slopeResult[i][j] != slopeRes) {
                    StdOut.printf("\nresult of %s slope to %s except %f but observed %f\n",
                                  points[i].toString(), points[j].toString(), slopeResult[i][j],
                                  slopeRes);
                    passed = false;
                }
            }
            String s = points[i].toString() + " slope to other points test ==> ";
            s += (passed ? "\033[1;32m PASSED \033[0m" : "\033[1;31m FAILED \033[0m");
            StdOut.println(s);
        }
    }

    // Point SlopeOrder  Unit test
    private static void testSloperOrder() {
        Point[] points = { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 0) };
        Point[] auxPoints = points.clone();
        Point[][] slopeOrderResult = {
                { points[0], points[3], points[2], points[1] },
                { points[1], points[3], points[2], points[0] },
                { points[2], points[1], points[0], points[3] },
                { points[3], points[1], points[0], points[2] }
        };

        for (int i = 0; i < points.length; i++) {
            boolean passed = true;
            Arrays.sort(auxPoints, points[i].slopeOrder());
            for (int j = 0; j < points.length; j++) {

                if (slopeOrderResult[i][j].compareTo(auxPoints[j]) != 0) {
                    StdOut.printf("\n%s SlopeOrder array except %s but observed %s index=%d\n",
                                  points[i].toString(), slopeOrderResult[i][j], auxPoints[j], j);
                    passed = false;
                }
            }
            String s = points[i].toString() + " SlopeOrder to other points sort test ==> ";
            s += (passed ? "\033[1;32m PASSED \033[0m" : "\033[1;31m FAILED \033[0m");
            StdOut.println(s);
        }
    }
}
