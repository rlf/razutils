/*
 * ContainsFilter.java
 *
 * Created on 26. juli 2007, 21:03
 *
 */

package dk.lockfuglsang.rasmus.util.ui.table.filters;

/**
 * The ContainsFilter returns true iff the objects stringrepresentation 
 * contains the expression.
 * @author Rasmus
 * @version 1.0
 */
public class ContainsFilter extends Filter {
  public ContainsFilter() {
    super();
  }
  public ContainsFilter(String exp) {
    super(exp);
  }
  public boolean match(Object o) {
    if (exp == null) return true;
    if (o == null) return false;
    return o.toString().indexOf(exp) != -1;
  }
}
