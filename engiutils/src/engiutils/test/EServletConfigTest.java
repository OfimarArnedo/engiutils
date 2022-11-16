package engiutils.test;

import engiutils.EServletConfig;
import junit.framework.TestCase;

public class EServletConfigTest extends TestCase {
   public void testEServletConfig() {
      String home = System.getProperty("PROGSJAVA_HOME", "C:/ProgsJava");
      String examplePath = home + "/nhcrs/eitech/config/emerlin.cfg";
      EServletConfig lc = new EServletConfig(examplePath);
      assertEquals(1, lc.maxcon);
      assertEquals(60, lc.timeout);
      assertEquals("_DEFAULT", lc.alias);
      assertEquals("NHENTRY", lc.login_alias);
      assertEquals("lelogin", lc.login_lib);
      assertEquals("loginUser", lc.login_func);
      assertEquals("NHENTRYMR", lc.login2_alias);
      assertEquals("lelogin2", lc.login2_lib);
      assertEquals("loginUserV2", lc.login2_func);
      assertEquals("<sin usar = pero no menos util>", lc.em_applst_path);
      assertEquals(true, lc.profiler_enabled);
      assertEquals(2048, lc.profiler_maxrows);
      assertEquals(2, lc.ll_mode);
      assertEquals(20, lc.ll_maxretries);
      assertEquals(500, lc.ll_sleep);
   }
}