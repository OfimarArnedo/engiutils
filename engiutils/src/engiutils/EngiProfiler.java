package engiutils;

import engiutils.EngiProfiler$1;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

public class EngiProfiler {
   private static final String version = "10";
   private final Vector filters;
   private final TreeMap data;
   private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
   private final SimpleDateFormat hourFormat = new SimpleDateFormat();
   private Date lastReset = new Date();
   private final String cfgPath;
   private int maxRows;
   private String baseCsvPath;

   public EngiProfiler(String cfgPath, String baseCsvPath, int maxRows) {
      this.cfgPath = cfgPath;
      this.data = new TreeMap();
      this.filters = new Vector();
      this.maxRows = maxRows;
      File dir = new File(baseCsvPath);
      if (!((File)dir).exists() && !((File)dir).mkdirs()) {
         dir = EFile.createTempDir("engisoft-profiler");
      }

      if (!((File)dir).canWrite()) {
         dir = EFile.createTempDir("engisoft-profiler");
      }

      try {
         this.baseCsvPath = ((File)dir).getCanonicalPath();
      } catch (IOException var6) {
         this.baseCsvPath = null;
         var6.printStackTrace();
      }

      this.loadConfig();
   }

   public boolean ackCsv(String file) {
      File csvFile = new File(this.baseCsvPath + File.separator + file);
      return csvFile.delete();
   }

   public int dumpCurrent() {
      if (this.data.isEmpty()) {
         return 0;
      } else {
         int st = 1;
         String[] headerInfo = this.getHeaderInfo();
         String header = headerInfo[0];

         for(int i = 1; i < headerInfo.length; ++i) {
            header = header + ",";
            header = header + headerInfo[i];
         }

         header = header + "\n";
         String csvFileName = this.baseCsvPath + File.separator + (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date()) + ".csv";
         FileOutputStream csvFile = null;

         try {
            csvFile = new FileOutputStream(csvFileName);
         } catch (FileNotFoundException var18) {
            ErrorContextBuilder.add("EMerlinProfiler.dumpCurrent:Exception", var18);
         }

         try {
            csvFile.write(header.getBytes());
            Iterator it = this.data.values().iterator();

            while(it.hasNext()) {
               InfoNode record = (InfoNode)it.next();
               String dump = record.toCsv();
               dump = dump + "\n";
               csvFile.write(dump.getBytes());
            }
         } catch (IOException var19) {
            ErrorContextBuilder.add("EMerlinProfiler.dumpCurrent:Exception", var19);
         } finally {
            try {
               csvFile.close();
            } catch (IOException var17) {
               ErrorContextBuilder.add("EMerlinProfiler.dumpCurrent:Exception", var17);
            }

            this.resetStats();
         }

         if (ErrorContextBuilder.isActive()) {
            st = -1;
         }

         return st;
      }
   }

   public String getCsvFile(String file) {
      String data = "";
      File csvFile = new File(this.baseCsvPath + File.separator + file);

      try {
         FileInputStream fis = new FileInputStream(csvFile);
         DataInputStream dis = new DataInputStream(fis);
         BufferedReader br = new BufferedReader(new InputStreamReader(dis));

         for(String line = null; (line = br.readLine()) != null; data = data + "\n") {
            data = data + line;
         }

         dis.close();
      } catch (IOException var8) {
         ErrorContextBuilder.add("EmerlinStats.getCsvFile:Exception", var8);
      }

      return data;
   }

   public List getCsvList() {
      File dir = new File(this.baseCsvPath);
      FilenameFilter filter = new EngiProfiler$1(this);
      return Arrays.asList(dir.list(filter));
   }

   private String getHeaderHtmlInfo() {
      String[] info = this.getHeaderInfo();
      String html = "<ul>";

      for(int i = 0; i < InfoNode.infoHeaders.length; ++i) {
         html = html + "<li>";
         html = html + InfoNode.infoHeaders[i];
         html = html + ": ";
         html = html + info[i];
         html = html + "</li>";
      }

      html = "</ul>";
      return html;
   }

   private String[] getHeaderInfo() {
      String[] header = new String[4];
      header[0] = "10";

      try {
         header[1] = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException var6) {
         try {
            byte[] ipAddr = new byte[]{127, 0, 0, 1};
            InetAddress addr = InetAddress.getByAddress(ipAddr);
            header[1] = addr.getHostName();
         } catch (UnknownHostException var5) {
            header[1] = "";
         }
      }

      header[2] = Long.toString(System.currentTimeMillis());
      header[3] = Long.toString(this.lastReset.getTime());
      return header;
   }

   private String getHeaderXmlInfo() {
      String[] info = this.getHeaderInfo();
      String xml = "<info>";

      for(int i = 0; i < InfoNode.infoHeaders.length; ++i) {
         xml = xml + "<";
         xml = xml + InfoNode.infoHeaders[i];
         xml = xml + ">";
         xml = xml + info[i];
         xml = xml + "</";
         xml = xml + InfoNode.infoHeaders[i];
         xml = xml + ">";
      }

      xml = xml + "</info>";
      return xml;
   }

   private FilterRecord getMatchingFilter(String ip, String fc) {
      FilterRecord matching = null;
      Iterator it = this.filters.iterator();

      while(it.hasNext()) {
         FilterRecord filter = (FilterRecord)it.next();
         if (filter.match(ip, fc)) {
            matching = filter;
            break;
         }
      }

      return matching;
   }

   public void hit(EngiRequest req) {
      String ip = null;
      String idfh = null;
      String func = null;
      int client = req.getClientType();
      FilterRecord filter = this.getMatchingFilter(req.getClientIP(), req.getFunctionCall());
      String key = Integer.toString(client);
      String date = this.dateFormat.format(req.getDate());
      key = key + date;
      if (filter != null) {
         idfh = filter.getTimeRange();
         if (filter.timeRangeIsMetachar()) {
            this.hourFormat.applyPattern(idfh);
            idfh = this.hourFormat.format(req.getDate());
         }

         ip = filter.getMatchedIp(req.getClientIP());
         func = filter.getMatchedFunction(req.getFunctionCall());
         key = key + idfh;
         key = key + ip;
         key = key + func;
      }

      synchronized(this.data) {
         InfoNode record = (InfoNode)this.data.get(key);
         if (record == null) {
            record = new InfoNode(client, idfh, ip, func);
         }

         record.hit(req);
         this.data.put(key, record);
         if (this.data.size() >= this.maxRows) {
            this.dumpCurrent();
         }

      }
   }

   private int loadConfig() {
      int loaded = -1;
      File cfg = new File(this.cfgPath);
      try {
         ESFileInputStream fis = new ESFileInputStream(cfg);
         String line = null;
         while((line = fis.readString()) != null) {
            line = line.trim();
            if (!line.startsWith("#") && line.length() > 0) {
               this.filters.add(new FilterRecord(line));
            }
         }
         loaded = 1;
      } catch (FileNotFoundException var5) {
         loaded = 0;
         String out = "WARN [";
	     out = String.valueOf(out) + DateFormat.getDateTimeInstance().format(new Date());
	     out = String.valueOf(out) + "] ";
	     out = String.valueOf(out) + "No existe el fichero de configuracidel profiler: " + cfg.getAbsolutePath();
	     System.out.println(out);
      }

      return loaded;
   }


   public int reloadProfilerConfig() {
      this.dumpCurrent();
      this.filters.clear();
      return this.loadConfig();
   }

   public void resetStats() {
      this.data.clear();
      this.lastReset = new Date();
   }

   public void setMaxRows(int rows) {
      this.maxRows = rows;
      if (this.data.size() >= this.maxRows) {
         this.dumpCurrent();
      }

   }

   public String showCurrent() {
      return this.showCurrent(false);
   }

   public String showCurrent(boolean html) {
      String dump = "";
      if (html) {
         dump = dump + this.getHeaderHtmlInfo();
         dump = dump + "<table border=\"1\"><tr>";

         for(int i = 0; i < InfoNode.rowHeader.length; ++i) {
            dump = dump + "<th>";
            dump = dump + InfoNode.rowHeader[i];
            dump = dump + "</th>";
         }

         dump = dump + "</tr>";
      } else {
         dump = dump + "<profile>";
         dump = dump + this.getHeaderXmlInfo();
         dump = dump + "<records>";
      }

      InfoNode record;
      for(Iterator it = this.data.values().iterator(); it.hasNext(); dump = dump + (html ? record.toHtmlTableRow() : record.toXml())) {
         record = (InfoNode)it.next();
      }

      dump = dump + (html ? "</table>" : "</records></profile>");
      return dump;
   }
}