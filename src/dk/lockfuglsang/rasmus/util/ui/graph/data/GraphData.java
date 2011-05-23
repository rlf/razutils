package dk.lockfuglsang.rasmus.util.ui.graph.data;

import java.util.List;

public interface GraphData<X,Y> {
  /** Returns the list of x-values. */
  List<X> getXValues();
  
  /** Returns the whole datastructure. */
  List<DataSeries<Y>> getDataSeries();
  
  /** Returns a list of the labels for all the series. */
  List<String> getLabels();
  
  /** Returns a list of all the values for the specific X-value. */
  List<Y> getValueAt(X x);
  
  /** Returns a list of all the values for the specific X-index. */
  List<Y> getValueAt(int index);
  
  /** Returns the nth serie-value at x-index x. */
  Y getValueAt(int x, int n);
  
  /** Returns the number of series. 
   * This number MUST correspond to the size of {@link #getDataSeries()} and the size of {@link #getValueAt(Object)}.
   * @return The number of series.
   */
  int getNumSeries();
  
  /** Returns the number of datapoints.
   * This number MUST correspond to the size of {@link #getXValues()} and the size of each {@link DataSeries}.
   * @return The number of values (x-values).
   */
  int getNumValues();
}
