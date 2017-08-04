package trianglesolver.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import trianglesolver.gui.StatusUpdater;

public class TSFigure {

    private final List<TSVertex> vertices;
    private final List<TSSegment> basicSegments;
    private final List<TSSegment> extendedSegments;
    private boolean locked;
    private final double tolerance;
    private Thread worker = null;
    private StatusUpdater updater;

    public TSFigure() {
        this(0.005, null);
    }

    public TSFigure(double tolerance, StatusUpdater updater) {
        vertices = new ArrayList<>();
        basicSegments = new ArrayList<>();
        extendedSegments = new ArrayList<>();
        locked = false;
        this.tolerance = tolerance;
        this.updater = updater;
    }

    synchronized public void addSegment(TSSegment segment) {
        if (locked) {
            throw new IllegalStateException("Figure is locked");
        }
        else {
            TSVertex A = segment.getVertexA();
            TSVertex B = segment.getVertexB();
            boolean addA = true;
            boolean addB = true;
            for (TSVertex v : vertices) {
                if (v == A || v.isCloseEnough(A, tolerance)) {
                    addA = false;
                }
                if (v == B || v.isCloseEnough(B, tolerance)) {
                    addB = false;
                }
            }
            if (addA) {
                vertices.add(A);
            }
            if (addB) {
                vertices.add(B);
            }
            for (TSSegment s : basicSegments) {
                if ((s.getVertexA() == A || s.getVertexA() == B) && (s.getVertexB() == A || s.getVertexB() == B)) {
                    return; //can't add second segment for the same pair of vertices.
                }
            }
            basicSegments.add(segment);
        }
    }

    synchronized public boolean isLocked() {
        return locked;
    }

    synchronized public void lock() {
        locked = true;
        extendedSegments.clear();
        fillExtendedSegments();
    }

    synchronized public void unlock() {
        locked = false;
        extendedSegments.clear();
    }

    synchronized public List<TSVertex> getVertices() {
        return vertices;
    }

    synchronized public List<TSSegment> getBasicSegments() {
        return basicSegments;
    }

    synchronized public List<TSSegment> getExtendedSegments() {
        return extendedSegments;
    }

    synchronized public void setUpdater(StatusUpdater updater) {
        this.updater = updater;
    }

    synchronized public void exportToFile(String path) throws IOException {
        if (!path.endsWith(".ts")) {
            path += ".ts";
        }
        File file = new File(path);
        FileOutputStream out = new FileOutputStream(file);
        byte n1[] = ByteBuffer.allocate(4).putInt(vertices.size()).array();
        out.write(n1);
        for (TSVertex v : vertices) {
            byte s[] = ByteBuffer.allocate(16).putDouble(v.getX()).putDouble(v.getY()).array();
            out.write(s);
        }
        byte n2[] = ByteBuffer.allocate(4).putInt(basicSegments.size()).array();
        out.write(n2);
        for (TSSegment s : basicSegments) {
            int A = vertices.indexOf(s.getVertexA());
            int B = vertices.indexOf(s.getVertexB());
            byte i[] = ByteBuffer.allocate(8).putInt(A).putInt(B).array();
            out.write(i);
        }
    }

    synchronized public void importFromfile(String path) throws FileNotFoundException, IOException {
        vertices.clear();
        basicSegments.clear();
        extendedSegments.clear();

        File file = new File(path);
        FileInputStream in = new FileInputStream(file);
        byte n1[] = new byte[4];
        in.read(n1);
        int size1 = ByteBuffer.wrap(n1).getInt();
        for (int i = 0; i < size1; ++i) {
            byte v1[] = new byte[8];
            byte v2[] = new byte[8];
            in.read(v1);
            in.read(v2);
            double X = ByteBuffer.wrap(v1).getDouble();
            double Y = ByteBuffer.wrap(v2).getDouble();
            vertices.add(new TSVertex(X, Y));
        }
        byte n2[] = new byte[4];
        in.read(n2);
        int size2 = ByteBuffer.wrap(n2).getInt();
        for (int i = 0; i < size2; ++i) {
            byte v1[] = new byte[4];
            byte v2[] = new byte[4];
            in.read(v1);
            in.read(v2);
            int vA = ByteBuffer.wrap(v1).getInt();
            int vB = ByteBuffer.wrap(v2).getInt();
            TSVertex A = vertices.get(vA);
            TSVertex B = vertices.get(vB);
            basicSegments.add(new TSSegment(A, B));
        }
    }

    private void fillExtendedSegments() {
        worker = new Thread() {
            @Override
            public void run() {
                updater.update("Copying basic segments...", null);
                extendedSegments.addAll(basicSegments);
                Map<TSVertex, List<TSVertex>> pairs = new HashMap<>();
                for (TSVertex v : vertices) {
                    pairs.put(v, new LinkedList<>());
                }
                updater.update("Generating extended segments...", null);
                boolean added;
                do {
                    added = false;
                    List<TSSegment> buffer = new LinkedList<>();
                    for (TSSegment sA : extendedSegments) {
                        for (TSSegment sB : extendedSegments) {
                            if (sA != sB) {
                                synchronized (this) {
                                    TSSegment segment = null;
                                    TSVertex common = null;
                                    if (sA.getVertexA() == sB.getVertexA()) { //common vertex A and A
                                        segment = new TSSegment(sA.getVertexB(), sB.getVertexB());
                                        common = sA.getVertexA();
                                    }
                                    else if (sA.getVertexA() == sB.getVertexB()) { //common vertex A and B
                                        segment = new TSSegment(sA.getVertexB(), sB.getVertexA());
                                        common = sA.getVertexA();
                                    }
                                    else if (sA.getVertexB() == sB.getVertexA()) { //common vertex B and A
                                        segment = new TSSegment(sA.getVertexA(), sB.getVertexB());
                                        common = sA.getVertexB();
                                    }
                                    else if (sA.getVertexB() == sB.getVertexB()) { //common vertex B and B
                                        segment = new TSSegment(sA.getVertexA(), sB.getVertexA());
                                        common = sA.getVertexB();
                                    }
                                    else {
                                        continue;
                                    }

                                    TSVertex projection = segment.getVertexProjection(common);
                                    if (projection.isCloseEnough(common, tolerance) && segment.contains(projection)) {
                                        if (!pairs.get(segment.getVertexA()).contains(segment.getVertexB()) && !pairs.get(segment.getVertexB()).contains(segment.getVertexA())) {
                                            buffer.add(segment);
                                            pairs.get(segment.getVertexA()).add(segment.getVertexB());
                                            pairs.get(segment.getVertexB()).add(segment.getVertexA());
                                            added = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    extendedSegments.addAll(buffer);
                } while (added);
                updater.update("Solving triangles...", null);
                int n = 0;
                for (TSSegment t1 : extendedSegments) {
                    for (TSSegment t2 : extendedSegments) {
                        if (t1 != t2) {
                            TSVertex common = null;
                            TSVertex missingA = null;
                            TSVertex missingB = null;
                            if (t1.getVertexA() == t2.getVertexA()) {
                                common = t1.getVertexA();
                                missingA = t1.getVertexB();
                                missingB = t2.getVertexB();
                            }
                            else if (t1.getVertexA() == t2.getVertexB()) {
                                common = t1.getVertexA();
                                missingA = t1.getVertexB();
                                missingB = t2.getVertexA();
                            }
                            else if (t1.getVertexB() == t2.getVertexA()) {
                                common = t1.getVertexB();
                                missingA = t1.getVertexA();
                                missingB = t2.getVertexB();
                            }
                            else if (t1.getVertexB() == t2.getVertexB()) {
                                common = t1.getVertexB();
                                missingA = t1.getVertexA();
                                missingB = t2.getVertexA();
                            }
                            else {
                                continue;
                            }
                            if (common != null && missingA != null && missingB != null) {
                                for (TSSegment t3 : extendedSegments) {
                                    if ((t3.getVertexA() == missingA || t3.getVertexA() == missingB) && (t3.getVertexB() == missingA || t3.getVertexB() == missingB)) {
                                        TSVertex projectionC = t3.getVertexProjection(common);
                                        if (projectionC.isCloseEnough(common, tolerance) && t3.contains(projectionC)) {
                                            continue;
                                        }
                                        TSVertex projectionA = t2.getVertexProjection(missingA);
                                        if (projectionA.isCloseEnough(missingA, tolerance) && t2.contains(projectionA)) {
                                            continue;
                                        }
                                        TSVertex projectionB = t1.getVertexProjection(missingB);
                                        if (projectionB.isCloseEnough(missingB, tolerance) && t1.contains(projectionB)) {
                                            continue;
                                        }
                                        n++;
                                        double result = n;
                                        result /= 6;
                                        result += (1 / 6);
                                        TSTriangle tr = new TSTriangle(common, missingA, missingB);
                                        updater.update("Solving triangles... (found: " + (int) result + ")", tr);
                                    }
                                }
                            }
                        }
                    }
                }
                updater.update("Completed - total found: " + n / 6 + ")", null);
                updater.finish();
            }
        };
        worker.start();
    }
}
