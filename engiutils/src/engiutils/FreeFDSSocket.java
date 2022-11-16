package engiutils;

import java.io.IOException;
import java.net.Socket;

public class FreeFDSSocket extends FDSSocket {
  public void acquireSocket(String IP, int port) throws IOException {
    this.sock = new Socket(IP, port);
    this.istrm = this.sock.getInputStream();
    this.ostrm = this.sock.getOutputStream();
  }
}
