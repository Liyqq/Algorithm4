import edu.princeton.cs.algs4.ResizingArrayBag;

import java.util.Arrays;

public class FastCollinearPoints {
    private final LineSegment[] lineSegments;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        validate(points);

        ResizingArrayBag<LineSegment> lineSegBag = new ResizingArrayBag<>();
        collinear(points, lineSegBag);

        int i = 0;
        lineSegments = new LineSegment[lineSegBag.size()];
        for (LineSegment lineSeg : lineSegBag) lineSegments[i++] = lineSeg;
    }

    // the number of line segments
    public int numberOfSegments() {
        return lineSegments.length;
    }

    // the line segments
    public LineSegment[] segments() {
        return lineSegments.clone();
    }

    // check the points argument validation
    private void validate(Point[] points) {
        if (points == null) throw new IllegalArgumentException("pasted argument is null!");

        for (int i = 0; i < points.length; i++)
            if (points[i] == null)
                throw new IllegalArgumentException("points array contains null value");

        Point[] copy = points.clone();
        Arrays.sort(copy);
        for (int i = 1; i < copy.length; i++)
            if (copy[i].compareTo(copy[i - 1]) == 0)
                throw new IllegalArgumentException("points array contains duplicated point");
    }

    // find collinear points
    private void collinear(Point[] points, ResizingArrayBag<LineSegment> lineSegBag) {
        Point[] auxPoints = points.clone();

        // find all collinear end points pair
        for (int i = 0; i < points.length; i++) {
            Arrays.sort(auxPoints, points[i].slopeOrder());

            int j = 1, n = auxPoints.length - 2;
            while (j < n) {
                int upperBoundIndex = upperBound(auxPoints, points[i], j);
                if (upperBoundIndex - j > 2) {
                    Arrays.sort(auxPoints, j, upperBoundIndex);
                    // add line segment iff i-th point is the min point in the collinear points to avoid duplicated addition
                    if (points[i].compareTo(auxPoints[j]) < 0)
                        lineSegBag.add(new LineSegment(points[i], auxPoints[upperBoundIndex - 1]));
                }
                j = upperBoundIndex;
            }
        }
    }

    // search for upper bound of with sorted slope order
    private int upperBound(Point[] sortedPoints, Point p, int start) {
        double slope = p.slopeTo(sortedPoints[start]);
        int i = start + 1;
        while ((i < sortedPoints.length) && (slope == p.slopeTo(sortedPoints[i]))) i++;
        return i;
    }
}
