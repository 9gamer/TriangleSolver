package trianglesolver.gui;

import java.awt.BasicStroke;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import javax.swing.JComponent;
import trianglesolver.util.TSFigure;
import trianglesolver.util.TSSegment;
import trianglesolver.util.TSTriangle;

public class TSSolverBoard extends JComponent {

    private Graphics2D graphics2D;
    private boolean showSolved;
    private int n;
    private final TSFigure figure;
    private final List<TSTriangle> list;

    public TSSolverBoard(TSFigure f, List<TSTriangle> l) {
        figure = f;
        list = l;
        n = -1;
    }

    private TSSolverBoard() {
        figure = null;
        list = null;
    }

    public void setSolutionVisibility(boolean b) {
        showSolved = b;
        if (!showSolved) {
            n = -1;
        }
        repaint();
    }

    public void prevSolution() {
        if (showSolved) {
            n--;
            if (n < 0) {
                n = list.size() - 1;
            }
            repaint();
        }
    }

    public void nextSolution() {
        if (showSolved) {
            n++;
            if (n >= list.size()) {
                n = 0;
            }
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics2D = (Graphics2D) graphics;
        clear();
        drawFigure();
        if (showSolved) {
            drawSolution();
        }
    }

    private void clear() {
        graphics2D.setPaint(Color.WHITE);
        graphics2D.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawFigure() {
        if (figure != null) {
            graphics2D.setPaint(new Color(40, 40, 40, 40));
            graphics2D.setStroke(new BasicStroke(5, CAP_ROUND, JOIN_ROUND));
            for (TSSegment s : figure.getBasicSegments()) {
                int Ax = (int) (s.getVertexA().getX() + 0.5);
                int Ay = (int) (s.getVertexA().getY() + 0.5);
                int Bx = (int) (s.getVertexB().getX() + 0.5);
                int By = (int) (s.getVertexB().getY() + 0.5);
                graphics2D.drawLine(Ax, Ay, Bx, By);
            }
        }
    }

    private void drawSolution() {
        if (list != null && n >= 0 && n < list.size()) {
            graphics2D.setPaint(new Color(181, 230, 29));
            graphics2D.setStroke(new BasicStroke(2, CAP_ROUND, JOIN_ROUND));
            TSTriangle t = list.get(n);
            int Ax = (int) (t.getVertexA().getX() + 0.5);
            int Ay = (int) (t.getVertexA().getY() + 0.5);
            int Bx = (int) (t.getVertexB().getX() + 0.5);
            int By = (int) (t.getVertexB().getY() + 0.5);
            int Cx = (int) (t.getVertexC().getX() + 0.5);
            int Cy = (int) (t.getVertexC().getY() + 0.5);
            graphics2D.drawLine(Ax, Ay, Bx, By);
            graphics2D.drawLine(Bx, By, Cx, Cy);
            graphics2D.drawLine(Cx, Cy, Ax, Ay);
        }
    }
}
