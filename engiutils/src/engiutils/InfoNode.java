package engiutils;

import java.text.SimpleDateFormat;
import java.util.Date;

class InfoNode {
  protected static final String[] rowHeader = new String[] { 
      "client", "date", "idfh", "ip", "func", "ctime", 
      "mtime", "hits", "etime", "inbytes", 
      "outbytes" };
  
  protected static final String[] infoHeaders = new String[] { "version", "host", "ts", "lstreset" };
  
  private int client;
  
  private String fh;
  
  private String ip;
  
  private String func;
  
  private long ctime;
  
  private long mtime;
  
  private int hits;
  
  private long etime;
  
  private long inbytes;
  
  private long outbytes;
  
  public InfoNode(int client, String fh, String ip, String func) {
    this.client = client;
    this.ctime = this.mtime = System.currentTimeMillis();
    this.hits = 0;
    this.inbytes = this.outbytes = 0L;
    this.fh = fh;
    this.ip = ip;
    this.func = func;
  }
  
  public void hit(EngiRequest req) {
    this.inbytes += req.getRequestSize();
    this.outbytes += req.getResponseSize();
    this.etime += req.getElapsedTime();
    this.mtime = System.currentTimeMillis();
    this.hits++;
  }
  
  private String[] toArray() {
    String[] csvFields = new String[11];
    csvFields[0] = Integer.toString(this.client);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    csvFields[1] = sdf.format(new Date(this.ctime));
    csvFields[2] = this.fh;
    csvFields[3] = this.ip;
    csvFields[4] = this.func;
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    csvFields[5] = timeFormat.format(new Date(this.ctime));
    csvFields[6] = timeFormat.format(new Date(this.mtime));
    csvFields[7] = Integer.toString(this.hits);
    csvFields[8] = Long.toString(this.etime);
    csvFields[9] = Long.toString(this.inbytes);
    csvFields[10] = Long.toString(this.outbytes);
    return csvFields;
  }
  
  public String toCsv() {
    String[] csvFields = toArray();
    String csvLine = csvFields[0];
    for (int i = 1; i < csvFields.length; i++) {
      csvLine = String.valueOf(csvLine) + ",";
      if (csvFields[i] == null)
        csvFields[i] = ""; 
      csvLine = String.valueOf(csvLine) + csvFields[i];
    } 
    return csvLine;
  }
  
  public String toHtmlTableRow() {
    String[] csvFields = toArray();
    String csvLine = "<tr>";
    for (int i = 0; i < csvFields.length; i++) {
      csvLine = String.valueOf(csvLine) + "<td>";
      if (csvFields[i] == null || csvFields[i].equals(""))
        csvFields[i] = "&nbsp;"; 
      csvLine = String.valueOf(csvLine) + csvFields[i];
      csvLine = String.valueOf(csvLine) + "</td>";
    } 
    csvLine = String.valueOf(csvLine) + "</tr>";
    return csvLine;
  }
  
  public String toXml() {
    String xml = "<record>";
    String[] csvFields = toArray();
    for (int i = 0; i < rowHeader.length; i++) {
      xml = String.valueOf(xml) + "<";
      xml = String.valueOf(xml) + rowHeader[i];
      xml = String.valueOf(xml) + ">";
      if (csvFields[i] == null)
        csvFields[i] = ""; 
      xml = String.valueOf(xml) + csvFields[i];
      xml = String.valueOf(xml) + "</";
      xml = String.valueOf(xml) + rowHeader[i];
      xml = String.valueOf(xml) + ">";
    } 
    xml = String.valueOf(xml) + "</record>";
    return xml;
  }
}
