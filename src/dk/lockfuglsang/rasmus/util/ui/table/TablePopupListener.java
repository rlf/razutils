/*
 * TablePopupListener.java
 *
 * Created on 11. juli 2007, 20:06
 *
 */
package dk.lockfuglsang.rasmus.util.ui.table;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;

/**
 * The TablePopupListener is used to react on mouse events on a table. If the
 * <code>mousePressed</code> or <code>mouseReleased</code> is called with
 * the <code>isPopupTrigger()</code> returning <code>true</code> the row in
 * the table is selected (can later be retrieved by calling
 * <code>getSelectedRow</code>) and the popup menu is shown.
 *
 * @version 1.0
 */
public class TablePopupListener implements MouseListener, KeyListener {
  /** The popup menu to show on the table. */
  private JPopupMenu menu;
  
  private KeyStroke showPopupKeyStroke;
  
  /** The currently selected row in the table. */
  private int rowIndex;
  private int colIndex;
  
  public TablePopupListener(JPopupMenu menu, KeyStroke showPopup) {
    this.menu = menu;
    rowIndex = -1;
    this.showPopupKeyStroke = showPopup;
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
      if (comp instanceof JTable) {
        JTable table = (JTable)comp;
        rowIndex = table.rowAtPoint(e.getPoint());
        colIndex = table.columnAtPoint(e.getPoint());
        // And select it...
        table.getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
      }
      menu.show(comp, e.getX(), e.getY());
    }
  }
  /** Returns the selected row index of the table. */
  public int getSelectedRow() {
    return rowIndex;
  }
  public int getSelectedColumn() {
    return colIndex;
  }

  @Override
  public void keyPressed(KeyEvent e) {
    Component comp = e.getComponent();
    if (comp instanceof JTable) {
      JTable table = (JTable) comp;
      rowIndex = table.getSelectedRow();
      colIndex = table.getSelectedColumn();
      if (e.getKeyCode() == showPopupKeyStroke.getKeyCode()) {
        e.consume();
        Rectangle r = table.getCellRect(rowIndex, colIndex, true);
        menu.show(comp, r.x, r.y);
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void keyTyped(KeyEvent e) {
    // TODO Auto-generated method stub
    
  }
}
