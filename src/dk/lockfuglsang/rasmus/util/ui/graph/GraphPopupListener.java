package dk.lockfuglsang.rasmus.util.ui.graph;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

public class GraphPopupListener implements MouseListener, KeyListener, MouseMotionListener {
  /** The popup menu to show on the table. */
  private JPopupMenu menu;
  
  private KeyStroke showPopupKeyStroke;
  
  public GraphPopupListener(JPopupMenu menu, KeyStroke showPopup) {
    this.menu = menu;
    this.showPopupKeyStroke = showPopup;
  }
  
  public void register(JComponent comp) {
    comp.addMouseListener(this);
    comp.addMouseMotionListener(this);
    comp.addKeyListener(this);
  }
  public void unregister(JComponent comp) {
    comp.removeMouseListener(this);
    comp.removeMouseMotionListener(this);
    comp.removeKeyListener(this);
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
      menu.show(comp, e.getX(), e.getY());
    }
  }

  @Override
  public void keyPressed(KeyEvent e) {
    Component comp = e.getComponent();
    if (e.getKeyCode() == showPopupKeyStroke.getKeyCode()) {
      Dimension p = comp.getSize();
      p.width /= 2;
      p.height /= 2;
      e.consume();
      menu.show(comp, p.width, p.height);
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void mouseDragged(MouseEvent e) {
  }

  @Override
  public void mouseMoved(MouseEvent e) {
  }
}
