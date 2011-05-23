package test.dk.lockfuglsang.rasmus.util.ui.graph.data.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.junit.Before;
import org.junit.Test;

import dk.lockfuglsang.rasmus.util.ui.graph.data.GraphData;
import dk.lockfuglsang.rasmus.util.ui.graph.data.impl.TableModelGraphData;
import dk.lockfuglsang.rasmus.util.ui.table.ColumnClassTableModel;

public class TableModelGraphDataTest {
  
  DefaultTableModel validModel;
  
  @Before
  public void setUp() {
    validModel = new ColumnClassTableModel();
    String[] columns = new String[] {"Legend", "X1", "X3", "X2"};
    Object[][] dataVector = new Object[][] {
      { "SeriesA", new Integer(1), new Integer(2), new Integer(3)},
      { "SeriesB", new Integer(1), new Integer(3), new Integer(5)},
      { "SeriesC", new Integer(-10), new Integer(10), new Integer(0)}
    };
    validModel.setDataVector(dataVector, columns);
  }
  
  @Test
  public void testTableModelGraphData_Empty() {
    TableModel emptyModel = new DefaultTableModel();
    GraphData<String,Number> graphData = new TableModelGraphData(emptyModel);
    
    try {
      graphData.getDataSeries();
      fail("Exception expected");
    } catch (UnsupportedOperationException e) {
      // As expected!
    }
    assertEquals("No xvalues", Collections.EMPTY_LIST, graphData.getXValues());
    assertEquals("No labels", Collections.EMPTY_LIST, graphData.getLabels());
    assertEquals("# series should be", 0, graphData.getNumSeries());
    assertEquals("# values should be", 0, graphData.getNumValues());
  }
  
  @Test
  public void testTableModelGraphData_Valid() {
    TableModel model = validModel;
    GraphData<String,Number> graphData = new TableModelGraphData(model);
    
    try {
      graphData.getDataSeries();
      fail("Exception expected");
    } catch (UnsupportedOperationException e) {
      // As expected!
    }
    assertEquals("xvalues", Arrays.asList(new String[] {"X1", "X3", "X2"}), graphData.getXValues());
    assertEquals("labels", Arrays.asList(new String[] {"SeriesA", "SeriesB", "SeriesC"}), graphData.getLabels());
    assertEquals("# series should be", 3, graphData.getNumSeries());
    assertEquals("# values should be", 3, graphData.getNumValues());
    assertEquals("value at 0,0", new Integer(1), graphData.getValueAt(0,0));
    assertEquals("value at 0,2", new Integer(-10), graphData.getValueAt(0,2));
    assertEquals("value at 2,2", new Integer(0), graphData.getValueAt(2,2));
    assertEquals("value at 0", Arrays.asList(new Integer[] {1,1,-10}), graphData.getValueAt(0));
    assertEquals("value at X3", Arrays.asList(new Integer[] {2,3,10}), graphData.getValueAt("X3"));
  }
}
