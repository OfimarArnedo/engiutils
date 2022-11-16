package engiutils;

public class ELimiter {
  public static final int TIMEOUT = 0;
  
  public static final int SIGNALED = 1;
  
  public static final int ABANDONED = 2;
  
  public static final int CLOSED = 3;
  
  protected int maxValue;
  
  protected int count;
  
  protected int queueLen;
  
  protected int queueCount;
  
  protected int timedOut;
  
  protected int signaledCount;
  
  protected int abandonedCount;
  
  protected int requestCount;
  
  public ELimiter(int maxValue) {
    this.maxValue = maxValue;
    this.count = 0;
    this.queueLen = 0;
    this.queueCount = 0;
    this.timedOut = 0;
    this.signaledCount = 0;
    this.abandonedCount = 0;
    this.requestCount = 0;
  }
  
  public synchronized void releaseLimiter(ELimiterToken token) {
    if (token.getLimiterStatus() == 1)
      this.count--; 
    if (token.getPendingNotify())
      notify(); 
  }
  
  public synchronized ELimiterToken waitLimiter(int timeout) {
    ELimiterToken result;
    this.requestCount++;
    if (this.maxValue > 0) {
      if (this.count < this.maxValue) {
        this.count++;
        this.signaledCount++;
        result = new ELimiterToken(1, true);
      } else {
        try {
          this.queueCount++;
          this.queueLen++;
          int entryMaxValue = this.maxValue;
          try {
            wait(timeout);
          } finally {
            this.queueLen--;
          } 
          if ((this.maxValue <= entryMaxValue && this.count < entryMaxValue) || (
            this.maxValue > entryMaxValue && this.count < this.maxValue)) {
            this.count++;
            this.signaledCount++;
            result = new ELimiterToken(1, true);
          } else {
            this.timedOut++;
            result = new ELimiterToken(0, false);
          } 
        } catch (InterruptedException ex) {
          this.abandonedCount++;
          result = new ELimiterToken(2, false);
        } 
      } 
    } else if (this.maxValue < 0) {
      result = new ELimiterToken(3, false);
    } else {
      this.count++;
      this.signaledCount++;
      result = new ELimiterToken(1, false);
    } 
    return result;
  }
  
  public void setMaxValue(int maxValue) {
    this.maxValue = maxValue;
  }
  
  public int getCount() {
    return this.count;
  }
  
  public int getQueueLen() {
    return this.queueLen;
  }
  
  public int getQueueCount() {
    return this.queueCount;
  }
  
  public int getTimedOut() {
    return this.timedOut;
  }
  
  public int getSignaledCount() {
    return this.signaledCount;
  }
  
  public int getAbandonedCount() {
    return this.abandonedCount;
  }
  
  public int getRequestCount() {
    return this.requestCount;
  }
  
  public void clearCounters() {
    this.count = 0;
    this.queueLen = 0;
  }
  
  public void clearHistory() {
    this.queueCount = 0;
    this.timedOut = 0;
    this.signaledCount = 0;
    this.abandonedCount = 0;
    this.requestCount = 0;
  }
}
