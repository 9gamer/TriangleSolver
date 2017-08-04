package trianglesolver.gui;

import java.awt.BasicStroke;
import static java.awt.BasicStroke.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import static java.awt.event.MouseEvent.*;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JComponent;
import trianglesolver.util.TSFigure;
import trianglesolver.util.TSSegment;
import trianglesolver.util.TSMode;
import trianglesolver.util.TSVertex;

public class TSEditorBoard extends JComponent {

    private Graphics2D graphics2D;
    private TSMode mode;
    private boolean showVertices;
    private TSFigure figure;

    private boolean MA_active;
    private TSVertex MA_Start;
    private TSVertex MA_Current;

    private final double DRAW_TOLERANCE = 12;
    private final double DRAW_VERTEX_FIELD = DRAW_TOLERANCE * DRAW_TOLERANCE;

    public TSEditorBoard() {
        mode = TSMode.TS_DRAW;
        showVertices = false;
        figure = null;

        MA_active = false;
        MA_Start = null;
        MA_Current = null;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getButton() == BUTTON3) {
                    MA_active = false;
                    MA_Start = null;
                    MA_Current = null;
                }
                if (event.getButton() == BUTTON1) {
                    if (mode == TSMode.TS_DRAW) { //register clicking only for drawing
                        if (!MA_active) { //start drawing new segment
                            MA_Start = new TSVertex(event.getX(), event.getY());
                            if (figure != null) {
                                double distV = Double.MAX_VALUE;
                                double distS = Double.MAX_VALUE;
                                TSVertex vertexV = null;
                                TSVertex vertexS = null;
                                for (TSSegment s : figure.getBasicSegments()) { //find the closest vertex to current
                                    double dA = MA_Start.getDistance(s.getVertexA());
                                    double dB = MA_Start.getDistance(s.getVertexB());
                                    double dS = s.getDistance(MA_Start);
                                    if (dA < distV) { //dA
                                        distV = dA;
                                        vertexV = s.getVertexA();
                                    }
                                    if (dB < distV) { //dB
                                        distV = dB;
                                        vertexV = s.getVertexB();
                                    }
                                    if (dS < distS) { //dS
                                        TSVertex vProj = s.getVertexProjection(MA_Start);
                                        if (s.contains(vProj)) {
                                            distS = dS;
                                            vertexS = vProj;
                                        }
                                    }
                                }
                                if (distV < DRAW_VERTEX_FIELD) {
                                    MA_Start = vertexV;
                                }
                                else {
                                    if (distV < DRAW_TOLERANCE * DRAW_TOLERANCE) {
                                        MA_Start = vertexV;
                                    }
                                    if (distS < DRAW_TOLERANCE * DRAW_TOLERANCE && distS < distV) {
                                        MA_Start = vertexS;
                                    }
                                }
                            }
                            MA_Current = new TSVertex(event.getX(), event.getY());
                        }
                        else { //stop drawing new segment
                            if (figure != null) {
                                double distV = Double.MAX_VALUE;
                                double distS = Double.MAX_VALUE;
                                TSVertex vertexV = null;
                                TSVertex vertexS = null;
                                for (TSSegment s : figure.getBasicSegments()) { //find the closest vertex to current
                                    double dA = MA_Current.getDistance(s.getVertexA());
                                    double dB = MA_Current.getDistance(s.getVertexB());
                                    double dS = s.getDistance(MA_Current);
                                    if (dA < distV) { //dA
                                        distV = dA;
                                        vertexV = s.getVertexA();
                                    }
                                    if (dB < distV) { //dB
                                        distV = dB;
                                        vertexV = s.getVertexB();
                                    }
                                    if (dS < distS) { //dS
                                        TSVertex vProj = s.getVertexProjection(MA_Current);
                                        if (s.contains(vProj)) {
                                            distS = dS;
                                            vertexS = vProj;
                                        }
                                    }
                                }
                                if (distV < DRAW_VERTEX_FIELD) {
                                    MA_Current = vertexV;
                                }
                                else {
                                    if (distV < DRAW_TOLERANCE * DRAW_TOLERANCE) {
                                        MA_Current = vertexV;
                                    }
                                    if (distS < DRAW_TOLERANCE * DRAW_TOLERANCE && distS < distV) {
                                        MA_Current = vertexS;
                                    }
                                }
                                insertNewSegment(MA_Start, MA_Current);
                            }
                            MA_Start = null;
                            MA_Current = null;
                        }
                        MA_active = !MA_active;
                    }
                }
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                if ((MA_active && mode == TSMode.TS_DRAW)) { //refresh only when in drawing mode
                    MA_Current.setX(event.getX());
                    MA_Current.setY(event.getY());
                    repaint();
                }
                if (MA_active && mode == TSMode.TS_MOVE) { //refresh only when in move mode
                    MA_Current.setX(event.getX());
                    MA_Current.setY(event.getY());
                    repaint();
                }
            }
        });
    }

    public void setMode(TSMode m) {
        mode = m;
    }

    public void setVerticesVisibility(boolean b) {
        showVertices = b;
    }

    public void setFigure(TSFigure f) {
        MA_active = false;
        MA_Start = null;
        MA_Current = null;
        figure = f;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics2D = (Graphics2D) graphics;
        clear();
        drawFigure();
        drawSegment();
    }

    private void clear() {
        if (figure == null) { //procedural drawing checkboard pattern
            int cX = getWidth() / 10 + 1;
            int cY = getHeight() / 10 + 1;
            for (int y = 0; y < cY; ++y) {
                for (int x = 0; x < cX; ++x) {
                    if (y % 2 != x % 2) {
                        graphics2D.setPaint(new Color(195, 195, 195));
                    }
                    else {
                        graphics2D.setPaint(new Color(127, 127, 127));
                    }
                    graphics2D.fillRect(x * 10, y * 10, 10, 10);
                }
            }
        }
        else {
            graphics2D.setPaint(Color.WHITE);
            graphics2D.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void drawFigure() {
        if (figure != null) {
            graphics2D.setStroke(new BasicStroke(3, CAP_ROUND, JOIN_ROUND));
            for (TSSegment s : figure.getBasicSegments()) {
                graphics2D.setPaint(new Color(72, 72, 72, 72));
                int Ax = (int) (s.getVertexA().getX() + 0.5);
                int Ay = (int) (s.getVertexA().getY() + 0.5);
                int Bx = (int) (s.getVertexB().getX() + 0.5);
                int By = (int) (s.getVertexB().getY() + 0.5);
                graphics2D.drawLine(Ax, Ay, Bx, By);
            }
            if (showVertices) { //draw vertices
                for (TSVertex v : figure.getVertices()) {
                    int x = (int) (v.getX() + 0.5);
                    int y = (int) (v.getY() + 0.5);
                    graphics2D.setPaint(new Color(255, 127, 39));
                    graphics2D.fillOval(x - 3, y - 3, 6, 6);
                }
            }
        }
    }

    private void drawSegment() {
        if (figure != null && !figure.isLocked()) {
            if (MA_Start != null && MA_Current != null) {
                int Ax = (int) (MA_Start.getX() + 0.5);
                int Ay = (int) (MA_Start.getY() + 0.5);
                int Bx = (int) (MA_Current.getX() + 0.5);
                int By = (int) (MA_Current.getY() + 0.5);

                graphics2D.setStroke(new BasicStroke(1, CAP_ROUND, JOIN_ROUND));
                graphics2D.setPaint(new Color(0, 162, 232));
                graphics2D.drawLine(Ax, Ay, Bx, By);
                graphics2D.setPaint(new Color(181, 230, 29));
                graphics2D.fillOval(Ax - 3, Ay - 3, 6, 6);
                graphics2D.fillOval(Bx - 3, By - 3, 6, 6);
            }
        }
    }

    private void insertNewSegment(TSVertex a, TSVertex b) {
        if (a.isCloseEnough(b, 0)) { //both vertices are in the same place
            return;
        }
        for (TSSegment s : figure.getBasicSegments()) {
            boolean isA = false;
            boolean isB = false;
            if (a.isCloseEnough(s.getVertexA(), 0) || a.isCloseEnough(s.getVertexB(), 0)) {
                isA = true;
            }
            if (b.isCloseEnough(s.getVertexA(), 0) || b.isCloseEnough(s.getVertexB(), 0)) {
                isB = true;
            }
            if (isA && isB) { //redundant segment, do nothing
                return;
            }
            if (s.contains(a) && s.contains(b)) {
                return;
            }
        }
        TSSegment newForA = null;
        TSSegment newForB = null;
        for (TSSegment s : figure.getBasicSegments()) { //solve vertex binding to segment
            if (s.contains(a) && a != s.getVertexA() && a != s.getVertexB()) {
                TSVertex segmentB = s.getVertexB();
                s.setVertexB(a);
                newForA = new TSSegment(a, segmentB);
                TSSegment curr = new TSSegment(a, b);
                if (curr.contains(s.getVertexA())) {
                    a = s.getVertexA();
                }
                if (curr.contains(segmentB)) {
                    a = segmentB;
                }
            }
            if (s.contains(b) && b != s.getVertexA() && b != s.getVertexB()) {
                TSVertex segmentB = s.getVertexB();
                s.setVertexB(b);
                newForB = new TSSegment(b, segmentB);
                TSSegment curr = new TSSegment(a, b);
                if (curr.contains(s.getVertexA())) {
                    b = s.getVertexA();
                }
                if (curr.contains(segmentB)) {
                    b = segmentB;
                }
            }
        }
        if (newForA != null) {
            figure.addSegment(newForA);
        }
        if (newForB != null) {
            figure.addSegment(newForB);
        }
        //solve overlapping segments (direction of solving overlaping is from point A to point B of new created segment)
        List<TSSegment> toAdd = new ArrayList<>();
        double originalA = (b.getY() - a.getY()) / (b.getX() - a.getX());
        double originalB = a.getY() - originalA * a.getX();
        boolean overrlapingOccured;
        do {
            if (a == b || a.isCloseEnough(b, 0.005)) {
                break;
            }
            overrlapingOccured = false;
            TSSegment closest = null;
            double dist = Double.MAX_VALUE;
            for (TSSegment s : figure.getBasicSegments()) { //find overlapping segment and reduce them
                double As = (s.getVertexB().getY() - s.getVertexA().getY()) / (s.getVertexB().getX() - s.getVertexA().getX());
                double Bs = s.getVertexA().getY() - As * s.getVertexA().getX();

                if (As == originalA && Bs == originalB) { //two segments on the same line (possible overlapping)                                                        
                    TSSegment originalSegment = new TSSegment(a, b);
                    double distA = a.getDistance(s.getVertexA());
                    double distB = a.getDistance(s.getVertexB());
                    if (distA == 0.0 || distB == 0.0) { //one common vertex
                        if (distA == 0.0) {
                            if (originalSegment.contains(s.getVertexB())) {
                                dist = 0;
                                closest = s;
                                break;
                            }
                            else if (s.contains(b)) {
                                return;
                            }
                            else {
                                continue;
                            }
                        }
                        if (distB == 0.0) {
                            if (originalSegment.contains(s.getVertexA())) {
                                dist = 0;
                                closest = s;
                                break;
                            }
                            else if (s.contains(b)) {
                                return;
                            }
                            else {
                                continue;
                            }
                        }
                    }
                    if (originalSegment.contains(s.getVertexA())) { //vertex A is inside of new segment
                        if (distA < dist) {
                            dist = distA;
                            closest = s;
                        }
                    }
                    if (originalSegment.contains(s.getVertexB())) { //vertex B is inside of new segment
                        if (distB < dist) {
                            dist = distB;
                            closest = s;
                        }
                    }
                }
            }
            if (closest != null) {
                overrlapingOccured = true;
                TSSegment originalSegment = new TSSegment(a, b);
                double distA = a.getDistance(closest.getVertexA());
                double distB = a.getDistance(closest.getVertexB());
                if (distA == 0.0 || distB == 0.0) { //one common vertex
                    if (distA == 0.0) {
                        a = new TSVertex(closest.getVertexB());
                    }
                    if (distB == 0.0) {
                        a = new TSVertex(closest.getVertexA());
                    }
                    continue;
                }
                if (originalSegment.contains(closest.getVertexA()) && originalSegment.contains(closest.getVertexB())) {
                    if (distA == dist) {
                        toAdd.add(new TSSegment(a, closest.getVertexA()));
                        a = closest.getVertexA();
                        continue;
                    }
                    else {
                        toAdd.add(new TSSegment(a, closest.getVertexB()));
                        a = closest.getVertexB();
                        continue;
                    }
                }
                if (originalSegment.contains(closest.getVertexA())) {
                    if (!closest.contains(a)) {
                        toAdd.add(new TSSegment(a, closest.getVertexA()));
                    }
                    a = closest.getVertexA();
                    continue;
                }
                if (originalSegment.contains(closest.getVertexB())) {
                    if (!closest.contains(a)) {
                        toAdd.add(new TSSegment(a, closest.getVertexB()));
                    }
                    a = closest.getVertexB();
                    continue;
                }
            }
            else {
                toAdd.add(new TSSegment(a, b));
            }
        } while (overrlapingOccured);

        //solve interestion of segments
        for (TSSegment next : toAdd) {
            List<TSIntersectingSegments> intersections = new ArrayList<>();
            for (TSSegment s : figure.getBasicSegments()) { //find intersecting segments
                if (next.getVertexA() != s.getVertexA() && next.getVertexB() != s.getVertexB() && next.getVertexA() != s.getVertexB() && next.getVertexB() != s.getVertexA()) {
                    TSVertex v = next.getIntersection(s);
                    if (v != null) {
                        double dx = next.getVertexA().getX() - v.getX();
                        double dy = next.getVertexA().getY() - v.getY();
                        double distance = dx * dx + dy * dy;
                        intersections.add(new TSIntersectingSegments(s, v, distance));
                    }
                }
            }
            if (!intersections.isEmpty()) { //resolve intersecting
                intersections.sort(new Comparator<TSIntersectingSegments>() {
                    @Override
                    public int compare(TSIntersectingSegments t1, TSIntersectingSegments t2) {
                        if (t1.distance > t2.distance) {
                            return 1;
                        }
                        if (t1.distance < t2.distance) {
                            return -1;
                        }
                        return 0;
                    }
                });
                for (TSIntersectingSegments tsis : intersections) {
                    TSVertex cross = tsis.intersection;
                    TSVertex segmentB = tsis.segment.getVertexB();
                    tsis.segment.setVertexB(cross);
                    figure.addSegment(new TSSegment(cross, segmentB));
                    figure.addSegment(new TSSegment(next.getVertexA(), cross));
                    next.setVertexA(cross);
                }
            }
            figure.addSegment(next);
        }
    }
}

class TSIntersectingSegments {

    public TSSegment segment;
    public TSVertex intersection;
    public double distance;

    public TSIntersectingSegments(TSSegment s, TSVertex v, double d) {
        segment = s;
        intersection = v;
        distance = d;
    }

    private TSIntersectingSegments() {

    }
}
