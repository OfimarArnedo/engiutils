package engiutils;

import java.io.IOException;
import java.io.InputStream;

public class SpyingInputStreaming extends InputStream {
   private InputStream is;
   private boolean started;

   public SpyingInputStreaming(InputStream os) {
      this.is = os;
      this.started = false;
   }

   public int read() throws IOException {
      if (!this.started) {
         System.out.print("<<< ");
         this.started = true;
      }

      int b = this.is.read();
      if (b > -1) {
         System.out.print((char)b);
      }

      return b;
   }
}