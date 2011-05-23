/*
 * Resource.java
 *
 * Created on 10. juli 2007, 22:07
 *
 */

package dk.lockfuglsang.rasmus.util.resources;

import java.awt.Color;
import java.net.URL;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * ResourceBundle convenience class.
 * Design Pattern: Adaptor
 * @author Rasmus
 * @version 1.0
 */
public class Resource {
  public static final String ACCELERATOR = "Accelerator";
  public static final String MNEMONIC = "Mnemonic";
  public static final String LABEL = "Label";
  public static final String TOOLTIP = "ToolTip";
  public static final String ICON = "Icon";
  
  protected ResourceBundle rb;
  /**
   * Creates a new instance of Resource
   */
  public Resource(String name) {
    rb = ResourceBundle.getBundle("resources." + name);
  }
  public String getString(String key, String defaultValue) {
    String s = defaultValue;
    try {
      s = rb.getString(key);
    } catch (MissingResourceException e) {
      // Do nothing
    }
    return s;
  }
  public String getString(String key) {
    return getString(key, key);
  }
  public String getString(String key, Object[] args) {
    String s = getString(key);
    MessageFormat msgFormat = new MessageFormat(s);
    return msgFormat.format(args);
  }
  public int getInt(String key, int defaultValue) {
    int result = defaultValue;
    try {
      String s = rb.getString(key);
      result = Integer.parseInt(s);
    } catch (MissingResourceException e) {
      // Ignore
    } catch (NumberFormatException e) {
      // Do nothing
    }
    return result;
  }
  public Color createColor(String key) {
    String sCol = getString(key);
    int rgb = -1;
    try {
      rgb = Integer.parseInt(sCol, 16);
    } catch (NumberFormatException e) {
      // Ignore
    }
    if (rgb == -1) {
      try {
        rgb = Integer.parseInt(sCol, 10);
      } catch (NumberFormatException e) {
        // Ignore
      }
    }
    Color col = new Color(rgb);
    return col;
  }
  public JMenuItem createJMenuItem(String key) {
    JMenuItem mi = new JMenuItem();
    prepareAbstractButton(mi, key);
    return mi;
  }
  public JMenu createJMenu(String key) {
    JMenu mi = new JMenu();
    prepareAbstractButton(mi, key);
    return mi;
  }
  public JButton createJButton(String key) {
    JButton mi = new JButton();
    prepareAbstractButton(mi, key);
    return mi;
  }
  public AbstractButton prepareToolbarButton(AbstractButton btn) {
    btn.setText("");
    
    btn.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(createColor("Toolbar.Border.Color")),
        BorderFactory.createEmptyBorder(2,2,2,2)));
    btn.setContentAreaFilled(false);
    return btn;
  }
  protected void prepareAbstractButton(AbstractButton btn, String key) {
    String s = null;
    // LABEL
    String label = null;
    try {
      label = rb.getString(key + "." + LABEL);
      btn.setText(label);
    } catch (MissingResourceException e) {
      // Ignore
    }
    
    // TOOLTTIP
    try {
      s = rb.getString(key + "." + TOOLTIP);
      btn.setToolTipText(s);
    } catch (MissingResourceException e) {
      // Ignore
    }
    
    // MNEMONIC
    try {
      s = rb.getString(key + "." + MNEMONIC);
      KeyStroke ks = KeyStroke.getKeyStroke(s);
      if (ks != null) {
        btn.setMnemonic(ks.getKeyCode());
      }
    } catch (MissingResourceException e) {
      // Set the first char as Mnemonic
      if (label != null && label.length() > 0) {
        btn.setMnemonic(label.charAt(0));
      }
    }
    
    // ICON
    try {
      s = rb.getString(key + "." + ICON);
      String imgPath = rb.getString("ImagePath");
      URL url = Thread.currentThread().getContextClassLoader().getResource(imgPath + "/" + s);
      if (url != null) {
        ImageIcon icon = new ImageIcon(url);
        if (icon != null) {
          btn.setIcon(icon);
        }
      }
    } catch (MissingResourceException e) {
      // Ignore
    }
  }
  
  public void prepareAction(Action action, String key) {
    String s = null;
    // LABEL
    s = getString(key + "." + LABEL);
    action.putValue(Action.NAME, s);
    
    // TOOLTTIP
    try {
      s = rb.getString(key + "." + TOOLTIP);
      action.putValue(Action.SHORT_DESCRIPTION, s);
    } catch (MissingResourceException e) {
      // Ignore
    }
    
    // MNEMONIC
    try {
      s = rb.getString(key + "." + MNEMONIC);
      KeyStroke ks = KeyStroke.getKeyStroke(s);
      if (ks != null) {
        action.putValue(Action.MNEMONIC_KEY, new Integer(ks.getKeyCode()));
      }
    } catch (MissingResourceException e) {
      // Ignore
    }
    
    // ACCELERATOR
    try {
      s = rb.getString(key + "." + ACCELERATOR);
      KeyStroke ks = KeyStroke.getKeyStroke(s);
      if (ks != null) {
        action.putValue(Action.ACCELERATOR_KEY, ks);
      }
    } catch (MissingResourceException e) {
      // Ignore
    }
    
    // ICON
    try {
      s = rb.getString(key + "." + ICON);
      String imgPath = rb.getString("ImagePath");
      URL url = Thread.currentThread().getContextClassLoader().getResource(imgPath + "/" + s);
      if (url != null) {
        ImageIcon icon = new ImageIcon(url);
        if (icon != null) {
          action.putValue(Action.SMALL_ICON, icon);
        }
      }
    } catch (MissingResourceException e) {
      // Ignore
    }
  }
}
