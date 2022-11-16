package engiutils;

import java.util.Enumeration;
import jakarta.servlet.http.HttpServletRequest;

public class ServletHeaders {
   private String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

   public ServletHeaders(HttpServletRequest request, String rootNodeName) {
      this.xml = this.xml + "<" + rootNodeName + ">";
      Enumeration names = request.getParameterNames();

      while(names.hasMoreElements()) {
         String name = (String)names.nextElement();
         this.addHeader(name, request.getParameter(name));
      }

      this.xml = this.xml + "</" + rootNodeName + ">";
   }

   private void addHeader(String name, String value) {
      name = UTF8Encoder.encode(name);
      value = UTF8Encoder.encode(value);
      this.xml = this.xml + "<" + name + ">" + value + "</" + name + ">";
   }

   public String getXML() {
      return this.xml;
   }
}