package engiutils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;

public class BigBrotherInputStream extends InputStream {
  private final InputStream is;
  
  private final LinkedList data;
  
  private boolean fired;
  
  private LinkedList window;
  
  private int windowHash;
  
  private final int anchorHash;
  
  private XmlMonitoringDataSource ds;
  
  private int windowStart;
  
  private boolean windowStartFound;
  
  public static final byte[] ANCHOR_START = "esprof=\"".getBytes();
  
  public static final byte[] ANCHOR_END = "\"".getBytes();
  
  public BigBrotherInputStream(InputStream is, XmlMonitoringDataSource ds) {
    this.is = is;
    this.data = new LinkedList();
    this.window = new LinkedList();
    this.windowHash = 0;
    this.windowStart = 0;
    this.windowStartFound = false;
    this.fired = true;
    int anchorHash = 0;
    this.ds = ds;
    for (int i = 0; i < ANCHOR_START.length; i++)
      anchorHash += ANCHOR_START[i]; 
    this.anchorHash = anchorHash;
  }
  
  public int read() throws IOException {
    int c = this.is.read();
    if (this.fired)
      if (this.windowStartFound) {
        if ((byte)c == ANCHOR_END[0]) {
          this.fired = false;
          byte[] byteValue = new byte[this.data.size()];
          for (int j = 0; j < this.data.size(); j++)
            byteValue[j] = ((Integer)this.data.get(j)).byteValue(); 
          String esprofValue = new String(byteValue, "UTF-8");
          this.ds.setRawFDSValue(esprofValue);
          this.ds.setWindowData(this.windowStart, this.data.size() + 1);
        } else {
          this.data.add(new Integer(c));
        } 
      } else {
        this.window.add(new Integer(c));
        this.windowHash += c;
        this.windowStart++;
        while (this.window.size() > ANCHOR_START.length) {
          int e = ((Integer)this.window.removeFirst()).intValue();
          this.windowHash -= e;
        } 
        this.windowStartFound = windowHasAnchor();
      }  
    return c;
  }
  
  private boolean windowHasAnchor() {
    int i = 0;
    if (this.windowHash != this.anchorHash)
      return false; 
    for (Iterator it = this.window.iterator(); it.hasNext(); i++) {
      if (((Integer)it.next()).byteValue() != ANCHOR_START[i])
        return false; 
    } 
    return true;
  }
}
