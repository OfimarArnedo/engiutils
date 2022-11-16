package engiutils;

import java.util.Arrays;
import java.util.List;

class FilterRecord {
  private final Filter ip;
  
  private final Filter fc;
  
  private final Cron instant;
  
  private final String fh;
  
  private boolean isMetachar = false;
  
  public FilterRecord(String data) {
    List tmp = Arrays.asList(data.split(";"));
    this.ip = new Filter(tmp.subList(0, 3));
    this.fc = new Filter(tmp.subList(3, 6));
    this.fh = resolveMetacharacter(tmp.get(6).toString());
    this.instant = new Cron(tmp.get(7).toString());
  }
  
  public String getMatchedFunction(String value) {
    return this.fc.getMatchedData(value);
  }
  
  public String getMatchedIp(String value) {
    return this.ip.getMatchedData(value);
  }
  
  public String getTimeRange() {
    return this.fh;
  }
  
  public boolean match(String ipContent, String fcContent) {
    boolean result = false;
    if (this.instant.mayRunNow())
      result = (this.ip.match(ipContent) && this.fc.match(fcContent)); 
    return result;
  }
  
  private String resolveMetacharacter(String fh) {
    if (fh.startsWith("@")) {
      this.isMetachar = true;
      switch (fh.charAt(1)) {
        case 'H':
          fh = "HH0000";
          return fh;
        case 'M':
          fh = "HHmm00";
          return fh;
        case 'S':
          fh = "HHmmss";
          return fh;
      } 
      this.isMetachar = false;
    } 
    return fh;
  }
  
  public boolean timeRangeIsMetachar() {
    return this.isMetachar;
  }
}
