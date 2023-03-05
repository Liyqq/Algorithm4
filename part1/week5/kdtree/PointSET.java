import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.TreeSet;

public class PointSET {
    private TreeSet<Point2D> set;

    // construct an empty set of points
    public PointSET() {
        set = new TreeSet<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return set.isEmpty();
    }

    // number of points in the set
    public int size() {
        return set.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        validate(p);
        set.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        validate(p);
        return set.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : set) p.draw();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        validate(rect);

        Point2D LB = new Point2D(rect.xmin(), rect.ymin());
        Point2D RT = new Point2D(rect.xmax(), rect.ymax());
        TreeSet<Point2D> ySubset = (TreeSet<Point2D>) set.subSet(LB, true, RT, true);

        Bag<Point2D> bag = new Bag<>();
        double xmin = rect.xmin(), xmax = rect.xmax();
        for (Point2D p : ySubset) {
            if (p.x() < xmin || p.x() > xmax) continue;
            bag.add(p);
        }
        return bag;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        validate(p);
        Point2D nearestPoint = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (Point2D tmpP : set) {
            if (tmpP.equals(p)) return tmpP;
            double dist = tmpP.distanceSquaredTo(p);
            if (dist != 0.0 && dist < minDistance) {
                nearestPoint = tmpP;
                minDistance = dist;
            }
        }
        // StdOut.println("PointSET minD=" + minDistance);
        return nearestPoint;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
    }

    private void validate(Point2D p) {
        if (p == null) throw new IllegalArgumentException("argument p is null!");
    }

    private void validate(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("argument rect is null!");
    }
}
