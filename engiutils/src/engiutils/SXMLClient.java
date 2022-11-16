package engiutils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class SXMLClient {
   protected EServletConfig lc;
   protected FDSSocket fs;
   private boolean connected;

   public SXMLClient() {
      this((EServletConfig)null);
   }

   public SXMLClient(EServletConfig lc) {
      this.lc = lc;
      this.connected = false;
   }

   public int login(String IP, int port, String dbsName, String dbsApp, int llMode) throws IOException {
      String buff = "\"" + dbsName + "\",\"" + dbsName + "\",0,1," + Runtime.getRuntime().toString() + ",\"" + dbsApp + "\",9,\"" + System.getProperty("user.name") + "\",1,1";
      return this.login(IP, port, dbsName, dbsApp, buff, llMode, "[OPENDBS]");
   }

   public int login(String IP, int port, String dbsName, String dbsApp, String XMLClientIP, int llMode) throws IOException {
      String buff = "V02|" + this.encodeHLStr(dbsName) + "|" + this.encodeHLStr(dbsApp) + "|";
      buff = buff + this.encodeHLStr(XMLClientIP) + "|" + this.encodeHLStr(System.getProperty("user.name")) + "|";
      buff = buff + "0|0|9|1|1";
      return this.login(IP, port, dbsName, dbsApp, buff, llMode, "[OPENDBS2]");
   }

   protected int login(String IP, int port, String dbsName, String dbsApp, String buff, int llMode, String protocol) throws IOException {
      String rcv = null;
      this.fs = FDSSocket.factory(this.lc, llMode);
      this.customizeSocket();

      try {
         this.fs.acquireSocket(IP, port);
      } catch (IOException var13) {
         ErrorContextBuilder.add("login:Exception", var13);
         ErrorContextBuilder.add("login:FDSCode", new Integer(2004));
         return 2004;
      }

      this.connected = true;
      String snd = this.buildBuffLP("", protocol);
      snd = this.buildBuffLP(snd, buff);

      int st;
      try {
         this.fs.writeSocket(snd);
         rcv = this.fs.receiveLPString();
         st = Integer.parseInt(rcv);
         if (st != 0) {
            ErrorContextBuilder.add("login:message", "Error al realizar login al fdserver (SXMLCLient.login).");
            ErrorContextBuilder.add("login:buff", buff);
            ErrorContextBuilder.add("login:snd", snd);
            ErrorContextBuilder.add("login:rcv", rcv);
            ErrorContextBuilder.add("login:FDSCode", new Integer(st));
         }
      } catch (Throwable var12) {
         st = 2015;
         ErrorContextBuilder.add("login:Exception", var12);
         ErrorContextBuilder.add("login:message", "Error al realizar login al fdserver (SXMLCLient.login).");
         ErrorContextBuilder.add("login:buff", buff);
         ErrorContextBuilder.add("login:snd", snd);
         ErrorContextBuilder.add("login:rcv", rcv);
         ErrorContextBuilder.add("login:FDSCode", new Integer(st));
         this.fs.closeSocket();
      }

      return st;
   }

   protected void customizeSocket() {
   }

   public int logout() throws IOException {
      if (!this.connected) {
         return 0;
      } else {
         String snd = this.buildBuffLP("", "[CLOSE_CONNECTION]");

         int st;
         try {
            this.fs.writeSocket(snd);
            String rcv = this.fs.receiveLPString();
            st = Integer.parseInt(rcv) == 1 ? 0 : -1;
         } catch (Throwable var6) {
            st = 2015;
            ErrorContextBuilder.add("logout:Exception", var6);
            ErrorContextBuilder.add("logout:message", "Error al realizar logout al fdserver (SXMLCLient.logout).");
            ErrorContextBuilder.add("logout:snd", snd);
            ErrorContextBuilder.add("logout:FDSCode", new Integer(st));
         }

         try {
            this.fs.closeSocket();
         } catch (Throwable var5) {
            st = 2015;
            ErrorContextBuilder.add("logout:Exception", var5);
            ErrorContextBuilder.add("logout:message", "Error al realizar closeSocket de logout al fdserver (SXMLCLient.logout).");
            ErrorContextBuilder.add("logout:FDSCode", new Integer(st));
         }

         return st;
      }
   }

   public String execFunc(String xml) throws IOException {
      return this.execFunc(EStringUtils.stringToArray(xml)).toString();
   }

   public ByteArrayOutputStream execFunc(ByteArrayOutputStream xml) throws IOException {
      return this.execFunc(xml, "[EXEC_XML_DOCUMENT]");
   }

   public ByteArrayOutputStream execFunc(ByteArrayOutputStream xml, String operation) throws IOException {
      String rcv = null;
      int len = 0;
      ByteArrayOutputStream result = new ByteArrayOutputStream();
      String snd = this.buildBuffLP("", operation);

      try {
         this.fs.writeSocket(snd);
      } catch (Throwable var10) {
         ErrorContextBuilder.add("execFunc:Exception", var10);
         ErrorContextBuilder.add("execFunc:message", "Error al enviar orden de ejecucion al servidor (SXMLCLient.execfun).");
         ErrorContextBuilder.add("execFunc:snd", snd);
         return result;
      }

      String slen = String.valueOf(xml.size());
      snd = this.buildBuffLP("", slen);

      try {
         this.fs.writeSocket(snd);
         this.fs.writeSocket(xml);
         rcv = this.fs.receiveLPString();
         len = Integer.parseInt(rcv);
      } catch (Throwable var9) {
         ErrorContextBuilder.add("execFunc:Exception", var9);
         ErrorContextBuilder.add("execFunc:message", "Error al enviar documento XML al servidor (SXMLCLient.execfun).");
         ErrorContextBuilder.add("execFunc:snd", snd);
         ErrorContextBuilder.add("execFunc:xml", xml);
         ErrorContextBuilder.add("execFunc:slen", slen);
         ErrorContextBuilder.add("execFunc:rcv", rcv);
         ErrorContextBuilder.add("execFunc:len", new Integer(len));
         return result;
      }

      if (len > 0) {
         byte[] buff = this.fs.readSocket(len);
         result.write(buff, 0, len);
      }

      return result;
   }

   protected String buildBuffLP(String addTo, String s) {
      DecimalFormat df = new DecimalFormat("00000");
      String slen = df.format((long)(s.length() + 1));
      String result = addTo + "L," + slen + "," + s + "\n";
      return result;
   }

   protected String encodeHLStr(String s) {
      String sLen = Integer.toHexString(s.length());
      if (sLen.length() < 2) {
         sLen = "0" + sLen;
      }

      return sLen + s;
   }
}