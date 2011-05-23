/*
 * FilterTableHeaderRenderer.java
 *
 * Created on 7. august 2007, 21:09
 *
 */

package dk.lockfuglsang.rasmus.util.ui.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import dk.lockfuglsang.rasmus.util.resources.Resource;
import dk.lockfuglsang.rasmus.util.ui.table.filters.Filter;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class FilterTableHeaderRenderer implements TableCellRenderer {
  protected final String NO_FILTER;
  protected TableCellRenderer parent;
  protected JPanel panel;
  protected JTextField textField;
  protected FilterRowTableModel model;
  protected MouseListener mouseListener;
  protected JTableHeader tableHeader;
  
  protected FilterTablePopup popup;
  
  /** Creates a new instance of FilterTableHeaderRenderer */
  public FilterTableHeaderRenderer(TableCellRenderer parent, FilterRowTableModel ftmodel, final FilterTablePopup popup, final Resource res) {
    this.parent = parent;
    this.model = ftmodel;
    panel = new JPanel(new BorderLayout());
    textField = new JTextField();
    panel.add(textField, BorderLayout.NORTH);
    mouseListener = new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        if (p.y < textField.getSize().height) {
          if (e.getButton() == MouseEvent.BUTTON3) {
            JTableHeader h = (JTableHeader) e.getSource();
            TableColumnModel columnModel = h.getColumnModel();
            int col = columnModel.getColumnIndexAtX(e.getX());
            popup.editFilter(col, h, p);
          } else if (e.getButton() == MouseEvent.BUTTON1) {
            JTableHeader h = (JTableHeader) e.getSource();
            TableColumnModel columnModel = h.getColumnModel();
            int column = columnModel.getColumnIndexAtX(e.getX());
            popup.setColumn(column);
            Point pos = h.getLocationOnScreen();
            for (int col = 0; col < column; col++) {
              pos.x += columnModel.getColumn(col).getWidth();
              pos.x += columnModel.getColumnMargin();
            }
            Dimension dim = textField.getSize();
            popup.showClickComponent(h.getTable(), pos, dim);
          }
          e.consume();
        }
      }
    };
    NO_FILTER = res.getString("FilterTable.String.NoFilter", "");
  }
  public void setTableHeader(JTableHeader tableHeader) {
    if (this.tableHeader != null) {
      this.tableHeader.removeMouseListener(mouseListener);
    }
    this.tableHeader = tableHeader;
    if (this.tableHeader != null) {
      this.tableHeader.addMouseListener(mouseListener);
    }
  }
  
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    Component c = parent.getTableCellRendererComponent(table,  value, isSelected,
        hasFocus, row, column);
    Filter f = model.getFilter(column);
    if (f != null) {
      textField.setText(f.getExpression());
    } else {
      textField.setText(NO_FILTER);
    }
    panel.add(c, BorderLayout.SOUTH);
    return panel;
  }
}
