/*
 * FilterTablePopup.java
 *
 * Created on 12. august 2007, 12:47
 *
 */

package dk.lockfuglsang.rasmus.util.ui.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.text.DateFormatter;
import javax.swing.text.NumberFormatter;

import dk.lockfuglsang.rasmus.util.resources.Resource;
import dk.lockfuglsang.rasmus.util.ui.table.filters.ContainsFilter;
import dk.lockfuglsang.rasmus.util.ui.table.filters.DateFilter;
import dk.lockfuglsang.rasmus.util.ui.table.filters.Filter;
import dk.lockfuglsang.rasmus.util.ui.table.filters.NullFilter;
import dk.lockfuglsang.rasmus.util.ui.table.filters.RangeFilter;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class FilterTablePopup extends JPopupMenu {
  static final public String OK = "ok_btn";
  static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder();
  
  protected FilterRowTableModel model;
  protected Resource res;
  
  protected int curCol;
  
  protected Map<Integer, Popup> pops;
  
  protected JMenuItem clearMenuItem;
  protected JMenuItem nullMenuItem;
  protected JPopupMenu parentPop;
  
  /** Creates a new instance of FilterTablePopup */
  public FilterTablePopup(FilterRowTableModel fmodel, Resource resource) {
    this.model = fmodel;
    this.res = resource;
    this.parentPop = this;
  
    this.pops = new HashMap<Integer, Popup>();
    
    JMenuItem mi = res.createJMenuItem("FilterTable.Clear");
    clearMenuItem = mi;
    this.add(mi);
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        model.setFilter(curCol, null);
        clearPops();
      }
    });
    nullMenuItem = res.createJMenuItem("FilterTable.NullFilter");
    nullMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        model.setFilter(curCol, new NullFilter());
      }
    });
  }
  private JComponent createContainsPattern() {
    //
    // StringPattern
    //
    final JTextField inTf = new JTextField(res.getString("FilterTable.DefaultPattern", "PATTERN"));
    inTf.setColumns(res.getInt("FilterTable.Default.Pattern.Cols", 10));
    final ActionListener acListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Filter f = model.getFilter(curCol);
        if (f == null) {
          f = new ContainsFilter();
        }
        f.setExpression(inTf.getText());
        if (f.getExpression().equals("")) {
          f = null;
        }
        model.setFilter(curCol, f);
      }
    };
    final JPanel pane = createPanel(curCol, acListener);
    inTf.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        acListener.actionPerformed(e);
        pane.firePropertyChange(OK, false, true);
      }
    }); 
    Filter f = model.getFilter(curCol);
    if (f != null) {
      inTf.setText(f.getExpression());
    } else {
      inTf.setText(res.getString("FilterTable.DefaultPattern", "PATTERN"));
    }
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        inTf.selectAll();
        inTf.requestFocus();
      }
    });
    pane.add(inTf, BorderLayout.CENTER);
    return pane;
  }
  private JComponent createNumberPattern() {
    //
    // NumberPattern
    //
    NumberFormatter nbrFormatter = new NumberFormatter(new DecimalFormat(res.getString("FilterTable.Default.Format", "#####")));
    nbrFormatter.setCommitsOnValidEdit(true);
    nbrFormatter.setAllowsInvalid(false);
    final JFormattedTextField lower = new JFormattedTextField(nbrFormatter);
    final JFormattedTextField upper = new JFormattedTextField(nbrFormatter);
    lower.setColumns(res.getInt("FilterTable.Default.Lower.Cols", 5));
    upper.setColumns(res.getInt("FilterTable.Default.Upper.Cols", 5));
    
    final ActionListener acListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Filter f = model.getFilter(curCol);
        if (f == null) {
          f = new RangeFilter();
        }
        f.setExpression("[" + lower.getText() + ".." + upper.getText() + "]");
        model.setFilter(curCol, f);
      }
    };
    final JPanel pane = createPanel(curCol, acListener);
    lower.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && lower.isEditValid()) {
          upper.requestFocusInWindow();
          upper.selectAll();
        }
      }
    });
    upper.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && upper.isEditValid()) {
          acListener.actionPerformed(null);
          pane.firePropertyChange(OK, false, true);
        }
      }
    });
    JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    valuePanel.add(new JLabel("["));
    valuePanel.add(lower);
    valuePanel.add(new JLabel(".."));
    valuePanel.add(upper);
    valuePanel.add(new JLabel("]"));
    Filter f = model.getFilter(curCol);
    if (f == null) {
      upper.setValue(new Integer(res.getInt("FilterTable.DefaultUpper", 10000)));
      lower.setValue(new Integer(res.getInt("FilterTable.DefaultLower", -10000)));
    } else if (f instanceof RangeFilter) {
      RangeFilter rf = (RangeFilter) f;
      upper.setValue(new Integer(rf.getUpper()));
      lower.setValue(new Integer(rf.getLower()));
    }
    pane.add(valuePanel, BorderLayout.CENTER);
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        lower.selectAll();
        lower.requestFocus();
      }
    });
    return pane;
  }
  private JComponent createDatePattern() {
    //
    // DatePattern
    //
    final DateFormat dateFormat = new SimpleDateFormat(res.getString("FilterTable.Default.DateFormat", "dd-MM-yy"));
    DateFormatter dateFormatter = new DateFormatter(dateFormat);
    dateFormatter.setCommitsOnValidEdit(true);
    dateFormatter.setAllowsInvalid(true);
    final JFormattedTextField lower = new JFormattedTextField(dateFormatter);
    final JFormattedTextField upper = new JFormattedTextField(dateFormatter);
    final ActionListener acListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Filter f = model.getFilter(curCol);
        if (f == null) {
          f = new DateFilter(dateFormat);
        }
        f.setExpression("[" + lower.getText() + ".." + upper.getText() + "]");
        model.setFilter(curCol, f);
      }
    };
    lower.setColumns(res.getInt("FilterTable.Default.Date.Cols", 7));
    upper.setColumns(res.getInt("FilterTable.Default.Date.Cols", 7));
    
    final JPanel pane = createPanel(curCol, acListener);
    lower.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && lower.isEditValid()) {
          upper.requestFocusInWindow();
          upper.selectAll();
        }
      }
    });
    upper.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && upper.isEditValid()) {
          acListener.actionPerformed(null);
          pane.firePropertyChange(OK, false, true);
        }
      }
    });
    JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    valuePanel.add(new JLabel(res.getString("FilterTable.From", "from")));
    valuePanel.add(lower);
    valuePanel.add(new JLabel(res.getString("FilterTable.To", "to")));
    valuePanel.add(upper);
    pane.add(valuePanel, BorderLayout.CENTER);
    Filter f = model.getFilter(curCol);
    if (f == null) {
      Calendar cal = Calendar.getInstance();
      upper.setValue(cal.getTime());
      cal.add(Calendar.MONTH, -1);
      lower.setValue(cal.getTime());
    } else if (f instanceof DateFilter) {
      DateFilter df = (DateFilter) f;
      lower.setValue(df.getStart());
      upper.setValue(df.getEnd());
    }
    return pane;
  }
  
  public void editFilter(int col, Component c, Point p) {
    setColumn(col);
    this.show(c, p.x, p.y);
  }
  public void setColumn(int col) {
    curCol = col;
    prepareMenu(this);
  }
  public void prepareMenu(Container menu) {
    menu.removeAll();
    menu.add(nullMenuItem);
    menu.add(clearMenuItem);
  }
  protected JPopupMenu getPopup(ActionEvent ae) {
    Component c = (Component)ae.getSource();
    while (c != null && !(c instanceof JPopupMenu)) {
      c = c.getParent();
    }
    if (c instanceof JPopupMenu) {
      return (JPopupMenu)c;
    }
    return new JPopupMenu(); // Dummy
  }
  private Popup createPopup(final int col, Component owner, Point p, Dimension d) {
    Class<?> cl = model.getColumnClass(col);
    JComponent comp = null;
    if (Number.class.isAssignableFrom(cl)) {
      comp = createNumberPattern();
    } else if (Date.class.isAssignableFrom(cl)) {
      comp = createDatePattern();
    } else {
      comp = createContainsPattern();
    }
    comp.setSize(d);
    comp.setPreferredSize(d);
    final Popup pop = PopupFactory.getSharedInstance().getPopup(owner, comp, p.x, p.y);
    pops.put(col, pop);
    comp.addPropertyChangeListener("ok_btn", new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        pop.hide();
        pops.remove(col);
      }
    });
    return pop;
  }
  /** Shows the click-component (popup). 
   * <b>Note:</b> This must not be called before the {@link #curCol} has been set {@link #setColumn(int)}*/
  public void showClickComponent(Component owner, Point pos, Dimension dim) {
    Popup pop = pops.get(curCol);
    if (pop != null) {
      pop.hide();
      pops.remove(curCol);
    } else {
      pop = createPopup(curCol, owner, pos, dim);
      pop.show();
    }
  }
  private void clearPops() {
    for (Iterator<Popup> it = pops.values().iterator(); it.hasNext(); ) {
      it.next().hide();
      it.remove();
    }
  }
  private void clearPop(int col) {
    if (pops.containsKey(col)) {
      pops.remove(col).hide();
    }
  }
  private JPanel createPanel(final int col, final ActionListener acListener) {
    final JPanel pane = new JPanel(new BorderLayout());
    JButton okBtn = res.createJButton("FilterTable.Button.Filter");
    okBtn.setMargin(new Insets(0,0,0,0));
    okBtn.setBorder(EMPTY_BORDER);
    okBtn.setContentAreaFilled(false);
    okBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        acListener.actionPerformed(e);
        pane.firePropertyChange(OK, false, true);
      }
    });
    JPanel btns = new JPanel(new GridLayout(1,2));
    btns.setBorder(EMPTY_BORDER);
    btns.add(okBtn);
    
    JButton clearBtn = res.createJButton("FilterTable.Button.ClearFilter");
    clearBtn.setMargin(new Insets(0,0,0,0));
    clearBtn.setBorder(EMPTY_BORDER);
    clearBtn.setContentAreaFilled(false);
    clearBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        model.setFilter(col, null);
        clearPop(col);
      }
    });
    btns.add(clearBtn);
    pane.add(btns, BorderLayout.EAST);
    return pane;
  }
}
