package engiutils;

public interface EServlet {
  void setMaxcon(int paramInt);
  
  void setTimeout(int paramInt);
  
  void clearCounters();
  
  void clearHistory();
  
  void reloadApps();
  
  String getHTMLStatus();
  
  void reloadConfig();
}
