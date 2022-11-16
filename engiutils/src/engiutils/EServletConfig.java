package engiutils;

public class EServletConfig extends EGenericConfig {
  public int maxcon;
  
  public int timeout;
  
  public String alias;
  
  public String login_alias;
  
  public String login_lib;
  
  public String login_func;
  
  public String login2_alias;
  
  public String login2_lib;
  
  public String login2_func;
  
  public String em_applst_path;
  
  public boolean profiler_enabled;
  
  public int profiler_maxrows;
  
  public int ll_mode;
  
  public int ll_maxretries;
  
  public int ll_sleep;
  
  public boolean xml_monitoring_enabled = true;
  
  public String identifier;
  
  public boolean eitechv4;
  
  public String cors_origin;
  
  public EServletConfig(String fName) {
    super(fName);
  }
}
