package dk.lockfuglsang.rasmus.util.ui.table.filters;

public class NotFilter extends Filter {
  
  private Filter filter;
  
  public NotFilter(Filter f) {
    this.filter = f;
  }
  @Override
  public boolean match(Object o) {
    return !filter.match(o);
  }
}
