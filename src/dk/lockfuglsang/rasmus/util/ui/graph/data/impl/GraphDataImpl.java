package dk.lockfuglsang.rasmus.util.ui.graph.data.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dk.lockfuglsang.rasmus.util.ui.graph.data.DataSeries;
import dk.lockfuglsang.rasmus.util.ui.graph.data.GraphData;

public class GraphDataImpl<X,Y> implements GraphData<X,Y> {

  private List<X> xvalues;
  private List<DataSeries<Y>> data;
  
  public GraphDataImpl() {
  }
  
  @Override
  public List<DataSeries<Y>> getDataSeries() {
    return data;
  }
  
  @Override
  public List<X> getXValues() {
    return xvalues;
  }
  
  @Override
  public List<String> getLabels() {
    List<String> labels = new ArrayList<String>();
    for (Iterator<DataSeries<Y>> it = data.iterator(); it.hasNext(); ) {
      labels.add(it.next().getLabel());
    }
    return labels;
  }
  
  @Override
  public List<Y> getValueAt(X x) {
    int ix = xvalues.indexOf(x);
    if (ix == -1) {
      throw new IllegalArgumentException("No x-value defined for " + x);
    }
    return getValueAt(ix);
  }
  
  @Override
  public List<Y> getValueAt(int index) {
    List<Y> values = new ArrayList<Y>();
    for (Iterator<DataSeries<Y>> it = data.iterator(); it.hasNext(); ) {
      values.add(it.next().getSeries().get(index));
    }
    return values;
  }

  @Override
  public Y getValueAt(int x, int n) {
    return getValueAt(x).get(n);
  }
  
  @Override
  public int getNumSeries() {
    return data.size();
  }

  @Override
  public int getNumValues() {
    return xvalues.size();
  }
  
}
