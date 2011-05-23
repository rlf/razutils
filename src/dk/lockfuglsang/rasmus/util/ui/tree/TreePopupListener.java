/*
 * TreePopupListener.java
 *
 * Created on 21. juli 2007, 18:33
 *
 */

package dk.lockfuglsang.rasmus.util.ui.tree;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * The TreePopupListener is used to react on mouse events on a tree. If the
 * <code>mousePressed</code> or <code>mouseReleased</code> is called with
 * the <code>isPopupTrigger()</code> returning <code>true</code> the node in
 * the tree is selected and the popup menu is shown.
 *
 * @version 1.0
 */
public class TreePopupListener implements MouseListener {
  /** The popup menu to show on the table. */
  private JPopupMenu menu;
  
  public TreePopupListener(JPopupMenu menu) {
    this.menu = menu;
  }
  
  /** Dummy method. Does nothing. */
  public void mouseClicked(MouseEvent e) {
    // Do nothing
  }
  
  /** Dummy method. Does nothing. */
  public void mouseEntered(MouseEvent e) {
    // Do nothing
  }
  
  /** Dummy method. Does nothing. */
  public void mouseExited(MouseEvent e) {
    // Do nothing
  }
  
  /**
   * Checks to see whether the popup should be shown and displays it if it
   * should.
   */
  public void mousePressed(MouseEvent e) {
    maybeShowPopup(e);
  }
  
  /**
   * Checks to see whether the popup should be shown and displays it if it
   * should.
   */
  public void mouseReleased(MouseEvent e) {
    maybeShowPopup(e);
  }
  
  private void maybeShowPopup(MouseEvent e) {
    if (e.isPopupTrigger()) {
      Component comp = e.getComponent();
      if (comp instanceof JTree) {
        JTree tree = (JTree)comp;
        TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
        tree.setSelectionPath(path);
      }
      menu.show(comp, e.getX(), e.getY());
    }
  }
}
