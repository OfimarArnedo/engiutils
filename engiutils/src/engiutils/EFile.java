package engiutils;

import java.io.File;

public class EFile extends File {
  private static final long serialVersionUID = 1L;
  
  public EFile(String pathname) {
    super(pathname);
  }
  
  public static EFile createTempDir(String id) {
    String path = System.getProperty("java.io.tmpdir");
    path = String.valueOf(path) + File.separator;
    if (id != null)
      path = String.valueOf(path) + id; 
    EFile tmpDir = new EFile(path);
    if (id != null && 
      !tmpDir.mkdir())
      System.out
        .println("[WARN] Se ha producido una situaciexcepcional al crear la ruta " + 
          tmpDir.getAbsolutePath()); 
    return tmpDir;
  }
}
