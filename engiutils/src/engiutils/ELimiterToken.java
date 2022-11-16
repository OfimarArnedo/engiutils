package engiutils;

public class ELimiterToken {
  private int limiterStatus;
  
  private boolean pendingNotify;
  
  public ELimiterToken(int limiterStatus, boolean pendingNotify) {
    this.limiterStatus = limiterStatus;
    this.pendingNotify = pendingNotify;
  }
  
  public int getLimiterStatus() {
    return this.limiterStatus;
  }
  
  public boolean getPendingNotify() {
    return this.pendingNotify;
  }
}
