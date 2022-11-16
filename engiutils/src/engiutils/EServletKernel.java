package engiutils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class EServletKernel {
   private static final long serialVersionUID = 4246193683432019311L;
   private static final String ACCESS_ATTR = "EntryActive";
   private static final String CLOUD_ACCESS_ATTR = "CloudEntryActive";
   private static final String CONFIG_FNAME = "closeaccess.conf";
   public static final String EITAPPCFG = "/../eitech/config/eitapp.cfg";
   private static final int XML_VERSION = 1;
   private final ServletContext context;
   private final String configPath;
   private AppAliasData ad;
   private EServletConfig lc;
   private AccessConfig access;
   private EServlet parent;
   private final String hostAddress;
   private String errOp;
   private String errRoot;
   private String servletName;
   // $FF: synthetic field
   static Class class$0;
   // $FF: synthetic field
   static Class class$1;

   public EServletKernel(EServlet parent, String configPath, String errRoot, String errOp, String servletName) {
      this.parent = parent;
      this.context = ((HttpServlet)parent).getServletContext();
      this.configPath = configPath;
      this.errRoot = errRoot;
      this.errOp = errOp;
      this.servletName = servletName;
      this.initWebmoduleAccess();
      this.loadServletConfig();
      this.loadAppData();
      ErrorContextBuilder.init(this.resolveErrorPath("/errors/"));

      String hostAddress;
      try {
         InetAddress addr = InetAddress.getLocalHost();
         hostAddress = addr.getHostAddress();
      } catch (UnknownHostException var8) {
         hostAddress = "127.0.0.1";
         var8.printStackTrace();
      }

      this.hostAddress = hostAddress;
   }

   public String getHostAddress() {
      return this.hostAddress;
   }

   public String getBasePath() {
      return this.context.getRealPath("");
   }

   public String resolveConfigPath(String suffix) {
      return this.resolvePath(this.getBasePath(), suffix, "/\\.\\./eitech/config/", "/etc/");
   }

   public String getWebInfPath() {
      return this.context.getRealPath("/WEB-INF");
   }

   public String resolveCSVPath(String suffix) {
      return this.resolvePath(this.getWebInfPath(), suffix, "/csv/", "/stats/");
   }

   public String resolveErrorPath(String suffix) {
      return this.resolvePath(this.getWebInfPath(), suffix, "/errors/", "/log/");
   }

   private String resolvePath(String prefix, String suffix, String pattern, String replacement) {
      String user = ((HttpServlet)this.parent).getInitParameter("icloud-user");
      return user == null ? prefix + suffix : suffix.replaceFirst(pattern, "/home/" + user + replacement);
   }

   public void loadAppData() {
      this.ad = new AppAliasData(this.resolveConfigPath("/../eitech/config/eitapp.cfg"), this.lc.alias);
   }

   public void loadServletConfig() {
      this.lc = new EServletConfig(this.resolveConfigPath(this.configPath));
   }

   public AppAliasData getAppData() {
      return this.ad;
   }

   public EServletConfig getServletConfig() {
      return this.lc;
   }

   private Boolean getWebmoduleAccessFromContext() {
      String user = ((HttpServlet)this.parent).getInitParameter("icloud-user");
      Boolean value;
      if (user == null) {
         value = (Boolean)this.context.getAttribute("EntryActive");
      } else {
         Map contexts = (Map)this.context.getAttribute("CloudEntryActive");
         if (this.context == null) {
            value = null;
         } else {
            value = (Boolean)contexts.get(user);
         }
      }

      return value;
   }

   private void setWebmoduleAccessToContext(Boolean value) {
      String user = ((HttpServlet)this.parent).getInitParameter("icloud-user");
      if (user == null) {
         this.context.setAttribute("EntryActive", value);
      } else {
         Map contexts = (Map)this.context.getAttribute("CloudEntryActive");
         if (this.context == null) {
            contexts = new HashMap();
         }

         ((Map)contexts).put(user, value);
         this.context.setAttribute("CloudEntryActive", contexts);
      }

   }

   private void initWebmoduleAccess() {
      Boolean value = this.getWebmoduleAccessFromContext();
      this.access = new AccessConfig(this.resolveCloseAccessPath());
      if (value == null) {
         this.setWebmoduleAccessToContext(this.access.active);
      } else {
         this.access.active = value;
      }

   }

   public String resolveCloseAccessPath() {
      return this.resolvePath(this.getBasePath(), "closeaccess.conf", "^", "/run/" + this.context.getServletContextName() + "_");
   }

   public void setWebmoduleAccess(boolean isActive) {
      this.access.active = new Boolean(isActive);
      this.access.writeConfig();
      this.setWebmoduleAccessToContext(this.access.active);
   }

   public boolean isWebmoduleAccessible() {
      Boolean value = this.getWebmoduleAccessFromContext();
      return value;
   }

   public void doConfigWork(HttpServletRequest request, PrintWriter out) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      boolean validParam = this.execConfigMethod(request, out, this.parent);
      if (!validParam) {
         validParam = this.execConfigMethod(request, out, this);
      }

      if (!validParam) {
         this.printEmuleGetError(out);
      }

   }

   private boolean execConfigMethod(HttpServletRequest request, PrintWriter out, Object destination) throws IllegalAccessException, InvocationTargetException {
      boolean validParam = false;
      Set mapKeys = request.getParameterMap().entrySet();
      Iterator it = mapKeys.iterator();

      while(it.hasNext()) {
         Entry entry = (Entry)it.next();
         String name = (String)entry.getKey();
         String methodName = name.substring(0, 1).toLowerCase() + name.substring(1);

         try {
            Class var10000 = destination.getClass();
            Class[] var10002 = new Class[1];
            Class var10005 = class$0;
            if (var10005 == null) {
               try {
                  var10005 = Class.forName("java.lang.String");
               } catch (ClassNotFoundException var15) {
                  throw new NoClassDefFoundError(var15.getMessage());
               }

               class$0 = var10005;
            }

            var10002[0] = var10005;
            Method m = var10000.getMethod(methodName, var10002);
            var10000 = m.getReturnType();
            Class var10001 = class$1;
            if (var10001 == null) {
               try {
                  var10001 = Class.forName("engiutils.EngiAdminAbstractResponse");
               } catch (ClassNotFoundException var14) {
                  throw new NoClassDefFoundError(var14.getMessage());
               }

               class$1 = var10001;
            }

            if (var10000.equals(var10001)) {
               EngiAdminAbstractResponse result = (EngiAdminAbstractResponse)m.invoke(destination, request.getParameter(name));
               boolean raw = true;
               String userAgent = request.getHeader("User-Agent");
               if (userAgent == null || userAgent.toLowerCase().equals("engiadmin")) {
                  raw = false;
               }

               if (request.getParameter("xml") != null) {
                  raw = false;
               }

               result.setRaw(raw);
               if (raw) {
                  this.printStatus(out, result.toString());
               } else {
                  out.write(result.toString());
               }

               validParam = true;
            }
         } catch (NoSuchMethodException var16) {
            validParam = false;
         }
      }

      return validParam;
   }

   public String buildXMLError(int code, String message) {
      return this.buildXMLError(code, message, false);
   }

   public String buildXMLError(int code, String message, boolean isEitech) {
      String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?><";
      if (isEitech) {
         xml = xml + "engisoft_eit><proc_result type=\"error\">";
      } else {
         xml = xml + this.errRoot;
         if (this.errOp != null) {
            xml = xml + " function=\"";
            xml = xml + this.errOp;
            xml = xml + "RS\"";
         }

         xml = xml + " version=\"";
         xml = xml + 1;
         xml = xml + "\">";
      }

      xml = xml + "<ErrorText Error=\"";
      xml = xml + code;
      xml = xml + "\">";
      xml = xml + message;
      if (ErrorContextBuilder.isActive()) {
         xml = xml + "\nError ID: ";
         xml = xml + Long.toString(ErrorContextBuilder.getCurrentErrorId());
      }

      xml = xml + "</ErrorText>";
      if (isEitech) {
         xml = xml + "</proc_result></engisoft_eit>";
      } else {
         xml = xml + "</";
         xml = xml + this.errRoot;
         xml = xml + ">";
      }

      return xml;
   }

   public String buildXMLError(int code, String message, String alias, EngiRequest req) {
      return this.buildXMLError(code, message, req.isEitech());
   }

   public String checkActiveServlets(int code, String doc) {
      String result = "";
      if (!this.isWebmoduleAccessible()) {
         result = this.buildXMLError(code, "Fuera de servicio");
      }

      return result;
   }

   public void outputHTML(PrintWriter out, String message, String title) {
      out.println("<html>");
      out.println("<head>");
      out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">");
      out.println("<title>");
      if (title == null) {
         title = "Estado servlet " + this.servletName;
      }

      out.println(title);
      out.println("</title>");
      out.println("<STYLE><!--H1{font-family : sans-serif,Arial,Tahoma;color : white;background-color : #0086b2;} H2{font-family : sans-serif,Arial,Tahoma;color : white;background-color : #0086b2;} H3{font-family : sans-serif,Arial,Tahoma;color : white;background-color : #0086b2;} body{font-family : sans-serif,Arial,Tahoma;color : black;background-color : white;} B{color : white;background-color : #0086b2;} HR{color : #0086b2;}--></STYLE>");
      out.println("</head>");
      out.println("<body alink=\"out0088\" link=\"#0000ff\" vlink=\"#ff0000\">");
      out.println(message);
      out.println("</body>");
      out.println("</html>");
   }

   public void printEmuleGetError(PrintWriter out) {
      String message = "<h1>HTTP Status 405 - El Metodo HTTP GET no es soportado por esta URL</h1><HR size=\"1\" noshade=\"noshade\"/><p><b>type</b> Status report</p><p><b>message</b> <u>El Metodo HTTP GET no es soportado por esta URL</u></p><p><b>description</b> <u>The specified HTTP method is not allowed for the requested resource (El Metodo HTTP GET no es soportado por esta URL).</u></p><HR size=\"1\" noshade=\"noshade\"/><h3>Apache Tomcat/4.1.30</h3>";
      this.outputHTML(out, message, "Apache Tomcat/4.1.30 - Error report");
   }

   public void printStatus(PrintWriter out, String status) {
      this.printStatus(out, status, true);
   }

   public int outputXMLError(OutputStream os, int code, String message, boolean isEitech) throws IOException {
      return this.outputXMLError(os, code, message, isEitech, (String)null);
   }

   public int outputXMLError(OutputStream os, int code, String message, boolean isEitech, String id) throws IOException {
      String xml = this.buildXMLError(code, message, isEitech);
      if (id != null) {
         ErrorContextBuilder.add(id, xml);
      }

      os.write(xml.getBytes());
      return xml.length();
   }

   public void printStatus(PrintWriter out, String status, boolean forzeBold) {
      String message = "<hr/>";
      message = message + "<H2>Servlet Status: </H2><hr/><br/>";
      if (forzeBold) {
         message = message + "<strong>";
      }

      message = message + status;
      if (forzeBold) {
         message = message + "</strong>";
      }

      this.outputHTML(out, message, (String)null);
   }

   public EngiAdminAbstractResponse status(String data) {
      return new EngiAdminRawResponse(this.parent.getHTMLStatus());
   }

   public EngiAdminAbstractResponse timeout(String newTimeout) {
      int limTimeout = Integer.parseInt(newTimeout);
      this.parent.setTimeout(limTimeout);
      return new EngiAdminOkResponse("Asignado timeout: " + String.valueOf(limTimeout) + "s");
   }

   public EngiAdminAbstractResponse clearcounters(String data) {
      this.parent.clearCounters();
      return new EngiAdminOkResponse("Contadores a cero.");
   }

   public EngiAdminAbstractResponse clearhistory(String data) {
      this.parent.clearHistory();
      return new EngiAdminOkResponse("Historia borrada.");
   }

   public EngiAdminAbstractResponse maxcon(String newNumberMaxCon) {
      int maxcon = Integer.parseInt(newNumberMaxCon);
      this.parent.setMaxcon(maxcon);
      return new EngiAdminOkResponse("Asignado n&uacute;mero m&aacute;ximo de conexiones: " + String.valueOf(maxcon));
   }

   public EngiAdminAbstractResponse reloadApps(String data) {
      Object result = null;

      try {
         this.parent.reloadApps();
         result = new EngiAdminOkResponse("Aplicaciones recargadas.");
      } catch (Exception var4) {
         result = new EngiAdminErrorResponse("Se ha producido un error al recargar las aplicaciones: " + var4.toString());
      }

      return (EngiAdminAbstractResponse)result;
   }

   public EngiAdminAbstractResponse reloadConfig(String data) {
      Object result = null;

      try {
         this.parent.reloadConfig();
         result = new EngiAdminOkResponse("Configuración recargada.");
      } catch (Exception var4) {
         result = new EngiAdminErrorResponse("Se ha producido un error al recargar la configuración: " + var4.toString());
      }

      return (EngiAdminAbstractResponse)result;
   }

   public void setCORS(HttpServletRequest request, HttpServletResponse response) {
      String corsOrigin = this.lc.cors_origin;
      if (corsOrigin != null) {
         response.setHeader("Access-Control-Allow-Origin", corsOrigin);
         response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
         response.setHeader("Access-Control-Allow-Headers", "X-Requested-With");
      }

   }
}