package dk.lockfuglsang.rasmus.util.ui.graph.data.impl;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.TableModel;

import dk.lockfuglsang.rasmus.util.ui.graph.data.DataSeries;
import dk.lockfuglsang.rasmus.util.ui.graph.data.GraphData;

/** Implements a mapping from a {@link TableModel} to a {@link GraphData}.
 * 
 * The {@link TableModel} is organized so the first column is the series, and the {@link TableModel#getColumnName(int)} defines the x-values.
 * 
 */
public class TableModelGraphData implements GraphData<String, Number> {

  private TableModel model;
  
  public TableModelGraphData(TableModel model) {
    this.model = model;
  }
  
  @Override
  public List<DataSeries<Number>> getDataSeries() {
    throw new UnsupportedOperationException("This operation is not supported!");
  }
  
  @Override
  public List<String> getLabels() {
    int n = getNumSeries();
    List<String> labels = new ArrayList<String>(n);
    for (int row = 0; row < n; row++) {
      labels.add(model.getValueAt(row, 0).toString());
    }
    return labels;
  }
  
  @Override
  public int getNumSeries() {
    return model.getRowCount();
  }
  
  @Override
  public int getNumValues() {
    return Math.max(0, model.getColumnCount() - 1);
  }
  
  @Override
  public List<String> getXValues() {
    int n = getNumValues();
    List<String> xvalues = new ArrayList<String>(n);
    for (int i = 0; i < n; i++) {
      xvalues.add(model.getColumnName(i+1));
    }
    return xvalues;
  }

  @Override
  public List<Number> getValueAt(String x) {
    int col = getXValues().indexOf(x);
    if (col == -1) {
      throw new IllegalArgumentException("No column named " + x + " was found!");
    }
    return getValueAt(col);
  }
  
  @Override
  public Number getValueAt(int x, int n) {
    return (Number) model.getValueAt(n, x+1);
  }
  
  @Override
  public List<Number> getValueAt(int index) {
    int col = index + 1;
    int n = getNumSeries();
    List<Number> values = new ArrayList<Number>(n);
    for (int row = 0; row < n; row++) {
      Object o = model.getValueAt(row, col);
      if (o instanceof Number) {
        values.add((Number) o);
      } else {
        throw new IllegalArgumentException("Value in row " + row + ", col " + col + " is not a Number but " + o);
      }
    }
    return values;
  }
}
