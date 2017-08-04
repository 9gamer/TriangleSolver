package trianglesolver.gui;

import trianglesolver.util.TSTriangle;

public interface StatusUpdater {

    public void update(String msg, TSTriangle triangle);

    public void finish();
}
