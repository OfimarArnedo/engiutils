package engiutils.test;

import engiutils.EMerlinClient;
import engiutils.EngiAdminAbstractResponse;
import engiutils.EngiAdminFDSRequest;
import engiutils.ErrorContextBuilder;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.TestCase;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class EMerlinClientTest extends TestCase {
   private static final String DBS_APP = "FDSAPP";
   private static final String HOST = "localhost";
   private static final String FUNCTION = "test";
   private static final String DBS_LIB = "lxmlusr";
   private static final String DBS_NAME = "PLAY";

   public void testBug2015XmlExec() throws IOException, ParserConfigurationException, SAXException {
      EngiAdminFDSRequest ec = new EngiAdminFDSRequest();
      ec.setFDSApp("FDSAPP");
      ec.setHost("localhost");
      ec.setResponseType("s");
      String[] args = new String[0];

      try {
         EngiAdminAbstractResponse response = ec.exec("PLAY", "lxmlusr", "test", args);
         assertTrue(!ErrorContextBuilder.isActive());
         assertEquals("test", response.toString());
      } catch (Exception var4) {
         assertTrue(var4 instanceof SAXParseException);
      }

   }

   public void testBug2015EmerlinExec() throws IOException {
      EMerlinClient ec = new EMerlinClient("test");
      ec.login("localhost", 7500, "PLAY", "FDSAPP", 1);
      ec.execFunc("<frak><yeah /></frak>");
      ec.logout();
   }
}