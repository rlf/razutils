package dk.lockfuglsang.rasmus.util.ui.menu;

import java.awt.AWTEvent;

import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class MenuItemPanel extends JMenuItem {
  
  public MenuItemPanel(JPanel panel) {
    add(panel);
  }
  @Override
  public void processEvent(AWTEvent e) {
    super.processEvent(e);
  }
}
