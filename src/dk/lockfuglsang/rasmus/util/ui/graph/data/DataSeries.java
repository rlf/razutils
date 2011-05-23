package dk.lockfuglsang.rasmus.util.ui.graph.data;

import java.util.List;

/** A {@link DataSeries} contains the label and data to be graphed.
 * 
 */
public interface DataSeries<T> {
  String getLabel();
  List<T> getSeries(); 
}
