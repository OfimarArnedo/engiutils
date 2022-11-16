package engiutils;

public class Chrono {
  private long begin;
  
  private long end;
  
  public void start() {
    this.begin = System.currentTimeMillis();
  }
  
  public void stop() {
    this.end = System.currentTimeMillis();
  }
  
  public long getTime() {
    return this.end - this.begin;
  }
  
  public long getMilliseconds() {
    if (this.end == 0L)
      stop(); 
    return this.end - this.begin;
  }
}
