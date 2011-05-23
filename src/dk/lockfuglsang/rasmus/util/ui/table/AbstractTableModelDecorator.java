/*
 * AbstractTableModelDecorator.java
 *
 * Created on 26. juli 2007, 19:46
 *
 */

package dk.lockfuglsang.rasmus.util.ui.table;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public abstract class AbstractTableModelDecorator extends AbstractTableModel implements TableModelFilter {
  protected TableModel tableModel;
  protected TableModelListener tableModelListener;
  
  public AbstractTableModelDecorator() {
    this.tableModel = null;
    this.tableModelListener = null;
  }
  /** Creates a new instance of AbstractTableModelDecorator */
  public AbstractTableModelDecorator(TableModel model, 
      TableModelListener tableModelListener) {
    this.tableModelListener = tableModelListener;
    setTableModel(model);
  }
  public void setTableModel(TableModel model) {
    if (this.tableModel != null) {
      this.tableModel.removeTableModelListener(tableModelListener);
    }
    
    this.tableModel = model;
    if (this.tableModel != null) {
      this.tableModel.addTableModelListener(tableModelListener);
    }
    fireTableStructureChanged();
  }
  /** Returns the table-model. */
  public TableModel getTableModel() {
    return tableModel;
  }
  
  /** Returns the column name of the selected column. */
  public String getColumnName(int column) {
    return tableModel.getColumnName(column);
  }
  
  /** Returns the column class of the selected column. */
  @SuppressWarnings("unchecked")
  public Class<?> getColumnClass(int column) {
    return tableModel.getColumnClass(column);
  }
  /** Clears the state of the decorator. */
  protected abstract void clearState();
  
  public abstract int modelIndex(int row);
  
  public TableModel getModel() {
    return tableModel;
  }
}
