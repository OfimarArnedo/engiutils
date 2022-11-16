package engiutils;

import java.util.Date;

public interface EngiRequest {
  int getClientType();
  
  String getClientIP();
  
  String getFunctionCall();
  
  Date getDate();
  
  int getRequestSize();
  
  int getResponseSize();
  
  long getElapsedTime();
  
  void setResponseSize(int paramInt);
  
  boolean isEitech();
  
  String toCSV();
}
