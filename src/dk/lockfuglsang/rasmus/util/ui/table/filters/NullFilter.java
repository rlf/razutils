/*
 * NullFilter.java
 *
 * Created on 12. august 2007, 12:45
 *
 */

package dk.lockfuglsang.rasmus.util.ui.table.filters;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class NullFilter extends Filter {
  
  /** Creates a new instance of NullFilter */
  public NullFilter() {
  }

  public synchronized boolean match(Object o) {
    return o == null;
  }
}
