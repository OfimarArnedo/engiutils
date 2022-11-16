package engiutils;

public class UTF8Encoder {
  public static String encode(String s) {
    String result;
    try {
      result = new String(s.getBytes("UTF-8"));
    } catch (Exception e) {
      result = "";
    } 
    return result;
  }
}
