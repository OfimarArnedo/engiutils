package engiutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public abstract class EConfig {
  private final File file;
  
  public EConfig(String fName) {
    ESFileInputStream fis;
    init();
    this.file = new File(fName);
    try {
      fis = new ESFileInputStream(this.file);
    } catch (FileNotFoundException e) {
      fis = null;
      String out = "WARN [";
      out = String.valueOf(out) + DateFormat.getDateTimeInstance().format(new Date());
      out = String.valueOf(out) + "] ";
      out = String.valueOf(out) + "No existe el fichero: ";
      out = String.valueOf(out) + fName;
      System.out.println(out);
    } 
    if (fis != null) {
      readFile(fis);
      try {
        fis.close();
      } catch (IOException iOException) {}
    } 
  }
  
  private void readFile(ESFileInputStream is) {
    String line;
    while ((line = is.readString()) != null) {
      line = line.trim();
      if (line.length() > 0)
        parse(line); 
    } 
  }
  
  public String getFilePath() {
    return this.file.getAbsolutePath();
  }
  
  protected void init() {}
  
  protected abstract void parse(String paramString);
}
