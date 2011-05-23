package dk.lockfuglsang.rasmus.util.ui.table.filters;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateFilter extends Filter {
  private DateFormat dateFormat;
  private Date start;
  private Date end;
  
  public DateFilter(DateFormat dateFormat) {
    this.dateFormat = dateFormat;
  }
  public void setRange(Date start, Date end) {
    this.start = new Date(start.getTime());
    this.end = new Date(end.getTime());
  }
  
  public Date getStart() {
    return start;
  }
  
  public Date getEnd() {
    return end;
  }
  
  @Override
  public void setExpression(String s) {
    // Syntax: [dd-MM-yy..dd-MM-yy]
    if (s.startsWith("[") && s.endsWith("]")) {
      int ix = s.indexOf("..");
      try {
        start = dateFormat.parse(s.substring(1, ix));
      } catch (ParseException e) {
        start = null;
      }
      try {
        end = dateFormat.parse(s.substring(ix + 2, s.length() - 1));
      } catch (ParseException e) {
        end = null;
      }
    }
    super.setExpression(s);
  }
  
  @Override
  public boolean match(Object o) {
    if (o instanceof Date) {
      Date d = (Date) o;
      if (start != null && start.after(d)) {
        return false;
      }
      if (end != null && end.before(d)) {
        return false;
      }
      return true;
    }
    return false;
  }
  
}
