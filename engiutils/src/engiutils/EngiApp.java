package engiutils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;

public abstract class EngiApp {
   private Map headers;
   private InputStream body;
   private File cwd;

   public ByteArrayOutputStream call() throws Exception {
      this.validate();
      return this.process();
   }

   public InputStream getBody() {
      return this.body;
   }

   public File getCurrentWorkingDirectory() throws IOException {
      if (this.cwd == null) {
         this.cwd = (new File(".")).getCanonicalFile();
      }

      return this.cwd;
   }

   public String getHeader(String key) {
      return this.getHeader(key, (String)null, false);
   }

   public String getHeader(String key, String defaultValue) {
      return this.getHeader(key, defaultValue, true);
   }

   public String getHeader(String key, String defaultValue, boolean optional) {
      if (this.getHeaders() == null) {
         return null;
      } else {
         String value = (String)this.getHeaders().get(key);
         if (value == null && !optional) {
            throw new IllegalArgumentException("Missing HTTP header " + key);
         } else {
            if (value == null) {
               value = defaultValue;
            }

            return value;
         }
      }
   }

   public Map getHeaders() {
      return this.headers;
   }

   public void init(HttpServletRequest request) throws IOException {
      this.body = request.getInputStream();
      this.headers = new HttpHeadersMap(request);
   }

   public boolean isDebugActive() {
      return this.headers.containsKey("ENG_DEV_DEBUG");
   }

   public boolean isEchoRequest() {
      return this.headers.containsKey("ENG_DEV_ECHO");
   }

   public boolean isProfilingActive() {
      return this.headers.containsKey("ENG_DEV_PROF");
   }

   protected abstract ByteArrayOutputStream process() throws Exception;

   public void setCurrentWorkingDirectory(String path) throws IOException {
      this.cwd = (new File(path)).getCanonicalFile();
   }

   protected void validate() {
      if (this.headers == null) {
         throw new IllegalStateException("Headers not set. Use setHeaders(HttpRequest) before call().");
      } else if (this.body == null) {
         throw new IllegalStateException("Body not set. Use setBody(InputStream) before call().");
      }
   }
}