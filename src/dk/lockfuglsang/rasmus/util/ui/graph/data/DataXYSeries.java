package dk.lockfuglsang.rasmus.util.ui.graph.data;

import java.util.List;

public interface DataXYSeries<X,Y> extends DataSeries<Y> {
  List<X> getXValues();
}
