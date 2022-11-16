package engiutils;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import jakarta.servlet.http.HttpServletRequest;

public class HttpHeadersMap extends EngiCachingDelegatedMap {
   private HttpServletRequest request;
   private Set keySet;

   public HttpHeadersMap(HttpServletRequest request) {
      this.request = request;
      this.keySet = new HashSet();
      Enumeration headers = request.getHeaderNames();

      while(headers.hasMoreElements()) {
         String header = (String)headers.nextElement();
         this.keySet.add(header.toLowerCase());
      }

   }

   public boolean containsKey(Object key) {
      return this.keySet.contains(key.toString().toLowerCase());
   }

   public Object get(Object key) {
      return super.get(key.toString().toLowerCase());
   }

   public boolean isEmpty() {
      return this.keySet.isEmpty();
   }

   public Set keySet() {
      return this.keySet;
   }

   public Object read(Object key) {
      return this.request.getHeader(key.toString());
   }

   public int size() {
      return this.keySet.size();
   }
}
