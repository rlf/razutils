/*
 * ColumnClassTableModel.java
 *
 * Created on 11. juli 2007, 20:06
 *
 */

package dk.lockfuglsang.rasmus.util.ui.table;

import java.util.Arrays;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class ColumnClassTableModel extends DefaultTableModel {
  
  /** Creates a new instance of ColumnClassTableModel */
  public ColumnClassTableModel() {
  }
  /** A list of classes of the columns. */
  protected Class<?>[] colClasses;
  
  /**
   * Sets the value at the specified position and fires a
   * <code>fireTableRowsUpdated()</code> is the value updated is the
   * column-status.
   */
  public synchronized void setValueAt(Object aValue, int row, int column) {
    super.setValueAt(aValue, row, column);
    this.fireTableRowsUpdated(row, row);
  }
  
  /**
   * {@inheritDoc} <p/>Also tries to denote the <code>Class</code> of the
   * specific columns.
   */
  public void setDataVector(Object[][] dataVector, Object[] columnIdentifiers) {
    super.setDataVector(dataVector, columnIdentifiers);
    int ncols = getColumnCount();
    int nrows = getRowCount();
    colClasses = new Class[ncols];
    // Default... revert to the Object.class
    Arrays.fill(colClasses, Object.class);
    // Scan the dataVector for columns of higher class.
    if (nrows > 0) {
      for (int i = 0; i < ncols; i++) {
        if (dataVector[0][i] != null) {
          Class<?> colClass = dataVector[0][i].getClass();
          for (int j = 1; j < nrows; j++) {
            if (dataVector[j][i] != null) {
              if (!colClass.isAssignableFrom(dataVector[j][i].getClass())) {
                colClass = Object.class;
              }
            }
          }
          colClasses[i] = colClass;
        }
      }
    }
    fireTableStructureChanged();
  }
  
  /**
   * Returns the class of this column if the complete column contains objects of
   * the same class. The <code>Object.class</code> is returned otherwise.
   */
  public Class<?> getColumnClass(int columnIndex) {
    if (colClasses == null) {
      throw new IllegalStateException(
          "The column class cannot be determined before the setDataVector has been called!");
    }
    if (columnIndex < 0 || columnIndex >= colClasses.length) {
      throw new IllegalArgumentException(
          "The columnIndex must be in the range [0.." + colClasses.length + "[");
    }
    return colClasses[columnIndex];
  }
  /** Forces a specific column to a specific type. */
  public void setColumnClass(int columnIndex, Class<?> cl) {
    if (colClasses == null) {
      throw new IllegalStateException(
          "The column class cannot be set before the setDataVector has been called!");
    }
    if (columnIndex < 0 || columnIndex >= colClasses.length) {
      throw new IllegalArgumentException(
          "The columnIndex must be in the range [0.." + colClasses.length + "[");
    }
    colClasses[columnIndex] = cl;
  }
}
