package engiutils;

import java.io.IOException;
import java.io.InputStream;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EXMLUtils {
  public static Document getXMLFromStream(InputStream is) {
    DOMParser parser = new DOMParser();
    try {
      parser.parse(new InputSource(is));
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (SAXException ex) {
      ex.printStackTrace();
    } 
    Document doc = parser.getDocument();
    return doc;
  }
}
