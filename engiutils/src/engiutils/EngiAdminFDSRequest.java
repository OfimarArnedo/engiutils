package engiutils;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class EngiAdminFDSRequest {
  private final SXMLClient client = new SXMLClient(null);
  
  private String host;
  
  private int port;
  
  private String dbs;
  
  private String fdsapp;
  
  private String responseType;
  
  private boolean loggedIn = false;
  
  public EngiAdminFDSRequest() {
    setHost("127.0.0.1");
    setFDSApp("FDSAPP");
    setResponseType("s");
  }
  
  public void setHost(String host) {
    setHost(host, 7500);
  }
  
  public void setHost(String host, int port) {
    close();
    this.host = host;
    this.port = port;
  }
  
  public void setFDSApp(String fdsapp) {
    close();
    this.fdsapp = fdsapp;
  }
  
  private void close() {
    if (this.loggedIn) {
      try {
        this.client.logout();
      } catch (IOException e) {
        e.printStackTrace();
      } 
      this.loggedIn = false;
    } 
  }
  
  public void setResponseType(String responseType) {
    this.responseType = responseType;
  }
  
  public EngiAdminAbstractResponse exec(String dbs, String library, String function, String[] args) throws IOException, ParserConfigurationException, SAXException {
    if (this.dbs != null && 
      !this.dbs.equals(dbs)) {
      close();
      this.dbs = dbs;
    } 
    if (!this.loggedIn)
      this.client.login(this.host, this.port, dbs, this.fdsapp, 1); 
    if (args == null)
      args = new String[0]; 
    String xmlResponse = this.client.execFunc(
        buildXMLRequest(library, function, args)).trim();
    return EngiAdminAbstractResponse.fromXml(xmlResponse);
  }
  
  private String buildXMLRequest(String library, String function, String[] args) {
    StringBuffer request = new StringBuffer();
    request.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    request.append("<engisoft_eit lang=\"1\">");
    request.append("<proc_xml>");
    request.append("<lib>");
    request.append(library);
    request.append("</lib>");
    request.append("<fun>");
    request.append(function);
    request.append("</fun>");
    request.append("<par>");
    request.append(this.responseType);
    request.append("s</par>");
    if (args.length == 0) {
      request.append("<data/>");
    } else {
      request.append("<data>");
      for (int i = 0; i < args.length; i++) {
        String arg = args[i];
        String argTag = "arg" + (i + 1);
        request.append("<");
        request.append(argTag);
        if (arg != null) {
          request.append(">");
          request.append(arg);
          request.append("<");
          request.append(argTag);
        } 
        request.append("/>");
      } 
      request.append("<data>");
    } 
    request.append("</proc_xml>");
    request.append("</engisoft_eit>");
    return request.toString();
  }
  
  protected void finalize() throws Throwable {
    this.client.logout();
    super.finalize();
  }
}
