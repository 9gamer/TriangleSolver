package trianglesolver.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.*;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import trianglesolver.util.TSFigure;
import trianglesolver.util.TSMode;

public class TSEditorWindow extends JFrame {

    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu menuFile = new JMenu("File");
    private final JMenuItem mfNew = new JMenuItem("New");
    private final JMenuItem mfLoad = new JMenuItem("Load");
    private final JMenuItem mfSave = new JMenuItem("Save");
    private final JMenu menuSolver = new JMenu("Solver");
    private final JMenuItem msSolve = new JMenuItem("Solve");
    private final ButtonGroup rbGroup = new ButtonGroup();
    private final JRadioButton rbDraw = new JRadioButton("Draw");
    private final JRadioButton rbMove = new JRadioButton("Move");
    private final JRadioButton rbDelete = new JRadioButton("Delete");
    private final TSEditorBoard board = new TSEditorBoard();
    private final JCheckBox cbShowVertices = new JCheckBox("Show vertices");

    private TSFigure figure;

    public TSEditorWindow() {
        super("TraingleSolver - editor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setSize(500, 500);
        setMinimumSize(new Dimension(400, 300));
        setVisible(true);

        initComponents();
        initLayout();
        initListeners();

        rbMove.setEnabled(false); //TEMP: lock unnecessary feature
        rbDelete.setEnabled(false); //TEMP: lock unnecessary feature
    }

    private void initComponents() {
        add(menuBar);
        menuBar.add(menuFile);
        menuFile.add(mfNew);
        menuFile.add(mfLoad);
        menuFile.add(mfSave);
        menuBar.add(menuSolver);
        menuSolver.add(msSolve);

        rbGroup.add(rbDraw);
        rbGroup.add(rbMove);
        rbGroup.add(rbDelete);
        add(rbDraw);
        add(rbMove);
        add(rbDelete);
        rbDraw.setSelected(true);

        add(board);
    }

    private void initLayout() {
        GroupLayout layout = new GroupLayout(getContentPane());
        setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(rbDraw)
                                        .addComponent(rbMove)
                                        .addComponent(rbDelete))
                                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                .addComponent(menuBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(board, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(cbShowVertices, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                )
                        )
                )
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(menuBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(rbDraw)
                                .addComponent(rbMove)
                                .addComponent(rbDelete))
                        .addComponent(board, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbShowVertices, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                )
        );
    }

    private void initListeners() {
        menuFile.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent me) {
                if (figure == null) {
                    mfSave.setEnabled(false);
                }
                else {
                    mfSave.setEnabled(true);
                }
                repaint();
            }

            @Override
            public void menuDeselected(MenuEvent me) {
                repaint();
            }

            @Override
            public void menuCanceled(MenuEvent me) {
                repaint();
            }
        });
        mfNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent action) {
                if (figure == null) {
                    figure = new TSFigure();
                }
                else {
                    int dialogOption = JOptionPane.showConfirmDialog(mfNew.getRootPane(), "Do you want to save current figure?", "Confirm", YES_NO_CANCEL_OPTION);
                    switch (dialogOption) {
                        case YES_OPTION:
                            JFileChooser save = new JFileChooser();
                            save.setFileFilter(new FileNameExtensionFilter("TrangleSolver", "ts"));
                            if (save.showSaveDialog(mfSave.getRootPane()) == JFileChooser.APPROVE_OPTION) {
                                try {
                                    figure.exportToFile(save.getSelectedFile().getPath());
                                    repaint();
                                } catch (IOException ex) {
                                    Logger.getLogger(TSEditorWindow.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            figure = new TSFigure();
                            board.setFigure(figure);
                            break;
                        case NO_OPTION:
                            figure = new TSFigure();
                            board.setFigure(figure);
                            break;
                        case CANCEL_OPTION:
                            break;
                        default:
                            break;
                    }
                }
                board.setFigure(figure);
                repaint();
            }
        });
        mfLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent action) {
                if (figure == null) {
                    figure = new TSFigure();
                    JFileChooser load = new JFileChooser();
                    load.setFileFilter(new FileNameExtensionFilter("TrangleSolver", "ts"));
                    if (load.showOpenDialog(mfLoad.getRootPane()) == JFileChooser.APPROVE_OPTION) {
                        try {
                            figure.importFromfile(load.getSelectedFile().getPath());
                            repaint();
                        } catch (IOException ex) {
                            Logger.getLogger(TSEditorWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                else {
                    int dialogOption = JOptionPane.showConfirmDialog(mfNew.getRootPane(), "Do you want to save current figure?", "Confirm", YES_NO_CANCEL_OPTION);
                    switch (dialogOption) {
                        case YES_OPTION:
                            JFileChooser save = new JFileChooser();
                            save.setFileFilter(new FileNameExtensionFilter("TrangleSolver", "ts"));
                            if (save.showSaveDialog(mfSave.getRootPane()) == JFileChooser.APPROVE_OPTION) {
                                try {
                                    figure.exportToFile(save.getSelectedFile().getPath());
                                    repaint();
                                } catch (IOException ex) {
                                    Logger.getLogger(TSEditorWindow.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            else {
                                break;
                            }
                        case NO_OPTION:
                            JFileChooser load = new JFileChooser();
                            load.setFileFilter(new FileNameExtensionFilter("TrangleSolver", "ts"));
                            if (load.showOpenDialog(mfLoad.getRootPane()) == JFileChooser.APPROVE_OPTION) {
                                try {
                                    figure.importFromfile(load.getSelectedFile().getPath());
                                    repaint();
                                } catch (IOException ex) {
                                    Logger.getLogger(TSEditorWindow.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            break;
                        case CANCEL_OPTION:
                            return;
                        default:
                            return;
                    }
                }
                board.setFigure(figure);
            }
        });
        mfSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent action) {
                JFileChooser save = new JFileChooser();
                save.setFileFilter(new FileNameExtensionFilter("TrangleSolver", "ts"));
                if (save.showSaveDialog(mfSave.getRootPane()) == JFileChooser.APPROVE_OPTION) {
                    try {
                        figure.exportToFile(save.getSelectedFile().getPath());
                        repaint();
                    } catch (IOException ex) {
                        Logger.getLogger(TSEditorWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        msSolve.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent action) {
                new TSSolverWindow(figure);
            }
        });

        rbDraw.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent change) {
                if (rbDraw.isSelected()) {
                    board.setMode(TSMode.TS_DRAW);
                }
            }
        });
        rbMove.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent change) {
                if (rbMove.isSelected()) {
                    board.setMode(TSMode.TS_MOVE);
                }
            }
        });
        rbDelete.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent change) {
                if (rbDelete.isSelected()) {
                    board.setMode(TSMode.TS_DElETE);
                }
            }
        });

        cbShowVertices.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent action) {
                board.setVerticesVisibility(cbShowVertices.isSelected());
                repaint();
            }
        });
    }
}
