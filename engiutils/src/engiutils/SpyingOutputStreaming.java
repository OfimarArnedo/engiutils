package engiutils;

import java.io.IOException;
import java.io.OutputStream;

public class SpyingOutputStreaming extends OutputStream {
   private OutputStream os;
   private boolean started;

   public SpyingOutputStreaming(OutputStream os) {
      this.os = os;
      this.started = false;
   }

   public void write(int b) throws IOException {
      if (!this.started) {
         System.out.print(">>> ");
         this.started = true;
      }

      System.out.print((char)b);
      this.os.write(b);
   }
}