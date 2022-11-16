package engiutils;

public class ESemaphore {
  protected int count;
  
  protected int maxCount;
  
  public static final int TIMEOUT = 0;
  
  public static final int SIGNALED = 1;
  
  public static final int ABANDONED = 2;
  
  public ESemaphore(int startCount) {
    this.count = startCount;
    this.maxCount = Integer.MAX_VALUE;
  }
  
  public ESemaphore(int startCount, int maxCount) {
    this.count = startCount;
    this.maxCount = maxCount;
  }
  
  public synchronized void releaseSemaphore() {
    if (this.count < this.maxCount) {
      this.count++;
      notify();
    } 
  }
  
  public int waitSemaphore() {
    return waitSemaphore(0);
  }
  
  public synchronized int waitSemaphore(int timeout) {
    int result;
    if (this.count > 0) {
      this.count--;
      result = 1;
    } else {
      try {
        wait(timeout);
        if (this.count > 0) {
          this.count--;
          result = 1;
        } else {
          result = 0;
        } 
      } catch (InterruptedException ex) {
        result = 2;
      } 
    } 
    return result;
  }
}
