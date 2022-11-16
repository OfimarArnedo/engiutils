package engiutils;

import java.io.File;
import java.io.FilenameFilter;

class EngiProfiler$1 implements FilenameFilter {
   // $FF: synthetic field
   final EngiProfiler this$0;

   EngiProfiler$1(EngiProfiler var1) {
      this.this$0 = var1;
   }

   public boolean accept(File dir, String name) {
      return name.endsWith(".csv");
   }
}