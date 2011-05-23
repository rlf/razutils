package dk.lockfuglsang.rasmus.util.ui.table;

// Implementation Note: This class is "snagged" from the Java Tutorial on JTables
// Ref: http://java.sun.com/docs/books/tutorial/uiswing/components/table.html#sorting
// Only minor changes have been made by me (Rasmus Lock Larsen)
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * TableSorterModel is a decorator for TableModels; adding sorting functionality to a
 * supplied TableModel. 
 * 
 * 
 * <p/>
 * <strong><font color="red">NOTE: This class is adapted from 
 * <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/table.html#sorting">
 * http://java.sun.com/docs/books/tutorial/uiswing/components/table.html#sorting</a>
 * </font></strong>
 * 
 * <p/>
 * TableSorterModel does not store or copy the data in its
 * TableModel; instead it maintains a map from the row indexes of the view to
 * the row indexes of the model. As requests are made of the sorter (like
 * getValueAt(row, col)) they are passed to the underlying model after the row
 * numbers have been translated via the internal mapping array. This way, the
 * TableSorterModel appears to hold another copy of the table with the rows in a
 * different order. <p/> TableSorterModel registers itself as a listener to the
 * underlying model, just as the JTable itself would. Events recieved from the
 * model are examined, sometimes manipulated (typically widened), and then
 * passed on to the TableSorterModel's listeners (typically the JTable). If a change
 * to the model has invalidated the order of TableSorterModel's rows, a note of this
 * is made and the sorter will resort the rows the next time a value is
 * requested. <p/> When the tableHeader property is set, either by using the
 * setTableHeader() method or the two argument constructor, the table header may
 * be used as a complete UI for TableSorterModel. The default renderer of the
 * tableHeader is decorated with a renderer that indicates the sorting status of
 * each column. In addition, a mouse listener is installed with the following
 * behavior:
 * <ul>
 * <li> Mouse-click: Clears the sorting status of all other columns and advances
 * the sorting status of that column through three values: {NOT_SORTED,
 * ASCENDING, DESCENDING} (then back to NOT_SORTED again).
 * <li> SHIFT-mouse-click: Clears the sorting status of all other columns and
 * cycles the sorting status of the column through the same three values, in the
 * opposite order: {NOT_SORTED, DESCENDING, ASCENDING}.
 * <li> CONTROL-mouse-click and CONTROL-SHIFT-mouse-click: as above except that
 * the changes to the column do not cancel the statuses of columns that are
 * already sorting - giving a way to initiate a compound sort.
 * </ul>
 * <p/> This is a long overdue rewrite of a class of the same name that first
 * appeared in the swing table demos in 1997.
 * 
 * 
 * @author Philip Milne
 * @author Brendon McLean
 * @author Dan van Enckevort
 * @author Parwinder Sekhon
 * @author Rasmus Lock Larsen - Added Enums and javadoc to the class
 * @version 2.1 02/27/04
 */

public class TableSorterModel extends AbstractTableModelDecorator implements TableModelFilter {
  /** Null-object, default directive. */
  private static Directive EMPTY_DIRECTIVE = new Directive(-1,
      SortOrder.NOT_SORTED);

  /** Default comparator for <code>Comparable</code> objects. */
  public static final Comparator<Object> COMPARABLE_COMPARATOR = new Comparator<Object>() {
    @SuppressWarnings("unchecked")
    public int compare(Object o1, Object o2) {
      return ((Comparable) o1).compareTo(o2);
    }
  };

  /** Lexical comparator. */
  public static final Comparator<Object> LEXICAL_COMPARATOR = new Comparator<Object>() {
    public int compare(Object o1, Object o2) {
      return o1.toString().compareTo(o2.toString());
    }
  };

  /** View to model mappings. */
  private Row[] viewToModel;

  /** Model to view mappings. */
  private int[] modelToView;

  /** The table header. */
  private JTableHeader tableHeader;

  /** The mouse-listener. For changing sort-order. */
  private MouseListener mouseListener;

  /** Comparators for each column. */
  private Map<Class<Object>, Comparator<Object>> columnComparators = new HashMap<Class<Object>, Comparator<Object>>();

  /**
   * List of columns currently controlling the sort-order. Activated by using
   * the <code>Ctrl+Click</code>.
   */
  private List<Directive> sortingColumns = new ArrayList<Directive>();

  /** Default constructor. */
  public TableSorterModel() {
    this.tableModelListener = new TableModelHandler();
    this.mouseListener = new MouseHandler();    
  }

  /** Constructor with injected table-model. */
  public TableSorterModel(TableModel tableModel) {
    this.tableModelListener = new TableModelHandler();
    this.mouseListener = new MouseHandler();
    setTableModel(tableModel);
  }

  /** Constructor with injected tableModel and tableHeader. */
  public TableSorterModel(TableModel tableModel, JTableHeader tableHeader) {
    this(tableModel);
    setTableHeader(tableHeader);
  }

  /** Resets the sorting-order of all columns. */
  protected void clearState() {
    viewToModel = null;
    modelToView = null;
  }
  
  /** Returns the table-header. */
  public JTableHeader getTableHeader() {
    return tableHeader;
  }

  /**
   * Sets the table-header and injects the <code>SortableHeaderRenderer</code>
   * to render the sorting arrows.
   */
  public void setTableHeader(JTableHeader tableHeader) {
    if (this.tableHeader != null) {
      this.tableHeader.removeMouseListener(mouseListener);
      TableCellRenderer defaultRenderer = this.tableHeader.getDefaultRenderer();
      if (defaultRenderer instanceof SortableHeaderRenderer) {
        this.tableHeader
            .setDefaultRenderer(((SortableHeaderRenderer) defaultRenderer).tableCellRenderer);
      }
    }
    this.tableHeader = tableHeader;
    if (this.tableHeader != null) {
      this.tableHeader.addMouseListener(mouseListener);
      this.tableHeader.setDefaultRenderer(new SortableHeaderRenderer(
          this.tableHeader.getDefaultRenderer()));
    }
  }

  /** Returns whether sorting is active. */
  public boolean isSorting() {
    return sortingColumns.size() != 0;
  }

  /**
   * Returns the directive governing the selected column.
   * 
   * @return <code>EMPTY_DIRECTIVE</code> if the column is not sorted.
   */
  private Directive getDirective(int column) {
    for (int i = 0; i < sortingColumns.size(); i++) {
      Directive directive = (Directive) sortingColumns.get(i);
      if (directive.column == column) {
        return directive;
      }
    }
    return EMPTY_DIRECTIVE;
  }

  /** Returns the SortOrder of the given column. */
  public SortOrder getSortingStatus(int column) {
    return getDirective(column).direction;
  }
  public SortOrder[] getSortingStatus() {
    SortOrder[] result = new SortOrder[tableModel.getColumnCount()];
    for (Directive d : sortingColumns) {
      result[d.column] = d.direction;
    }
    return result;
  }

  /** Re-sorts the rows according to the column sort-order. */
  private void sortingStatusChanged() {
    clearState();
    fireTableDataChanged();
    if (tableHeader != null) {
      tableHeader.repaint();
    }
  }

  /** Changes the sorting-status for the given column. */
  @SuppressWarnings("unchecked")
  public void setSortingStatus(int column, SortOrder rstatus) {
    SortOrder status = rstatus;
    if (status == null) { 
      status = SortOrder.NOT_SORTED;
    }
    Directive directive = getDirective(column);
    if (directive != EMPTY_DIRECTIVE) {
      sortingColumns.remove(directive);
    }
    if (status != SortOrder.NOT_SORTED) {
      sortingColumns.add(new Directive(column, status));
    }
    sortingStatusChanged();
  }

  /**
   * Returns the proper <code>Icon</code> for visualizing the sort-order of
   * the given column.
   * 
   * @param column
   *          The columnIndex.
   * @param size
   *          The size of the arrow to return.
   * @return An <code>Arrow</code> visualizing the sort-order or
   *         <code>null</code> if the column is unsorted.
   */
  protected Icon getHeaderRendererIcon(int column, int size) {
    Directive directive = getDirective(column);
    if (directive == EMPTY_DIRECTIVE) {
      return null;
    }
    return new Arrow(directive.direction == SortOrder.DESCENDING, size,
        sortingColumns.indexOf(directive));
  }

  /** Resets sorting. The table will be unsorted. */
  private void cancelSorting() {
    sortingColumns.clear();
    sortingStatusChanged();
  }

  /** Specifically sets the comparator for a column-Class. */
  @SuppressWarnings("unchecked")
  public void setColumnComparator(Class type, Comparator comparator) {
    if (comparator == null) {
      columnComparators.remove(type);
    } else {
      columnComparators.put(type, comparator);
    }
  }

  /**
   * Returns the comparator to be used for the specified column.
   * 
   * <p/><b>Note:</b> Requires the model to implement
   * <code>getColumnClass(int)</code> to function correctly.
   * 
   * @param column
   *          The index of the column.
   * @return The correct comparator.
   */
  protected Comparator<Object> getComparator(int column) {
    Class<? extends Object> columnType = tableModel.getColumnClass(column);
    Comparator<Object> comparator = columnComparators.get(columnType);
    if (comparator != null) {
      return comparator;
    }
    if (Comparable.class.isAssignableFrom(columnType)) {
      return COMPARABLE_COMPARATOR;
    }
    return LEXICAL_COMPARATOR;
  }

  /** Returns the current view-to-model mapping. */
  private Row[] getViewToModel() {
    if (viewToModel == null) {
      int tableModelRowCount = tableModel.getRowCount();
      viewToModel = new Row[tableModelRowCount];
      for (int row = 0; row < tableModelRowCount; row++) {
        viewToModel[row] = new Row(row);
      }

      if (isSorting()) {
        Arrays.sort(viewToModel);
      }
    }
    return viewToModel;
  }

  /** Returns the modelIndex based on the viewIndex. */
  public int modelIndex(int viewIndex) {
    int index = getViewToModel()[viewIndex].modelIndex;
    return index;
  }

  /** Returns the model-to-view mapping. */
  private int[] getModelToView() {
    if (modelToView == null) {
      int n = getViewToModel().length;
      modelToView = new int[n];
      for (int i = 0; i < n; i++) {
        modelToView[modelIndex(i)] = i;
      }
    }
    return modelToView;
  }

  // TableModel interface methods
  /** Returns the number of rows currently in the tablemodel. */
  public int getRowCount() {
    return (tableModel == null) ? 0 : tableModel.getRowCount();
  }

  /** Returns the number of columns currently in the tablemodel. */
  public int getColumnCount() {
    return (tableModel == null) ? 0 : tableModel.getColumnCount();
  }

  /** Returns the column name of the selected column. */
  public String getColumnName(int column) {
    return tableModel.getColumnName(column);
  }

  /** Returns the column class of the selected column. */
  @SuppressWarnings("unchecked")
  public Class getColumnClass(int column) {
    return tableModel.getColumnClass(column);
  }

  /** Indicates whether the specified cell is editable. */
  public boolean isCellEditable(int row, int column) {
    return tableModel.isCellEditable(modelIndex(row), column);
  }

  /** Returns the value at the specified indices. */
  public Object getValueAt(int row, int column) {
    return tableModel.getValueAt(modelIndex(row), column);
  }

  /** Sets the value at the specified indices. */
  public void setValueAt(Object aValue, int row, int column) {
    tableModel.setValueAt(aValue, modelIndex(row), column);
  }

  // Helper enums

  /**
   * The <code>SortOrder</code> indicates which order the column should be
   * sorted.
   */
  public enum SortOrder {
    NOT_SORTED, ASCENDING, DESCENDING;

    /**
     * Returns the next <code>SortOrder</code>. Used for looping through the
     * SortOrders.
     * 
     * @return The next sortorder.
     */
    public SortOrder getNext() {
      return getNext(1);
    }

    /**
     * Returns the previous <code>SortOrder</code>. Used for looping through
     * the SortOrders.
     * 
     * @return The next sortorder.
     */
    public SortOrder getPrevious() {
      return getNext(-1);
    }

    /**
     * Private helper method for iterating through the possible
     * <code>SortOrder</code>s.
     */
    private SortOrder getNext(int delta) {
      int index = ordinal();
      SortOrder[] values = values();
      index += delta;
      if (index < 0)
        index += values.length;
      index = index % values.length;
      return values[index];
    }
  }

  // Helper classes

  /** Helper class which holds mapping between the view and the model. */
  @SuppressWarnings("unchecked")
  private class Row implements Comparable {
    private int modelIndex;

    /** Simple constructor. */
    public Row(int index) {
      this.modelIndex = index;
    }

    /** Implementation of the <code>Comparable</code> interface. */
    @SuppressWarnings("unchecked")
    public int compareTo(Object o) {
      int row1 = modelIndex;
      int row2 = ((Row) o).modelIndex;

      for (Iterator it = sortingColumns.iterator(); it.hasNext();) {
        Directive directive = (Directive) it.next();
        int column = directive.column;
        Object o1 = tableModel.getValueAt(row1, column);
        Object o2 = tableModel.getValueAt(row2, column);

        int comparison = 0;
        // Define null less than everything, except null.
        if (o1 == null && o2 == null) {
          comparison = 0;
        } else if (o1 == null) {
          comparison = -1;
        } else if (o2 == null) {
          comparison = 1;
        } else {
          comparison = getComparator(column).compare(o1, o2);
        }
        if (comparison != 0) {
          return directive.direction == SortOrder.DESCENDING ? -comparison
              : comparison;
        }
      }
      return 0;
    }
  }

  /** The implementation of the <code>TableModelListener</code>.
   * Controls whether the complete table should be reordered.
   * 
   * @version 1.0
   */
  private class TableModelHandler implements TableModelListener {
    /** {@inheritDoc} */
    public void tableChanged(TableModelEvent e) {
      // If we're not sorting by anything, just pass the event along.
      if (!isSorting()) {
        clearState();
        fireTableChanged(e);
        return;
      }

      // If the table structure has changed, cancel the sorting; the
      // sorting columns may have been either moved or deleted from
      // the model.
      if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
        cancelSorting();
        fireTableChanged(e);
        return;
      }

      // We can map a cell event through to the view without widening
      // when the following conditions apply:
      // 
      // a) all the changes are on one row (e.getFirstRow() ==
      // e.getLastRow()) and,
      // b) all the changes are in one column (column !=
      // TableModelEvent.ALL_COLUMNS) and,
      // c) we are not sorting on that column (getSortingStatus(column) ==
      // NOT_SORTED) and,
      // d) a reverse lookup will not trigger a sort (modelToView != null)
      //
      // Note: INSERT and DELETE events fail this test as they have column
      // == ALL_COLUMNS.
      // 
      // The last check, for (modelToView != null) is to see if
      // modelToView
      // is already allocated. If we don't do this check; sorting can
      // become
      // a performance bottleneck for applications where cells
      // change rapidly in different parts of the table. If cells
      // change alternately in the sorting column and then outside of
      // it this class can end up re-sorting on alternate cell updates -
      // which can be a performance problem for large tables. The last
      // clause avoids this problem.
      int column = e.getColumn();
      if (e.getFirstRow() == e.getLastRow()
          && column != TableModelEvent.ALL_COLUMNS
          && getSortingStatus(column) == SortOrder.NOT_SORTED
          && modelToView != null) {
        int viewIndex = getModelToView()[e.getFirstRow()];
        fireTableChanged(new TableModelEvent(TableSorterModel.this, viewIndex,
            viewIndex, column, e.getType()));
        return;
      }

      // Something has happened to the data that may have invalidated the
      // row order.
      clearState();
      fireTableDataChanged();
      return;
    }
  }

  /** <code>MouseAdaptor</code> for controlling the sorting of the columns. */
  private class MouseHandler extends MouseAdapter {
    /** Mouse was clicked on a table header. */
    public void mouseClicked(MouseEvent e) {
      if (e.isConsumed()) return;
      JTableHeader h = (JTableHeader) e.getSource();
      TableColumnModel columnModel = h.getColumnModel();
      int viewColumn = columnModel.getColumnIndexAtX(e.getX());
      int column = columnModel.getColumn(viewColumn).getModelIndex();
      if (column != -1) {
        SortOrder status = getSortingStatus(column);
        if (!e.isControlDown()) {
          cancelSorting();
        }
        // Cycle the sorting states through {NOT_SORTED, ASCENDING,
        // DESCENDING} or
        // {NOT_SORTED, DESCENDING, ASCENDING} depending on whether
        // shift is pressed.
        status = (e.isShiftDown() ? status.getPrevious() : status.getNext());
        setSortingStatus(column, status);
      }
    }
  }

  /** Internal implementation of the arrows displayed when sorting is active.
   * 
   * @version 1.0
   */
  private static class Arrow implements Icon {
    private boolean descending;

    private int size;

    private int priority;

    public Arrow(boolean descending, int size, int priority) {
      this.descending = descending;
      this.size = size;
      this.priority = priority;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
      Color color = c == null ? Color.GRAY : c.getBackground();
      // In a compound sort, make each succesive triangle 20%
      // smaller than the previous one.
      int dx = (int) (size / 2 * Math.pow(0.8, priority));
      int dy = descending ? dx : -dx;
      // Align icon (roughly) with font baseline.
      y = y + 5 * size / 6 + (descending ? -dy : 0);
      int shift = descending ? 1 : -1;
      g.translate(x, y);

      // Right diagonal.
      g.setColor(color.darker());
      g.drawLine(dx / 2, dy, 0, 0);
      g.drawLine(dx / 2, dy + shift, 0, shift);

      // Left diagonal.
      g.setColor(color.brighter());
      g.drawLine(dx / 2, dy, dx, 0);
      g.drawLine(dx / 2, dy + shift, dx, shift);

      // Horizontal line.
      if (descending) {
        g.setColor(color.darker().darker());
      } else {
        g.setColor(color.brighter().brighter());
      }
      g.drawLine(dx, 0, 0, 0);

      g.setColor(color);
      g.translate(-x, -y);
    }

    public int getIconWidth() {
      return size;
    }

    public int getIconHeight() {
      return size;
    }
  }

  /** Renderer for the table-headers. */
  private class SortableHeaderRenderer implements TableCellRenderer {
    private TableCellRenderer tableCellRenderer;

    /** Injecting contructor. */
    public SortableHeaderRenderer(TableCellRenderer tableCellRenderer) {
      this.tableCellRenderer = tableCellRenderer;
    }

    /** {@inheritDoc} */
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      Component c = tableCellRenderer.getTableCellRendererComponent(table,
          value, isSelected, hasFocus, row, column);
      Component cl = c;
      if (c instanceof JPanel) {
        // Search for a label
        for (Component comp : ((JPanel)c).getComponents()) {
          if (comp instanceof JLabel) {
            cl = comp;
            break;
          }
        }
      }
      if (cl instanceof JLabel) {
        JLabel l = (JLabel) cl;
        l.setHorizontalTextPosition(JLabel.LEFT);
        int modelColumn = table.convertColumnIndexToModel(column);
        l.setIcon(getHeaderRendererIcon(modelColumn, l.getFont().getSize()));
      }
      return c;
    }
  }

  /** Container class for column and sort-direction. */
  private static class Directive {
    private int column;

    private SortOrder direction;

    public Directive(int column, SortOrder direction) {
      this.column = column;
      this.direction = direction;
    }
  }
}