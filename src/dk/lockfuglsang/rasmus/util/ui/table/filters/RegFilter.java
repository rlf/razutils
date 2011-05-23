package dk.lockfuglsang.rasmus.util.ui.table.filters;

public class RegFilter extends Filter {
  
  public RegFilter(String regexp) {
    super(regexp);
  }
  @Override
  public boolean match(Object o) {
    if (exp == null) return true;
    if (o == null) return false;
    return o.toString().matches(exp);
  }
}
