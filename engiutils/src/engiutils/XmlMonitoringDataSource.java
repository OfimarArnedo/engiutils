package engiutils;

import java.io.ByteArrayOutputStream;

public interface XmlMonitoringDataSource {
   void setAsFailed(int var1, ByteArrayOutputStream var2);

   void setRawFDSValue(String var1);

   void setQueuedTime(long var1);

   void setWindowData(int var1, int var2);
}