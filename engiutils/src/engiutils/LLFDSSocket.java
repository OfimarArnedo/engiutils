package engiutils;

import java.io.IOException;
import java.net.Socket;

public class LLFDSSocket extends FDSSocket {
  private int maxRetries = 5;
  
  private long sleepBeforeRetry = 1000L;
  
  public LLFDSSocket(int maxRetries, long sleepBeforeRetry) {
    this.maxRetries = maxRetries;
    this.sleepBeforeRetry = sleepBeforeRetry;
  }
  
  protected boolean isPassAllowed() throws IOException {
    byte[] passResponse = readSocket(1);
    return (passResponse[0] == 1);
  }
  
  public void acquireSocket(String IP, int port) throws IOException {
    int retries = 0;
    boolean passAllowed = false;
    do {
      this.sock = new Socket(IP, port);
      this.istrm = this.sock.getInputStream();
      passAllowed = isPassAllowed();
      if (passAllowed)
        continue; 
      this.sock.close();
      this.istrm.close();
      try {
        Thread.sleep(this.sleepBeforeRetry);
      } catch (InterruptedException interruptedException) {}
      retries++;
      if (retries >= this.maxRetries)
        throw new IOException("System busy"); 
    } while (!passAllowed);
    this.ostrm = this.sock.getOutputStream();
  }
}
