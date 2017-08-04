package trianglesolver.gui;

import java.awt.BasicStroke;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle;
import trianglesolver.util.TSFigure;
import trianglesolver.util.TSSegment;
import trianglesolver.util.TSTriangle;
import trianglesolver.util.TSVertex;

public class TSSolverWindow extends JFrame implements StatusUpdater {

    private final JButton next = new JButton("Next >");
    private final JButton prev = new JButton("< Prev");
    private final JButton save = new JButton("Save results");
    private final TSSolverBoard board;
    private final JProgressBar progressBar = new JProgressBar();
    private final JCheckBox showSolved = new JCheckBox("Show results");
    private final JLabel label = new JLabel();

    private final TSFigure figure;
    private final List<TSTriangle> result = new LinkedList<>();

    public TSSolverWindow(TSFigure f) {
        super("TraingleSolver - solver");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(500, 500);
        setMinimumSize(new Dimension(400, 300));
        setVisible(true);

        figure = f;
        board = new TSSolverBoard(figure, result);
        figure.setUpdater(this);

        initComponents();
        initLayout();
        initListeners();

        figure.lock();
    }

    private TSSolverWindow() {
        figure = null;
        board = new TSSolverBoard(null, null);
    }

    private void initComponents() {
        add(prev);
        prev.setEnabled(false);
        add(next);
        next.setEnabled(false);
        add(save);
        save.setEnabled(false);
        add(board);
        add(progressBar);
        add(showSolved);
        add(label);
    }

    private void initLayout() {
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(board, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(prev)
                                        .addGap(8)
                                        .addComponent(next)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
                                        .addComponent(save))
                                .addComponent(progressBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(showSolved)
                                                .addComponent(label))))
                        .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(prev)
                                .addComponent(next)
                                .addComponent(save))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(board, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(showSolved)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label)
                        .addContainerGap())
        );
    }

    private void initListeners() {
        prev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent action) {
                board.prevSolution();
            }
        });
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent action) {
                board.nextSolution();
            }
        });
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent action) {
                JFileChooser save = new JFileChooser();
                save.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (save.showSaveDialog(getRootPane()) == JFileChooser.APPROVE_OPTION) {
                    File export = save.getSelectedFile();
                    if (export.exists()) {
                        int left = Integer.MAX_VALUE;
                        int right = Integer.MIN_VALUE;
                        int top = Integer.MAX_VALUE;
                        int bottom = Integer.MIN_VALUE;
                        for (TSVertex v : figure.getVertices()) {
                            int x = (int) v.getX();
                            int y = (int) v.getY();
                            top = Math.min(top, y);
                            bottom = Math.max(bottom, y);
                            left = Math.min(left, x);
                            right = Math.max(right, x);
                        }
                        int BORDER = 4;

                        int iter = 0;
                        for (TSTriangle tr : result) {
                            BufferedImage bi = new BufferedImage(right - left + BORDER * 2, bottom - top + BORDER * 2, TYPE_INT_RGB);
                            Graphics2D image = bi.createGraphics();
                            image.setPaint(new Color(180, 180, 180));
                            image.setStroke(new BasicStroke(5, CAP_ROUND, JOIN_ROUND));
                            for (TSSegment segment : figure.getBasicSegments()) {
                                int Ax = (int) segment.getVertexA().getX() - left + BORDER;
                                int Ay = (int) segment.getVertexA().getY() - top + BORDER;
                                int Bx = (int) segment.getVertexB().getX() - left + BORDER;
                                int By = (int) segment.getVertexB().getY() - top + BORDER;
                                image.drawLine(Ax, Ay, Bx, By);
                            }
                            image.setPaint(new Color(0, 162, 232, 180));
                            int trAx = (int) tr.getVertexA().getX() - left + BORDER;
                            int trAy = (int) tr.getVertexA().getY() - top + BORDER;
                            int trBx = (int) tr.getVertexB().getX() - left + BORDER;
                            int trBy = (int) tr.getVertexB().getY() - top + BORDER;
                            int trCx = (int) tr.getVertexC().getX() - left + BORDER;
                            int trCy = (int) tr.getVertexC().getY() - top + BORDER;

                            int[] X = new int[]{trAx, trBx, trCx};
                            int[] Y = new int[]{trAy, trBy, trCy};
                            image.fillPolygon(X, Y, 3);

                            try {
                                ImageIO.write(bi, "png", new File(export.getPath() + "\\" + iter++ + ".png"));
                            } catch (IOException ex) {
                            }
                        }
                    }
                }
            }
        });
        showSolved.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent action) {
                if (showSolved.isSelected()) {
                    prev.setEnabled(true);
                    next.setEnabled(true);
                    board.setSolutionVisibility(true);
                }
                else {
                    prev.setEnabled(false);
                    next.setEnabled(false);
                    board.setSolutionVisibility(false);
                }
            }
        });
    }

    @Override
    public void update(String msg, TSTriangle triangle) {
        progressBar.setIndeterminate(true);
        label.setText(msg);
        if (triangle != null) {
            boolean duplicate = false;
            for (TSTriangle tr : result) {
                if (tr != triangle) {
                    boolean haveA = tr.contains(triangle.getVertexA());
                    boolean haveB = tr.contains(triangle.getVertexB());
                    boolean haveC = tr.contains(triangle.getVertexC());
                    if (haveA && haveB && haveC) {
                        duplicate = true;
                    }
                }
            }
            if (!duplicate) {
                result.add(triangle);
            }
        }
    }

    @Override
    public void finish() {
        progressBar.setIndeterminate(false);
        save.setEnabled(true);
    }
}
