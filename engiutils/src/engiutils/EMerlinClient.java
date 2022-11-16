package engiutils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EMerlinClient extends SXMLClient {
  public static final String DEFAULT_EMERLIN_LOGIN_FUNC = "eLogin";
  
  public static final String DEFAULT_EMERLIN_LOGIN_LIB = "lemerlin";
  
  private final String parentServlet;
  
  public EMerlinClient(String servlet) {
    this.parentServlet = servlet;
  }
  
  public EMerlinClient(String servlet, EServletConfig lc) {
    super(lc);
    this.parentServlet = servlet;
  }
  
  public ByteArrayOutputStream execFunc(ByteArrayOutputStream xml) throws IOException {
    return execFunc(xml, "[EXEC_EMERLIN]");
  }
  
  public ByteArrayOutputStream execLogin(String lib, String func, String vcd, String idMc, String idCnx, String idSession, String servletName) throws IOException {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    String snd = buildBuffLP("", "[LOGIN_EMERLIN]");
    int len = 0;
    if (lib == null)
      lib = "lemerlin"; 
    if (func == null)
      func = "eLogin"; 
    String buff = "V01|";
    buff = String.valueOf(buff) + encodeHLStr(lib);
    buff = String.valueOf(buff) + "|";
    buff = String.valueOf(buff) + encodeHLStr(func);
    buff = String.valueOf(buff) + "|";
    buff = String.valueOf(buff) + encodeHLStr(vcd);
    buff = String.valueOf(buff) + "|";
    buff = String.valueOf(buff) + encodeHLStr(idMc);
    buff = String.valueOf(buff) + "|";
    buff = String.valueOf(buff) + encodeHLStr(idCnx);
    buff = String.valueOf(buff) + "|";
    if (idSession == null)
      idSession = "session:" + servletName; 
    buff = String.valueOf(buff) + encodeHLStr(idSession);
    buff = String.valueOf(buff) + "|";
    buff = String.valueOf(buff) + encodeHLStr(servletName);
    snd = buildBuffLP(snd, buff);
    try {
      this.fs.writeSocket(snd);
      String rcv = this.fs.receiveLPString();
      len = Integer.parseInt(rcv);
    } catch (Throwable e) {
      ErrorContextBuilder.add("execLogin:Exception", e);
      ErrorContextBuilder.add("execLogin:message", 
          "Error al enviar orden de ejecucion al servidor (" + 
          this.parentServlet + ".execLogin).");
      return result;
    } 
    if (len > 0) {
      byte[] answer = this.fs.readSocket(len);
      result.write(answer, 0, len);
    } 
    return result;
  }
}
