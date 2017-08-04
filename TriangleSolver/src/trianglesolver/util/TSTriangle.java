package trianglesolver.util;

public class TSTriangle {

    private TSVertex A;
    private TSVertex B;
    private TSVertex C;

    public TSTriangle(TSVertex a, TSVertex b, TSVertex c) {
        A = a;
        B = b;
        C = c;
    }

    private TSTriangle() {

    }

    public TSVertex getVertexA() {
        return A;
    }

    public TSVertex getVertexB() {
        return B;
    }

    public TSVertex getVertexC() {
        return C;
    }

    public boolean contains(TSVertex v) {
        return (A == v || B == v || C == v);
    }
}
