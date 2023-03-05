import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class KdTree {
    private static final boolean H = true;
    private static final boolean V = false;
    private Node root;
    private int size;

    private static class Node {
        private Point2D point; // the point
        private RectHV rect;   // the axis-aligned rectangle corresponding to this node
        private Node lb, rt;   // the left/bottom, right/top subtree

        public Node(Point2D point) {
            this.point = point;
        }
    }

    // construct an empty set of points
    public KdTree() {
    }

    // is the set empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        validate(p);
        root = put(root, new Node(p), 0.0, 1.0, 0.0, 1.0, H);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        validate(p);
        return get(root, new Node(p), H) != null;
    }

    // draw all points to standard draw
    public void draw() {
        draw(root, H);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        validate(rect);
        Bag<Point2D> bag = new Bag<>();
        range(root, rect, bag);
        return bag;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        validate(p);
        return nearest(root, p, Double.POSITIVE_INFINITY, H);
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        KdTree kdtree = new KdTree();
        for (int i = 0; i < n; i++) {
            double x = StdRandom.uniformDouble(0.0, 1.0);
            double y = StdRandom.uniformDouble(0.0, 1.0);
            kdtree.insert(new Point2D(x, y));
            StdOut.printf("%8.6f %8.6f\n", x, y);
        }
        StdOut.println("kdtree size: " + kdtree.size());

        StdDraw.enableDoubleBuffering();
        StdDraw.clear();
        kdtree.draw();
        StdDraw.show();
    }

    private void validate(Point2D p) {
        if (p == null) throw new IllegalArgumentException("argument p is null!");
    }

    private void validate(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("argument rect is null!");
    }

    private int compareTo(Node a, Node b, boolean partition) {
        if (partition == H) return Double.compare(a.point.x(), b.point.x());
        return Double.compare(a.point.y(), b.point.y());
    }

    // insert a node into tree
    private Node put(Node x, Node newN, double borderLo, double borderHi,
                     double parentBorderLo, double parentBorderHi, boolean partition) {
        if (x == null) {
            size++;
            newN.rect = (partition == H) ?
                        new RectHV(parentBorderLo, borderLo, parentBorderHi, borderHi) :
                        new RectHV(borderLo, parentBorderLo, borderHi, parentBorderHi);
            return newN;
        }

        int cmp = compareTo(x, newN, partition);
        double partitionBorder = (partition == H) ? x.point.x() : x.point.y();
        partition = (partition == H) ? V : H; // next level reverse partition
        if (cmp > 0)
            x.lb = put(x.lb, newN, parentBorderLo, partitionBorder, borderLo, borderHi, partition);
        else if (cmp < 0)
            x.rt = put(x.rt, newN, partitionBorder, parentBorderHi, borderLo, borderHi, partition);
        else if (!x.point.equals(newN.point)) { // specified situation
            x.rt = put(x.rt, newN, partitionBorder, parentBorderHi, borderLo, borderHi, partition);
        }
        return x;
    }

    // get the target node
    private Node get(Node x, Node target, boolean partition) {
        if (x == null) return null;

        int cmp = compareTo(x, target, partition);
        partition = (partition == H) ? V : H; // next level reverse partition
        if (cmp > 0) return get(x.lb, target, partition);
        else if (cmp < 0) return get(x.rt, target, partition);
        else {
            if (x.point.equals(target.point)) return x;
            return get(x.rt, target, partition);
        }
    }

    // draw all the points in kd-tree to standard draw
    private void draw(Node x, boolean partition) {
        if (x == null) return;
        drawSingleNode(x, partition);
        partition = (partition == H) ? V : H; // next level reverse partition
        draw(x.lb, partition);
        draw(x.rt, partition);
    }

    // draw a single point
    private void drawSingleNode(Node x, boolean partition) {
        Point2D start, end;
        if (partition == H) {
            start = new Point2D(x.point.x(), x.rect.ymin());
            end = new Point2D(x.point.x(), x.rect.ymax());
            StdDraw.setPenColor(StdDraw.RED);
        }
        else {
            start = new Point2D(x.rect.xmin(), x.point.y());
            end = new Point2D(x.rect.xmax(), x.point.y());
            StdDraw.setPenColor(StdDraw.BLUE);
        }
        start.drawTo(end);
        StdDraw.setPenColor();

        StdDraw.setPenRadius(0.01);
        x.point.draw();
        StdDraw.setPenRadius();
    }

    // search all the points query rect contains
    private void range(Node x, RectHV rect, Bag<Point2D> bag) {
        if (x == null || !x.rect.intersects(rect)) return;

        if (rect.contains(x.point)) bag.add(x.point);
        range(x.lb, rect, bag);
        range(x.rt, rect, bag);
    }

    // search the closest point with the query point
    private Point2D nearest(Node x, Point2D queryP, double minDistance, boolean partition) {
        if (x == null) return null;
        if (x.rect.distanceSquaredTo(queryP) > minDistance) return null;

        Point2D nearestP = null;
        if (findNewClosestPoint(x.point, queryP, minDistance)) {
            nearestP = x.point;
            minDistance = x.point.distanceSquaredTo(queryP);
        }

        int cmp = (partition == H) ? Point2D.X_ORDER.compare(queryP, x.point) :
                  Point2D.Y_ORDER.compare(queryP, x.point);
        partition = (partition == H) ? V : H; // next level reverse partition
        if (cmp < 0) {
            Point2D leftNP = nearest(x.lb, queryP, minDistance, partition);
            if (findNewClosestPoint(leftNP, queryP, minDistance)) {
                nearestP = leftNP;
                minDistance = leftNP.distanceSquaredTo(queryP);
            }

            Point2D rightNP = nearest(x.rt, queryP, minDistance, partition);
            if (findNewClosestPoint(rightNP, queryP, minDistance)) nearestP = rightNP;
        }
        else {
            Point2D rightNP = nearest(x.rt, queryP, minDistance, partition);
            if (findNewClosestPoint(rightNP, queryP, minDistance)) {
                nearestP = rightNP;
                minDistance = rightNP.distanceSquaredTo(queryP);
            }

            Point2D leftNP = nearest(x.lb, queryP, minDistance, partition);
            if (findNewClosestPoint(leftNP, queryP, minDistance)) nearestP = leftNP;
        }

        return nearestP;
    }

    // check if this point is a new closest point to the query point
    private boolean findNewClosestPoint(Point2D p, Point2D queryPoint, double minDistance) {
        return (p != null) && (p.distanceSquaredTo(queryPoint) < minDistance);
    }
}
