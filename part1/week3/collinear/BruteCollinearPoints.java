import edu.princeton.cs.algs4.ResizingArrayBag;

import java.util.Arrays;

public class BruteCollinearPoints {
    private final LineSegment[] lineSegments;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        validate(points);

        ResizingArrayBag<LineSegment> lineSegBag = new ResizingArrayBag<>();
        collinear(points, lineSegBag);

        int i = 0;
        lineSegments = new LineSegment[lineSegBag.size()];
        for (LineSegment lineSeg : lineSegBag) lineSegments[i++] = lineSeg;
    }

    // return the number of line segments
    public int numberOfSegments() {
        return lineSegments.length;
    }

    // return an array of line segments
    public LineSegment[] segments() {
        return lineSegments.clone();
    }

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

    private void collinear(Point[] points, ResizingArrayBag<LineSegment> lineSegBag) {
        int n = points.length - 3;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n + 1; j++) {
                for (int k = j + 1; k < n + 2; k++) {
                    // check for 3 points collinear
                    double slopeOfPiToPj = points[i].slopeTo(points[j]);
                    double slopeOfPiToPk = points[i].slopeTo(points[k]);
                    if (slopeOfPiToPj != slopeOfPiToPk) continue;

                    // // check for 4 points collinear
                    for (int q = k + 1; q < n + 3; q++) {
                        double slopeOfPiToPq = points[i].slopeTo(points[q]);
                        if (slopeOfPiToPj == slopeOfPiToPq) {
                            Point[] collinearP = { points[i], points[j], points[k], points[q] };
                            Arrays.sort(collinearP);
                            lineSegBag.add(new LineSegment(collinearP[0], collinearP[3]));
                        }
                    }
                }
            }
        }
    }
}
