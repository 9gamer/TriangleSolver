package trianglesolver.util;

public class TSSegment {

    private TSVertex A;
    private TSVertex B;

    public TSSegment(TSVertex a, TSVertex b) {
        A = a;
        B = b;
    }

    private TSSegment() {
        //disabled, because segment need to have 2 points.
    }

    public void setVertexA(TSVertex a) {
        if (a == null) {
            throw new NullPointerException();
        }
        A = a;
    }

    public void setVertexB(TSVertex b) {
        if (b == null) {
            throw new NullPointerException();
        }
        B = b;
    }

    public TSVertex getVertexA() {
        return A;
    }

    public TSVertex getVertexB() {
        return B;
    }

    public boolean contains(TSVertex v) {
        if (v == A || v == B) {
            return true;
        }
        if (v.getX() == A.getX() && v.getY() == A.getY()) {
            return true;
        }
        if (v.getX() == B.getX() && v.getY() == B.getY()) {
            return true;
        }
        double a = (B.getY() - A.getY()) / (B.getX() - A.getX());
        double b = A.getY() - a * A.getX();

        if (v.getY() == a * v.getX() + b) {
            if (Math.min(A.getX(), B.getX()) <= v.getX() && v.getX() <= Math.max(A.getX(), B.getX())) {
                if (Math.min(A.getY(), B.getY()) <= v.getY() && v.getY() <= Math.max(A.getY(), B.getY())) {
                    return true;
                }
            }
        }
        return false;
    }

    public TSVertex getIntersection(TSSegment segment) {
        double x;
        double y;
        if (A.getX() == B.getX() && segment.getVertexA().getX() == segment.getVertexB().getX()) {
            return null;
        }
        else if (A.getX() == B.getX()) {
            double a1 = (segment.getVertexB().getY() - segment.getVertexA().getY()) / (segment.getVertexB().getX() - segment.getVertexA().getX());
            double b1 = segment.getVertexA().getY() - a1 * segment.getVertexA().getX();
            x = A.getX();
            y = a1 * x + b1; //intersection Y
        }
        else if (segment.getVertexA().getX() == segment.getVertexB().getX()) {
            double a1 = (B.getY() - A.getY()) / (B.getX() - A.getX());
            double b1 = A.getY() - a1 * A.getX();
            x = segment.getVertexA().getX();
            y = a1 * x + b1; //intersection Y
        }
        else {
            double a1 = (B.getY() - A.getY()) / (B.getX() - A.getX());
            double b1 = A.getY() - a1 * A.getX();
            double a2 = (segment.getVertexB().getY() - segment.getVertexA().getY()) / (segment.getVertexB().getX() - segment.getVertexA().getX());
            double b2 = segment.getVertexA().getY() - a2 * segment.getVertexA().getX();
            if (a1 != a2) { //parallel lines don't have intersection
                x = (b2 - b1) / (a1 - a2); //intersection X
                y = a1 * x + b1; //intersection Y
            }
            else {
                return null;
            }
        }

        if (Math.min(A.getX(), B.getX()) <= x && x <= Math.max(A.getX(), B.getX())) {
            if (Math.min(A.getY(), B.getY()) <= y && y <= Math.max(A.getY(), B.getY())) {
                if (Math.min(segment.getVertexA().getX(), segment.getVertexB().getX()) <= x && x <= Math.max(segment.getVertexA().getX(), segment.getVertexB().getX())) {
                    if (Math.min(segment.getVertexA().getY(), segment.getVertexB().getY()) <= y && y <= Math.max(segment.getVertexA().getY(), segment.getVertexB().getY())) {
                        return new TSVertex(x, y);
                    }
                }
            }
        }
        return null;
    }

    public TSVertex getVertexProjection(TSVertex v) {
        if (A.getX() == B.getX()) {
            return new TSVertex(A.getX(), v.getY());
        }
        if (A.getY() == B.getY()) {
            return new TSVertex(v.getX(), A.getY());
        }
        double a1 = (B.getY() - A.getY()) / (B.getX() - A.getX());
        double b1 = A.getY() - a1 * A.getX();
        double a2 = (-1 / a1);
        double b2 = v.getY() - a2 * v.getX();

        double x = (b2 - b1) / (a1 - a2);
        double y = a1 * x + b1;

        return new TSVertex(x, y);
    }

    public double getDistance(TSVertex v) {
        if (A.getX() == B.getX()) {
            return (A.getX() - v.getX()) * (A.getX() - v.getX());
        }
        if (A.getY() == B.getY()) {
            return (A.getY() - v.getY()) * (A.getY() - v.getY());
        }
        double a1 = (B.getY() - A.getY()) / (B.getX() - A.getX());
        double b1 = A.getY() - a1 * A.getX();
        double a2 = (-1 / a1);
        double b2 = v.getY() - a2 * v.getX();

        double x = (b2 - b1) / (a1 - a2);
        double y = a1 * x + b1;

        double dx = x - v.getX();
        double dy = y - v.getY();

        return dx * dx + dy * dy;
    }
}
