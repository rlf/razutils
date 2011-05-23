/*
 * RangeFilter.java
 *
 * Created on 11. august 2007, 11:04
 *
 */

package dk.lockfuglsang.rasmus.util.ui.table.filters;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class RangeFilter extends Filter {
  int lower;
  int upper;
  /** Creates a new instance of RangeFilter */
  public RangeFilter() {
    // Syntax: [lower..upper]
  }
  public RangeFilter(String exp) {
    super(exp);
  }
  public RangeFilter(int lower, int upper) {
    super("[" + lower + ".." + upper + "]");
  }

  public boolean match(Object o) {
    if (o instanceof Number) {
      int i = ((Number)o).intValue();
      return i >= lower && i <= upper;
    }
    return false;
  }
  public void setExpression(String s) {
    if (s.startsWith("[") && s.endsWith("]")) {
      int ix = s.indexOf("..");
      try {
        lower = Integer.parseInt(s.substring(1, ix));
      } catch (NumberFormatException e) {
        lower = Integer.MIN_VALUE;
      }
      try {
        upper = Integer.parseInt(s.substring(ix + 2, s.length() - 1));
      } catch (NumberFormatException e) {
        upper = Integer.MAX_VALUE;
      }
    }
    super.setExpression(s);
  }
  public int getLower() { return lower; }
  public int getUpper() { return upper; }
}
