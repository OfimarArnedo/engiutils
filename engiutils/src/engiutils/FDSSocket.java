package engiutils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public abstract class FDSSocket {
  protected Socket sock = null;
  
  protected InputStream istrm = null;
  
  protected OutputStream ostrm = null;
  
  private static final byte[] fdserverDiedSampleResponse = new byte[8];
  
  public abstract void acquireSocket(String paramString, int paramInt) throws IOException;
  
  public static FDSSocket factory(EServletConfig lc, int llMode) {
    if (llMode == 2) {
      if (lc == null)
        return new LLFDSSocket(10, 1000L); 
      return new LLFDSSocket(lc.ll_maxretries, lc.ll_sleep);
    } 
    if (lc != null && 
      lc.xml_monitoring_enabled)
      return new XmlMonitoringFDSSocket(); 
    return new FreeFDSSocket();
  }
  
  public byte[] readSocket(int len) throws IOException {
    int rlen;
    byte[] b = new byte[len];
    int i = 0;
    do {
      rlen = this.istrm.read(b, i, len);
      len -= rlen;
      i += rlen;
    } while (rlen >= 0 && len > 0);
    return b;
  }
  
  public String receiveLPString() throws IOException {
    byte[] blen = readSocket(8);
    if (blen[0] == 0 && blen[blen.length - 1] == 0 && 
      Arrays.equals(blen, fdserverDiedSampleResponse))
      throw new IOException("FDServer finaliz"); 
    String slen = new String(blen, 2, 5);
    int len = Integer.parseInt(slen);
    byte[] buff = readSocket(len);
    String bdata = new String(buff, 0, len - 1);
    return bdata;
  }
  
  public void writeSocket(String s) throws IOException {
    this.ostrm.write(s.getBytes());
    this.ostrm.flush();
  }
  
  protected void writeSocket(ByteArrayOutputStream s) throws IOException {
    s.writeTo(this.ostrm);
    this.ostrm.flush();
  }
  
  protected void closeSocket() throws IOException {
    if (this.istrm != null)
      this.istrm.close(); 
    if (this.ostrm != null)
      this.ostrm.close(); 
    if (this.sock != null && 
      !this.sock.isClosed())
      this.sock.close(); 
  }
}
