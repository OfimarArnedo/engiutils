package engiutils.test;

import engiutils.AppAliasData;
import junit.framework.TestCase;

public class AppAliasDataTest extends TestCase {
   private String home = System.getProperty("PROGSJAVA_HOME", "C:/ProgsJava") + "/nhcrs/eitech";

   public void testGetDefaultAlias() {
      AppAliasData ad = new AppAliasData(this.home + "/../eitech/config/eitapp.cfg");
      assertEquals("NHENTRY", ad.getDefaultAlias());
      ad = new AppAliasData(this.home + "/../eitech/config/eitapp.cfg", "PLAY");
      assertEquals("PLAY", ad.getDefaultAlias());
   }

   private void assertUnknownAlias(AppAliasData ad) {
      ad.getAliasConfig("PLAY");
      assertEquals("127.0.0.1", ad.ip);
      assertEquals(7500, ad.port);
      assertEquals("PLAY", ad.appName);
      assertEquals("FDSAPP", ad.fdsApp);
      assertEquals(1, ad.loadLimitMode);
   }

   private void assertKnownAlias(AppAliasData ad) {
      ad.getAliasConfig("prova");
      assertEquals("192.168.30.68", ad.ip);
      assertEquals(7501, ad.port);
      assertEquals("lprova", ad.appName);
      assertEquals("FDSAPP2", ad.fdsApp);
      assertEquals(2, ad.loadLimitMode);
   }

   public void testGetAliasConfig() {
      AppAliasData ad = new AppAliasData(this.home + "/../eitech/config/eitapp.cfg");
      this.assertUnknownAlias(ad);
      this.assertKnownAlias(ad);
      this.assertUnknownAlias(ad);
      this.assertKnownAlias(ad);
   }
}