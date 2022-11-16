package engiutils;

import java.text.DecimalFormat;

public class GETStatusServlet {
  private static String getMaxConnStr(int maxCon) {
    String result;
    if (maxCon < 0) {
      result = "(Cerrado)";
    } else if (maxCon == 0) {
      result = "(Ilimitado)";
    } else {
      result = String.valueOf(maxCon);
    } 
    return result;
  }
  
  public static String getHTML(ELimiter lim, int maxCon, int timeOut) {
    String s = "<table border=\"1\" bordercolor=\"#0086b2\" cellspacing=\"0\" width=\"38%\" align=\"left\">";
    s = String.valueOf(s) + "<tr><td nowrap=\"nowrap\"><strong>N&uacute;mero m&aacute;ximo de conexiones:</strong></td><td align=\"right\">" + 
      getMaxConnStr(maxCon) + "</td></tr>";
    s = String.valueOf(s) + "<tr><td nowrap=\"nowrap\"><strong>Timeout:</strong></td><td align=\"right\">" + 
      String.valueOf(timeOut) + "</td></tr>";
    s = String.valueOf(s) + "<tr><td colspan=\"2\"><br></td></tr>";
    s = String.valueOf(s) + "<tr><td nowrap=\"nowrap\"><strong>Conexiones activas:</strong></td><td align=\"right\">" + 
      String.valueOf(lim.getCount()) + "</td></tr>";
    s = String.valueOf(s) + "<tr><td nowrap=\"nowrap\"><strong>Conexiones en cola:</strong></td><td align=\"right\">" + 
      String.valueOf(lim.getQueueLen()) + "</td></tr>";
    s = String.valueOf(s) + "<tr><td colspan=\"2\"><br></td></tr>";
    s = String.valueOf(s) + "<tr><td nowrap=\"nowrap\"><strong>Contador de conexiones recibidas:</strong></td><td align=\"right\">" + 
      String.valueOf(lim.getRequestCount()) + "</td></tr>";
    s = String.valueOf(s) + "<tr><td nowrap=\"nowrap\"><strong>Contador de conexiones aceptadas:</strong></td><td align=\"right\">" + 
      getPropValue(lim.getSignaledCount(), lim.getRequestCount()) + 
      "</td></tr>";
    s = String.valueOf(s) + "<tr><td nowrap=\"nowrap\"><strong>Contador de conexiones encoladas:</strong></td><td align=\"right\">" + 
      getPropValue(lim.getQueueCount(), lim.getRequestCount()) + 
      "</td></tr>";
    s = String.valueOf(s) + "<tr><td nowrap=\"nowrap\"><strong>Contador de conexiones timedout:</strong></td><td align=\"right\">" + 
      getPropValue(lim.getTimedOut(), lim.getRequestCount()) + 
      "</td></tr>";
    s = String.valueOf(s) + "<tr><td nowrap=\"nowrap\"><strong>Contador de conexiones abandoned:</strong></td><td align=\"right\">" + 
      getPropValue(lim.getAbandonedCount(), lim.getRequestCount()) + 
      "</td></tr>";
    s = String.valueOf(s) + "</table>";
    return s;
  }
  
  public static String getPropValue(int amount, int total) {
    double p;
    if (total > 0) {
      p = amount * 100.0D / total;
    } else {
      p = 0.0D;
    } 
    DecimalFormat df = new DecimalFormat("##0.00");
    df.format(p);
    return String.valueOf(String.valueOf(amount)) + " (" + df.format(p) + "%)";
  }
}
