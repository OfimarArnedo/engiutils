package engiutils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class Cron {
  protected static final int MINUTESPERHOUR = 60;
  
  protected static final int HOURESPERDAY = 24;
  
  protected static final int DAYSPERWEEK = 7;
  
  protected static final int MONTHSPERYEAR = 12;
  
  protected static final int DAYSPERMONTH = 31;
  
  private HashMap minutes = new HashMap();
  
  private HashMap hours = new HashMap();
  
  private HashMap daysInMonth = new HashMap();
  
  private HashMap monthes = new HashMap();
  
  private HashMap daysInWeek = new HashMap();
  
  public Cron(String expression) {
    String[] params = expression.split(" ");
    this.minutes = parseRangeParam(params[0], 60, 0);
    this.hours = parseRangeParam(params[1], 24, 0);
    this.daysInMonth = parseRangeParam(params[2], 31, 1);
    this.monthes = parseRangeParam(params[3], 12, 0);
    this.daysInWeek = parseRangeParam(params[4], 7, 0);
  }
  
  private static HashMap parseRangeParam(String param, int timelength, int minlength) {
    String[] paramarray;
    if (param.indexOf(",") != -1) {
      paramarray = param.split(",");
    } else {
      paramarray = new String[] { param };
    } 
    StringBuffer rangeitems = new StringBuffer();
    for (int i = 0; i < paramarray.length; i++) {
      if (paramarray[i].indexOf("/") != -1) {
        for (int a = 1; a <= timelength; a++) {
          if (a % 
            Integer.parseInt(paramarray[i]
              .substring(paramarray[i].indexOf("/") + 1)) == 0)
            if (a == timelength) {
              rangeitems.append(String.valueOf(minlength) + ",");
            } else {
              rangeitems.append(String.valueOf(a) + ",");
            }  
        } 
      } else if (paramarray[i].equals("*")) {
        rangeitems.append(fillRange(String.valueOf(minlength) + "-" + timelength));
      } else {
        rangeitems.append(fillRange(paramarray[i]));
      } 
    } 
    String[] values = rangeitems.toString().split(",");
    HashMap result = new HashMap();
    for (int j = 0; j < values.length; j++)
      result.put(Integer.valueOf(values[j]), values[j]); 
    return result;
  }
  
  private static String fillRange(String range) {
    if (range.indexOf("-") == -1)
      return String.valueOf(range) + ","; 
    String[] rangearray = range.split("-");
    StringBuffer result = new StringBuffer();
    int i = Integer.parseInt(rangearray[0]);
    for (; i <= 
      Integer.parseInt(rangearray[1]); i++)
      result.append(String.valueOf(i) + ","); 
    return result.toString();
  }
  
  public boolean mayRunAt(Calendar cal) {
    Integer month = new Integer(cal.get(2));
    Integer day = new Integer(cal.get(5));
    Integer dayOfWeek = new Integer(cal.get(7) - 1);
    Integer hour = new Integer(cal.get(11));
    Integer minute = new Integer(cal.get(12));
    if (this.minutes.get(minute) != null && 
      this.hours.get(hour) != null && 
      this.daysInMonth.get(day) != null && 
      this.monthes.get(month) != null && 
      this.daysInWeek.get(dayOfWeek) != null)
      return true; 
    return false;
  }
  
  public boolean mayRunNow() {
    return mayRunAt(new GregorianCalendar());
  }
}
