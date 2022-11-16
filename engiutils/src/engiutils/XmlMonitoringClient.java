package engiutils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlMonitoringClient extends SXMLClient implements XmlMonitoringDataSource {
   private final long timestampRequest;
   private long timestampResponse;
   private String rawFDSvalue;
   private String transactionStatus;
   private int engiResult;
   private long queuedTime;
   private long fdsExecTime;
   private int windowStart;
   private int windowSize;
   // $FF: synthetic field
   static Class class$0;

   public XmlMonitoringClient() {
      this((EServletConfig)null, System.currentTimeMillis());
   }

   public XmlMonitoringClient(EServletConfig lc, long timestampRequest) {
      super(lc);
      this.transactionStatus = "OK";
      this.engiResult = 0;
      this.windowStart = -1;
      this.windowSize = -1;
      this.timestampRequest = timestampRequest;
   }

   protected void customizeSocket() {
      Class var10000 = this.fs.getClass();
      Class var10001 = class$0;
      if (var10001 == null) {
         try {
            var10001 = Class.forName("engiutils.XmlMonitoringFDSSocket");
         } catch (ClassNotFoundException var2) {
            throw new NoClassDefFoundError(var2.getMessage());
         }

         class$0 = var10001;
      }

      if (var10000.equals(var10001)) {
         XmlMonitoringFDSSocket xms = (XmlMonitoringFDSSocket)this.fs;
         xms.setDataSource(this);
      }

   }

   public void setRawFDSValue(String value) {
      this.rawFDSvalue = value;
   }

   public void setAsFailed(int result, ByteArrayOutputStream doc) {
      if (this.lc.xml_monitoring_enabled) {
         this.transactionStatus = "KO";
         this.engiResult = result;
         if (this.rawFDSvalue == null) {
            if (doc == null) {
               this.rawFDSvalue = "0,unkown,0";
            } else {
               DOMParser parser = new DOMParser();
               String transId = null;
               int operation = 0;
               String requestorId = null;

               try {
                  parser.parse(new InputSource(new ByteArrayInputStream(doc.toByteArray())));
                  Document xml = parser.getDocument();
                  Node root = xml.getFirstChild();
                  operation = this.resolveOTAOperation(root.getNodeName());
                  requestorId = this.getRequestorId(root);
                  transId = this.getTransactionIdentifier(root);
               } catch (SAXException var13) {
               } catch (IOException var14) {
               } finally {
                  if (transId == null) {
                     transId = "0";
                  }

                  if (requestorId == null) {
                     requestorId = "unknown";
                  }

                  this.rawFDSvalue = transId + "," + requestorId + "," + Integer.toString(operation);
               }
            }
         }

      }
   }

   private String getRequestorId(Node root) {
      String requestorId = null;
      NodeList nodes = root.getChildNodes();

      for(int i = 0; i < nodes.getLength(); ++i) {
         Node rootChild = nodes.item(i);
         if (rootChild.getNodeName().equals("POS")) {
            NodeList posNodes = rootChild.getChildNodes();

            for(int j = 0; j < posNodes.getLength(); ++j) {
               Node posChild = posNodes.item(j);
               if (posChild.getNodeName().equals("Source")) {
                  NodeList sourceNodes = posChild.getChildNodes();

                  for(int k = 0; k < sourceNodes.getLength(); ++k) {
                     Node sourceChild = sourceNodes.item(k);
                     if (sourceChild.getNodeName().equals("RequestorID")) {
                        NamedNodeMap attrs = sourceChild.getAttributes();
                        requestorId = attrs.getNamedItem("ID").getNodeValue();
                        return requestorId;
                     }
                  }

                  return requestorId;
               }
            }

            return requestorId;
         }
      }

      return requestorId;
   }

   private String getTransactionIdentifier(Node root) {
      NamedNodeMap attrs = root.getAttributes();
      String transId = attrs.getNamedItem("TransactionIdentifier").getNodeValue();
      return transId;
   }

   private int resolveOTAOperation(String nodeName) {
      int operation = -1;
      if (nodeName.equals("OTA_HotelSearchRQ")) {
         operation = 603;
      }

      if (nodeName.equals("OTA_HotelAvailRQ")) {
         operation = 604;
      }

      if (nodeName.equals("OTA_HotelResRQ")) {
         operation = 606;
      }

      if (nodeName.equals("OTA_CancelRQ")) {
         operation = 609;
      }

      if (nodeName.equals("OTA_HotelResModifyRQ")) {
         operation = 610;
      }

      if (nodeName.equals("OTA_HotelDescriptiveInfoRQ")) {
         operation = 611;
      }

      if (nodeName.equals("OTA_HotelResModifyNotifRQ")) {
         operation = 614;
      }

      if (nodeName.equals("OTA_MultiAvailRQ")) {
         operation = 615;
      }

      if (nodeName.equals("OTA_SingleAvailRQ")) {
         operation = 616;
      }

      if (nodeName.equals("OTA_PricingRQ")) {
         operation = 617;
      }

      return operation;
   }

   public void commit(String entry, List data) {
      if (entry == null) {
         entry = this.lc.identifier;
      }

      if (this.anchorFound() && this.lc.xml_monitoring_enabled) {
         SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSS'Z'");
         sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
         this.timestampResponse = System.currentTimeMillis();
         long ellapsedTime = this.timestampResponse - this.timestampRequest;
         String stReq = sdf.format(new Date(this.timestampRequest));
         String stResp = sdf.format(new Date(this.timestampResponse));
         String[] fields = this.rawFDSvalue.split(",");
         String csv = fields[0] + "," + fields[1] + "," + entry + ",";
         csv = csv + stReq + "," + stResp + ",";
         csv = csv + this.transactionStatus + "," + this.engiResult + "," + Long.toString(ellapsedTime) + ",";
         csv = csv + Long.toString(this.fdsExecTime) + "," + Long.toString(this.queuedTime) + ",";
         csv = csv + fields[2];
         int missingFields = 5;

         int i;
         for(i = 3; i < fields.length; ++i) {
            csv = csv + "," + fields[i];
            --missingFields;
         }

         for(i = 0; i < missingFields; ++i) {
            csv = csv + ",";
         }

         data.add(csv);
      }

   }

   public ByteArrayOutputStream execFunc(ByteArrayOutputStream xml, String operation) throws IOException {
      long timestampFdsStart = System.currentTimeMillis();
      ByteArrayOutputStream result = super.execFunc(xml, operation);
      this.fdsExecTime = System.currentTimeMillis() - timestampFdsStart;
      return result;
   }

   public void setQueuedTime(long queuedTime) {
      this.queuedTime = queuedTime;
   }

   public void setWindowData(int start, int size) {
      this.windowStart = start;
      this.windowSize = size;
   }

   public boolean anchorFound() {
      return this.windowStart != -1 && this.windowSize != -1;
   }

   public int getWindowStart() {
      return this.windowStart;
   }

   public int getWindowSize() {
      return this.windowSize;
   }
}