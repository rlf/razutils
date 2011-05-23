/*
 * FilterTableModel.java
 *
 * Created on 26. juli 2007, 19:40
 *
 */

package dk.lockfuglsang.rasmus.util.ui.table;

import java.util.HashMap;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import dk.lockfuglsang.rasmus.util.ui.table.filters.Filter;

/**
 * The {@link FilterRowTableModel} allows the user to filter out rows based on {@link Filter}s applied to specific columns
 * 
 * @see #setFilter(int, Filter)
 */
public class FilterRowTableModel extends AbstractTableModelDecorator implements TableModelListener {
  private Map<Integer,Filter> filters;
  private int rows[];
  
  /**
   * Creates a new instance of FilterTableModel
   */
  public FilterRowTableModel(TableModel model) {
    this.filters = new HashMap<Integer, Filter>();
    this.tableModelListener = this;
    setTableModel(model);
  }
  public int getRowCount() {
    return getRowIndices().length;
  }
  public synchronized Filter getFilter(int column) {
    return filters.get(new Integer(column));
  }
  public synchronized void setFilter(int column, Filter filter) {
    if (filter == null) {
      filters.remove(new Integer(column));
    } else {
      filters.put(new Integer(column), filter);
    }
    clearState();
    fireTableStructureChanged();
  }
  public int getColumnCount() {
    return tableModel.getColumnCount();
  }
  
  public int modelIndex(int row) {
    int[] rows = getRowIndices();
    return rows[row];
  }
  
  public synchronized Object getValueAt(int rowIndex, int columnIndex) {
    return tableModel.getValueAt(modelIndex(rowIndex), columnIndex);
  }
  
  protected void clearState() {
    rows = null;
  }
  protected synchronized int[] getRowIndices() {
    if (rows == null) {
      int newrows[] = new int[tableModel.getRowCount()];
      int nrow = 0;
      if (filters.size() > 0) {
        for (int row = 0; row < newrows.length; row++) {
          boolean keep = true;
          for (Integer index : filters.keySet()) {
            Filter filter = filters.get(index);
            int colIndex = index.intValue();
            keep &= filter.match(tableModel.getValueAt(row, colIndex));
          }
          if (keep) {
            newrows[nrow++] = row;
          }
        }
        rows = new int[nrow];
        System.arraycopy(newrows, 0, rows, 0, nrow);
      } else {
        rows = new int[tableModel.getRowCount()];
        for (int i = 0; i < rows.length; i++) {
          rows[i] = i;
        }
      }
    }
    return rows;
  }
  @Override
  public void tableChanged(TableModelEvent e) {
    clearState();
    fireTableStructureChanged();
  }
}
