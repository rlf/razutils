package dk.lockfuglsang.rasmus.util.ui.table;

import javax.swing.table.TableModel;

public interface TableModelFilter {
  /** Maps a row-index to the underlying model index. */
  int modelIndex(int row);
  /** The TableModel being filtered. */
  TableModel getModel();
  /** Returns the number of rows.
   * @see TableModel#getRowCount()
   * @return the number of rows
   */
  int getRowCount();
}
