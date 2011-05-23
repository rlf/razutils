package test.dk.lockfuglsang.rasmus.util.ui.table;

import static org.junit.Assert.assertEquals;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.junit.BeforeClass;
import org.junit.Test;

import dk.lockfuglsang.rasmus.util.ui.table.FilterColumnTableModel;


public class FilterColumnTableModelTest {
  static TableModel tableModel;

  @BeforeClass
  public static void setUpClass() {
    String[] columnNames = {"Col1", "Col2", "Col3"};
    String[][] data = {
        { "C1R1", "C2R1", "C3R1" },  
        { "C1R2", "C2R2", "C3R2" },  
        { "C1R3", "C2R3", "C3R3" },  
    };
    tableModel = new DefaultTableModel(data, columnNames);
  }
  
  @Test
  public void testFilterColumnTableModel() {
    int[] skip = { 1 };
    FilterColumnTableModel filter = new FilterColumnTableModel(tableModel, skip);
    
    assertEquals("Number of cols", 2, filter.getColumnCount());
    assertEquals("Number of rows", 3, filter.getRowCount());
    assertEquals("C1R1", "C1R1", filter.getValueAt(0, 0));
    assertEquals("C3R3", "C3R3", filter.getValueAt(2, 1));
  }
}
