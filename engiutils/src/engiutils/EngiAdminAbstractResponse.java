/* Decompiler 39ms, total 635ms, lines 213 */
package engiutils;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xerces.dom.DeferredTextImpl;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class EngiAdminAbstractResponse {
   private static DocumentBuilderFactory factory = null;
   protected String message;
   private boolean raw = true;
   private boolean dirty = false;
   // $FF: synthetic field
   static Class class$0;

   protected EngiAdminAbstractResponse() {
   }

   public EngiAdminAbstractResponse(String message) {
      this.message = message;
   }

   public static EngiAdminAbstractResponse fromXml(String doc) throws ParserConfigurationException, SAXException, IOException {
      return fromXml(doc, false);
   }

   public static EngiAdminAbstractResponse fromXml(String doc, boolean compat) throws ParserConfigurationException, SAXException, IOException {
      EngiAdminAbstractResponse result = null;
      if (factory == null) {
         factory = DocumentBuilderFactory.newInstance();
         factory.setValidating(false);
         factory.setIgnoringComments(true);
      }

      DocumentBuilder db;
      Document html;
      Element root;
      NodeList nl;
      Node n;
      if (compat) {
         if (doc.startsWith("<html>")) {
            doc = doc.replaceAll("<meta .*>", "<meta />");
            doc = doc.replaceAll("<br *>", "<br />");
            doc = doc.replaceAll("<hr *>", "<hr />");
            doc = doc.replaceAll("<head>", "<head />");
            db = factory.newDocumentBuilder();
            html = db.parse(new InputSource(new StringReader(doc)));
            root = html.getDocumentElement();
            if (root != null) {
               nl = root.getChildNodes();
               n = null;

               int i;
               for(i = 0; i < nl.getLength() && !(n = nl.item(i)).getNodeName().toLowerCase().equals("body"); ++i) {
               }

               if (n != null && n.getNodeName().toLowerCase().equals("body")) {
                  nl = n.getChildNodes();

                  for(i = 0; i < nl.getLength() && !(n = nl.item(i)).getNodeName().toLowerCase().equals("strong"); ++i) {
                  }

                  if (n != null && n.getNodeName().toLowerCase().equals("strong")) {
                     nl = n.getChildNodes();

                     for(i = 0; i < nl.getLength() && !((n = nl.item(i)) instanceof DeferredTextImpl); ++i) {
                     }

                     result = new EngiAdminOkResponse(n.getNodeValue());
                  } else {
                     result = new EngiAdminErrorResponse("Respuesta inv�lida");
                  }
               } else {
                  result = new EngiAdminErrorResponse("Respuesta inv�lida");
               }
            } else {
               result = new EngiAdminErrorResponse("Respuesta inv�lida");
            }
         } else {
            result = new EngiAdminRawResponse(doc);
         }
      } else {
         db = factory.newDocumentBuilder();
         html = db.parse(new InputSource(new StringReader(doc)));
         root = html.getDocumentElement();
         if (root != null) {
            String message;
            if (root.getNodeName().equals("ok")) {
               message = root.getAttribute("msg");
               result = new EngiAdminOkResponse(message);
            } else if (root.getNodeName().equals("err")) {
               message = root.getAttribute("msg");
               result = new EngiAdminErrorResponse(message);
            } else if (root.getNodeName().equals("raw")) {
               nl = root.getChildNodes();
               if (nl.getLength() != 0) {
                  n = nl.item(0);
                  Class var10000 = n.getClass();
                  Class var10001 = class$0;
                  if (var10001 == null) {
                     try {
                        var10001 = Class.forName("org.apache.xerces.dom.DeferredCDATASectionImpl");
                     } catch (ClassNotFoundException var11) {
                        throw new NoClassDefFoundError(var11.getMessage());
                     }

                     class$0 = var10001;
                  }

                  Node aux;
                  if (!var10000.equals(var10001)) {
                     while((aux = n.getNextSibling()) != null) {
                        var10000 = aux.getClass();
                        var10001 = class$0;
                        if (var10001 == null) {
                           try {
                              var10001 = Class.forName("org.apache.xerces.dom.DeferredCDATASectionImpl");
                           } catch (ClassNotFoundException var10) {
                              throw new NoClassDefFoundError(var10.getMessage());
                           }

                           class$0 = var10001;
                        }

                        if (var10000.equals(var10001)) {
                           break;
                        }
                     }
                  } else {
                     aux = n;
                  }

                  if (aux != null) {
                     var10000 = aux.getClass();
                     var10001 = class$0;
                     if (var10001 == null) {
                        try {
                           var10001 = Class.forName("org.apache.xerces.dom.DeferredCDATASectionImpl");
                        } catch (ClassNotFoundException var9) {
                           throw new NoClassDefFoundError(var9.getMessage());
                        }

                        class$0 = var10001;
                     }

                     if (var10000.equals(var10001)) {
                        result = new EngiAdminRawResponse(aux.getNodeValue());
                        return (EngiAdminAbstractResponse)result;
                     }
                  }

                  result = new EngiAdminRawResponse("");
               } else {
                  result = new EngiAdminRawResponse("");
               }
            } else if (root.getNodeName().equals("engisoft_eit")) {
               nl = root.getChildNodes();
               if (nl.getLength() != 0) {
                  n = nl.item(0).getFirstChild();
                  if (n.getNodeName().equals("raw")) {
                     n = n.getFirstChild();
                     result = new EngiAdminRawResponse(n.getNodeValue());
                  } else {
                     result = new EngiAdminOkResponse(n.getNodeValue());
                  }
               }
            }
         } else {
            result = new EngiAdminErrorResponse("Respuesta inv�lida");
         }
      }

      return (EngiAdminAbstractResponse)result;
   }

   public void setRaw(boolean raw) {
      this.raw = raw;
   }

   protected String toRaw() {
      return this.message;
   }

   public String toString() {
      String result;
      if (this.raw) {
         result = this.toRaw();
      } else {
         result = this.toXml();
      }

      return result;
   }

   protected abstract String toXml();

   public void setDirty(boolean isDirty) {
      this.dirty = isDirty;
   }

   public boolean isDirty() {
      return this.dirty;
   }
}