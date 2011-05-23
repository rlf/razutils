/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.lockfuglsang.rasmus.util.ui.table.filters;

/**
 * And filter of composite filters
 */
public class CompositeFilter extends Filter {
  private final Filter[] filters;
  
  public CompositeFilter(Filter... filters) {
    this.filters = filters;
  }
  
  @Override
  public boolean match(Object o) {
    for (Filter filter : filters) {
      if (!filter.match(o)) {
        return false;
      }
    }
    return true;
  }
  
}
