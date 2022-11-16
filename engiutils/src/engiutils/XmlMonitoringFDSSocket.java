package engiutils;

import java.io.IOException;
import java.net.Socket;

public class XmlMonitoringFDSSocket extends FDSSocket {
   private XmlMonitoringDataSource ds;

   public void setDataSource(XmlMonitoringDataSource ds) {
      this.ds = ds;
   }

   public void acquireSocket(String IP, int port) throws IOException {
      this.sock = new Socket(IP, port);
      this.istrm = new BigBrotherInputStream(this.sock.getInputStream(), this.ds);
      this.ostrm = this.sock.getOutputStream();
   }
}