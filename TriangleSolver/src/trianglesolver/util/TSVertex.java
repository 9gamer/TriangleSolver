package trianglesolver.util;

public class TSVertex {

    private double X;
    private double Y;

    public TSVertex(double x, double y) {
        X = x;
        Y = y;
    }

    public TSVertex(TSVertex v) {
        X = v.X;
        Y = v.Y;
    }

    public void setX(double x) {
        X = x;
    }

    public void setY(double y) {
        Y = y;
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    public boolean isCloseEnough(TSVertex vertex, double distance) {
        double dx = vertex.X - X;
        double dy = vertex.Y - Y;
        return dx * dx + dy * dy <= distance * distance;
    }

    public double getDistance(TSVertex v) {
        double dx = v.X - X;
        double dy = v.Y - Y;
        return dx * dx + dy * dy;
    }

    private TSVertex() {
        //disabled, because there is no default X and Y.
    }
}
