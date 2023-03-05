import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

public class Client {
    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        // Stopwatch timer1 = new Stopwatch();
        // BruteCollinearPoints collinear_ = new BruteCollinearPoints(points);
        // StdOut.printf("Cost Time = %f (sec)\n", timer1.elapsedTime());
        // StdOut.println("Collinear Line Segment # = " + collinear_.numberOfSegments());

        Stopwatch timer2 = new Stopwatch();
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        StdOut.printf("Cost Time = %f (sec)\n", timer2.elapsedTime());
        StdOut.println("Collinear Line Segment # = " + collinear.numberOfSegments());

        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
