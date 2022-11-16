package engiutils;

import java.io.UnsupportedEncodingException;

public class UTF8Decoder {
   public static String decode(String s) {
      String result;
      try {
         result = new String(s.getBytes(), "UTF-8");
      } catch (UnsupportedEncodingException var3) {
         result = "";
      }

      return result;
   }
}