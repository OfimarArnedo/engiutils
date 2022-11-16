package engiutils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class EStringUtils {
  public static String streamToString(InputStream inputStream) throws IOException {
    StringBuffer sb = new StringBuffer();
    BufferedReader br = new BufferedReader(new InputStreamReader(
          inputStream));
    int b;
    while ((b = br.read()) >= 0)
      sb.append((char)b); 
    return sb.toString();
  }
  
  public static ByteArrayOutputStream streamToArray(InputStream inputStream) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[512];
    int read = 0;
    while ((read = inputStream.read(buffer, 0, buffer.length)) != -1)
      baos.write(buffer, 0, read); 
    return baos;
  }
  
  public static ByteArrayOutputStream stringToArray(String s) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      baos.write(s.getBytes());
    } catch (IOException iOException) {}
    return baos;
  }
  
  public static String nullify(String value) {
    if (value == null)
      return "null"; 
    return value;
  }
}
