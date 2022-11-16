package engiutils;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Date;

public class EGenericConfig extends EConfig {
  public EGenericConfig(String fName) {
    super(fName);
  }
  
  protected void parse(String line) {
    String[] property = line.split("=", 2);
    try {
      Field f = getClass().getField(property[0].trim());
      set(f, property[1].trim());
    } catch (Exception e) {
      String out = "WARN [";
      out = String.valueOf(out) + DateFormat.getDateTimeInstance().format(new Date());
      out = String.valueOf(out) + "] ";
      out = String.valueOf(out) + "Error mientras cargaba la l'" + line + "' de ";
      out = String.valueOf(out) + getFilePath();
      out = String.valueOf(out) + ": ";
      out = String.valueOf(out) + e.getMessage();
      System.out.println(out);
    } 
  }
  
  private void set(Field f, String rawValue) throws IllegalArgumentException, IllegalAccessException {
    if (f.getType().equals(int.class)) {
      f.setInt(this, Integer.parseInt(rawValue));
    } else if (f.getType().equals(boolean.class)) {
      f.setBoolean(this, Boolean.valueOf(rawValue).booleanValue());
    } else {
      f.set(this, rawValue);
    } 
  }
}
