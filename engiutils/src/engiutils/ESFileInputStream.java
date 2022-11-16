package engiutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ESFileInputStream extends FileInputStream {
  public ESFileInputStream(File f) throws FileNotFoundException {
    super(f);
  }
  
  public String readString() {
    String Result;
    try {
      Result = "";
      int Buffer = read();
      if (Buffer > -1) {
        do {
          if (Buffer == 13)
            continue; 
          if (Buffer == 10)
            break; 
          Result = String.valueOf(Result) + (char)Buffer;
        } while ((Buffer = read()) > -1);
      } else {
        Result = null;
      } 
    } catch (IOException e) {
      Result = "";
    } 
    return Result;
  }
}
