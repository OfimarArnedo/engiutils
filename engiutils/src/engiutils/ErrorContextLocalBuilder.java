package engiutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;

public class ErrorContextLocalBuilder {
   private static long seqErr;
   private static final Object lockSeqErr = new Object();
   private final ThreadLocal errId = new ThreadLocal();
   private final ThreadLocal context = new ThreadLocal();
   private final ThreadLocal path = new ThreadLocal();
   // $FF: synthetic field
   static Class class$0;
   // $FF: synthetic field
   static Class class$1;

   public static void add(String name, Object value) {
      ErrorInfo cell = new ErrorInfo(name, value);
      ErrorContextLocalBuilder.ErrorContextBuilderHolder.instance.add(cell);
   }

   public static void dump() {
      TreeMap elems = ErrorContextLocalBuilder.ErrorContextBuilderHolder.instance.getErrorContext();
      if (elems != null) {
         String id = ErrorContextLocalBuilder.ErrorContextBuilderHolder.instance.getErrorContextId();
         String path = ErrorContextLocalBuilder.ErrorContextBuilderHolder.instance.getErrorContextPath();
         if (path == null) {
            init((String)null);
            path = ErrorContextLocalBuilder.ErrorContextBuilderHolder.instance.getErrorContextPath();
         }

         StringBuffer fileNameBuffer = new StringBuffer(path);
         fileNameBuffer.append(File.separator);
         fileNameBuffer.append(id);
         fileNameBuffer.append(".err");
         String fileName = fileNameBuffer.toString();
         String seqId = Long.toString(ErrorContextLocalBuilder.ErrorContextBuilderHolder.instance.getErrorId());

         try {
            FileOutputStream fos = new FileOutputStream(fileName, true);
            StringBuffer headerBuffer = new StringBuffer("=== ");
            headerBuffer.append("Error ID: ");
            headerBuffer.append(seqId);
            headerBuffer.append(" / ");
            headerBuffer.append(DateFormat.getDateTimeInstance().format(new Date()));
            headerBuffer.append(" ===\n");
            fos.write(headerBuffer.toString().getBytes());
            Iterator it = elems.values().iterator();

            while(it.hasNext()) {
               ErrorInfo cell = (ErrorInfo)it.next();
               fos.write(cell.toString().getBytes());
               fos.write("\n".getBytes());
            }

            fos.close();
            StringBuffer outBuffer = new StringBuffer("ERR [");
            outBuffer.append(DateFormat.getDateTimeInstance().format(new Date()));
            outBuffer.append("] ");
            outBuffer.append(seqId);
            outBuffer.append(" ");
            outBuffer.append(id);
            System.out.println(outBuffer.toString());
         } catch (FileNotFoundException var10) {
            var10.printStackTrace();
         } catch (IOException var11) {
            var11.printStackTrace();
         }
      }

   }

   public static long getCurrentErrorId() {
      return ErrorContextLocalBuilder.ErrorContextBuilderHolder.instance.getErrorId();
   }

   public static void init(String path) {
      Object dir = null;

      try {
         if (path == null) {
            dir = EFile.createTempDir((String)null);
         } else {
            dir = new File(path);
            if (!((File)dir).exists() && !((File)dir).mkdirs()) {
               System.out.println("[WARN] Se ha producido una situación excepcional al crear la ruta " + ((File)dir).getAbsolutePath());
            }
         }

         ErrorContextLocalBuilder.ErrorContextBuilderHolder.instance.setErrorContextPath(((File)dir).getCanonicalPath());
      } catch (IOException var3) {
         ErrorContextLocalBuilder.ErrorContextBuilderHolder.instance.setErrorContextPath(((File)dir).getAbsolutePath());
      }

   }

   public static boolean isActive() {
      TreeMap elems = ErrorContextLocalBuilder.ErrorContextBuilderHolder.instance.getErrorContext();
      return elems != null;
   }

   private void add(ErrorInfo info) {
      TreeMap errors = (TreeMap)this.context.get();
      if (errors == null) {
         Long errId = null;
         synchronized(lockSeqErr) {
            ++seqErr;
            errId = new Long(seqErr);
         }

         errors = new TreeMap();
         this.errId.set(errId);
      }

      errors.put(info.getId(), info);
      this.context.set(errors);
   }

   private long getErrorId() {
      Long id = (Long)this.errId.get();
      return id == null ? -1L : id;
   }

   private void setErrorContextPath(String path) {
      this.path.set(path);
   }

   private TreeMap getErrorContext() {
      return (TreeMap)this.context.get();
   }

   private String getErrorContextId() {
      StackTraceElement[] st = (new Throwable()).getStackTrace();
      String id = null;

      for(int i = 1; i < st.length && id == null; ++i) {
         String klassName = st[i].getClassName();

         try {
            Class klass = Class.forName(klassName);
            if (klass != null) {
               Class superklass = klass.getSuperclass();

               while(true) {
                  Class var10001 = class$0;
                  if (var10001 == null) {
                     try {
                        var10001 = Class.forName("java.lang.Object");
                     } catch (ClassNotFoundException var8) {
                        throw new NoClassDefFoundError(var8.getMessage());
                     }

                     class$0 = var10001;
                  }

                  if (superklass.equals(var10001) || id != null) {
                     break;
                  }

                  var10001 = class$1;
                  if (var10001 == null) {
                     try {
                        var10001 = Class.forName("javax.servlet.http.HttpServlet");
                     } catch (ClassNotFoundException var9) {
                        throw new NoClassDefFoundError(var9.getMessage());
                     }

                     class$1 = var10001;
                  }

                  if (superklass.equals(var10001)) {
                     id = klass.getName().toLowerCase();
                  } else {
                     superklass = superklass.getSuperclass();
                  }
               }
            }
         } catch (ClassNotFoundException var10) {
            var10.printStackTrace();
         }
      }

      if (id == null) {
         try {
            id = st[3].getClassName();
         } catch (ArrayIndexOutOfBoundsException var7) {
            id = "unknown";
         }
      }

      return id;
   }

   private String getErrorContextPath() {
      return (String)this.path.get();
   }

   private static class ErrorContextBuilderHolder {
      private static final ErrorContextLocalBuilder instance = new ErrorContextLocalBuilder();
   }
}