package dk.lockfuglsang.rasmus.util.ui.graph.data.impl;

import java.util.ArrayList;
import java.util.List;

import dk.lockfuglsang.rasmus.util.ui.graph.data.DataSeries;

public class DataSeriesImpl<T> implements DataSeries<T> {

  private String label;
  private List<T> data;
  public DataSeriesImpl(String label, List<T> data) {
    this.data = new ArrayList<T>(data);
    this.label = label;
  }
  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public List<T> getSeries() {
    return data;
  }
  
}
