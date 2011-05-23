/*
 * UIHelper.java
 *
 * Created on 11. juli 2007, 22:44
 *
 */

package dk.lockfuglsang.rasmus.util.ui;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class UIHelper {
  static private LookAndFeel laf;
  /** Creates a new instance of UIHelper */
  private UIHelper() {
    // Static access only
  }
  /** Returns the Look And Feel of the system. */
  static public LookAndFeel getLookAndFeel() {
    if (laf == null) {
      String clName = UIManager.getSystemLookAndFeelClassName();
      try {
        // Try to set the system look and feel
        UIManager.setLookAndFeel(clName);
      } catch (Exception x) {
        // Catch multiple exception type.. cause we don't really care
      }
      laf = UIManager.getLookAndFeel();
    }
    return laf;
  }
}
