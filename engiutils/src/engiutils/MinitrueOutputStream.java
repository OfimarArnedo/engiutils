package engiutils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;

public class MinitrueOutputStream extends OutputStream {
  private final OutputStream os;
  
  private LinkedList window;
  
  private int windowHash;
  
  private boolean fired;
  
  private final int anchorHash;
  
  private boolean skip;
  
  public MinitrueOutputStream(OutputStream os) {
    this.os = os;
    this.window = new LinkedList();
    this.windowHash = 0;
    this.fired = true;
    this.skip = false;
    int anchorHash = 0;
    for (int i = 0; i < BigBrotherInputStream.ANCHOR_START.length; i++)
      anchorHash += BigBrotherInputStream.ANCHOR_START[i]; 
    this.anchorHash = anchorHash;
  }
  
  public void write(byte[] b) throws IOException {
    write(b, 0, b.length);
  }
  
  public void write(byte[] b, int off, int length) throws IOException {
    for (int i = 0; i < length; i++)
      write(b[i]); 
  }
  
  public void write(int c) throws IOException {
    if (this.fired) {
      if (this.skip) {
        if ((char)c == BigBrotherInputStream.ANCHOR_END[0]) {
          this.fired = false;
          this.skip = false;
        } 
      } else {
        this.window.add(new Integer(c));
        this.windowHash += c;
        if (this.window.size() == BigBrotherInputStream.ANCHOR_START.length)
          if (windowHasAnchor()) {
            this.skip = true;
            this.window.clear();
          } else {
            int first = ((Integer)this.window.removeFirst()).intValue();
            this.windowHash -= first;
            this.os.write(first);
          }  
      } 
    } else {
      this.os.write(c);
    } 
  }
  
  private boolean windowHasAnchor() {
    int i = 0;
    if (this.windowHash != this.anchorHash)
      return false; 
    for (Iterator it = this.window.iterator(); it.hasNext(); i++) {
      if (((Integer)it.next()).byteValue() != BigBrotherInputStream.ANCHOR_START[i])
        return false; 
    } 
    return true;
  }
}
