package engiutils;

import java.util.HashMap;

public class AppAliasData extends EConfig {
  public String ip;
  
  public int port;
  
  public String appName;
  
  public String fdsApp;
  
  public int loadLimitMode;
  
  private static final String DEFAULT_ALIAS = "NHENTRY";
  
  private final String defAlias;
  
  private HashMap apps;
  
  private final EntryData defaultApp;
  
  private final EntryData notFoundApp;
  
  private static final int LL_DEFAULT_MODE = 1;
  
  public AppAliasData(String path) {
    this(path, null);
  }
  
  public AppAliasData(String path, String defAlias) {
    super(path);
    if (defAlias == null || defAlias.length() == 0) {
      this.defAlias = "NHENTRY";
    } else {
      this.defAlias = defAlias;
    } 
    this.notFoundApp = new EntryData();
    this.notFoundApp.ip = "127.0.0.1";
    this.notFoundApp.port = 7500;
    this.notFoundApp.fdsApp = "FDSAPP";
    this.notFoundApp.loadLimitMode = 1;
    EntryData dA = (EntryData)this.apps.get(getDefaultAlias());
    if (dA == null) {
      this.defaultApp = new EntryData();
      this.defaultApp.fdsApp = this.notFoundApp.fdsApp;
      this.defaultApp.ip = this.notFoundApp.ip;
      this.defaultApp.loadLimitMode = this.notFoundApp.loadLimitMode;
      this.defaultApp.port = this.notFoundApp.port;
      this.defaultApp.alias = getDefaultAlias();
      this.defaultApp.appName = getDefaultAlias();
    } else {
      this.defaultApp = dA;
    } 
  }
  
  public String getDefaultAlias() {
    return this.defAlias;
  }
  
  private void setApp(EntryData ed) {
    this.ip = ed.ip;
    this.port = ed.port;
    this.appName = ed.appName;
    this.fdsApp = ed.fdsApp;
    this.loadLimitMode = ed.loadLimitMode;
  }
  
  public int getAliasConfig(String alias) {
    if (alias == null) {
      setApp(this.defaultApp);
    } else {
      EntryData ed = (EntryData)this.apps.get(alias);
      if (ed == null) {
        setApp(this.notFoundApp);
        this.appName = alias;
      } else {
        setApp(ed);
      } 
    } 
    return 0;
  }
  
  protected void init() {
    this.apps = new HashMap();
  }
  
  protected void parse(String line) {
    EntryData ed = new EntryData();
    String[] data = line.replaceAll("\\s", "").split(",");
    ed.alias = data[0];
    ed.ip = data[1];
    ed.port = Integer.parseInt(data[2]);
    ed.fdsApp = data[3];
    ed.appName = data[4];
    if (data.length >= 6) {
      ed.loadLimitMode = Integer.parseInt(data[5]);
    } else {
      ed.loadLimitMode = 1;
    } 
    this.apps.put(ed.alias, ed);
  }
  
  public String toString() {
    String out = "IP: ";
    out = String.valueOf(out) + this.ip;
    out = String.valueOf(out) + "\n";
    out = String.valueOf(out) + "Port: ";
    out = String.valueOf(out) + Integer.toString(this.port);
    out = String.valueOf(out) + "\n";
    out = String.valueOf(out) + "Main DBS: ";
    out = String.valueOf(out) + this.appName;
    out = String.valueOf(out) + " (";
    out = String.valueOf(out) + this.fdsApp;
    out = String.valueOf(out) + ")";
    out = String.valueOf(out) + "\n";
    out = String.valueOf(out) + "LL: ";
    out = String.valueOf(out) + Integer.toString(this.loadLimitMode);
    out = String.valueOf(out) + "\n";
    out = String.valueOf(out) + "Default alias: ";
    out = String.valueOf(out) + this.defAlias;
    return out;
  }
}
