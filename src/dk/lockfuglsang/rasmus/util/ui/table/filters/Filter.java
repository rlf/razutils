/*
 * Filter.java
 *
 * Created on 26. juli 2007, 21:01
 *
 */

package dk.lockfuglsang.rasmus.util.ui.table.filters;

/**
 * Filter interface.
 * @author Rasmus
 * @version 1.0
 */
public abstract class Filter {
  protected String exp;
  public Filter() {
    exp = null;
  }
  public Filter(String exp) {
    this.exp = exp;
  }
  public void setExpression(String s) {
    exp = s;
  }
  public String getExpression() {
    return exp;
  }
  public abstract boolean match(Object o);
}
