package engiutils;

import java.io.PrintWriter;
import java.io.StringWriter;

class ErrorInfo {
   private final String id;
   private final Object value;
   // $FF: synthetic field
   static Class class$0;

   public ErrorInfo(String id, Object value) {
      this.id = id;
      this.value = value;
   }

   public String getId() {
      return this.id;
   }

   public String toString() {
      String out = "[";
      out = out + this.id;
      out = out + "]\n";
      if (this.value == null) {
         out = out + "null";
      } else if (this.value instanceof Throwable) {
         Throwable e = (Throwable)this.value;
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);
         this.printMinimalStackTrace(e, pw);
         out = out + sw.toString();
      } else {
         out = out + this.value.toString().trim();
         out = out + "\n";
      }

      return out;
   }

   private void printMinimalStackTrace(Throwable e, PrintWriter pw) {
      pw.println(e);
      StackTraceElement[] trace = e.getStackTrace();

      for(int i = 0; i < trace.length; ++i) {
         String var10000 = trace[i].getClassName();
         Class var10001 = class$0;
         if (var10001 == null) {
            try {
               var10001 = Class.forName("javax.servlet.http.HttpServlet");
            } catch (ClassNotFoundException var5) {
               throw new NoClassDefFoundError(var5.getMessage());
            }

            class$0 = var10001;
         }

         if (var10000.equals(var10001.getName())) {
            break;
         }

         pw.println("\tat " + trace[i]);
      }

      Throwable ourCause = e.getCause();
      if (ourCause != null) {
         pw.println("Caused by...");
         this.printMinimalStackTrace(ourCause, pw);
      }

   }
}