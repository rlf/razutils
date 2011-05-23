package dk.lockfuglsang.rasmus.util.ui.table;

import javax.swing.table.TableModel;

/**
 * Filters out certain specific columns.
 */
public class FilterColumnTableModel extends AbstractTableModelDecorator {
  private int[] skipIndex;
  
  public FilterColumnTableModel(TableModel model, int[] skipIndex) {
    super(model, null);
    this.skipIndex = skipIndex;
  }
  
  @Override
  protected void clearState() {
    // Not used, no state in this filter
  }
  
  /** Returns column-index in the underlying tablemodel.
   * 
   * @param column The column in the filtered model.
   * @return The column in the unfiltered model.
   */
  private int translateColumn(int column) {
    int offset = 0;
    for (int i = 0; i < skipIndex.length; i++) {
      if (skipIndex[i] <= column) {
        offset++;
      } else {
        break;
      }
    }
    return offset + column;
  }
  
  @Override
  public int getColumnCount() {
    return super.tableModel.getColumnCount() - skipIndex.length;
  }
  
  @Override
  public int getRowCount() {
    return super.tableModel.getRowCount();
  }
  
  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    return super.tableModel.getValueAt(rowIndex, translateColumn(columnIndex));
  }
  
  @Override
  public String getColumnName(int column) {
    return super.getColumnName(translateColumn(column));
  }
  
  @Override
  public Class<?> getColumnClass(int column) {
    return super.getColumnClass(translateColumn(column));
  }

  @Override
  public int modelIndex(int row) {
    return row;
  }
}
