package engiutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AccessConfig {
   public Boolean active;
   private String configFileName;

   public AccessConfig(String fName) {
      this.active = Boolean.TRUE;
      this.configFileName = fName;
      if (fName != null) {
         ESFileInputStream fis;
         try {
            File f = new File(fName);
            fis = new ESFileInputStream(f);
         } catch (FileNotFoundException var6) {
            var6.printStackTrace();
            fis = null;
         }

         if (fis != null) {
            this.readConfig(fis);

            try {
               fis.close();
            } catch (IOException var5) {
               var5.printStackTrace();
            }
         }

      }
   }

   private void readConfig(ESFileInputStream fis) {
      while(true) {
         String s = fis.readString();
         if (s == null) {
            s = "";
         } else {
            if (s.indexOf("Active=") != 0) {
               continue;
            }

            this.active = Boolean.valueOf(s.substring(7));
         }

         return;
      }
   }

   private void writeString(OutputStream os, String s) {
      byte[] bytes = new byte[s.length()];

      for(int x = 0; x < bytes.length; ++x) {
         bytes[x] = (byte)s.charAt(x);
      }

      try {
         os.write(bytes);
      } catch (IOException var5) {
      }

   }

   public void writeConfig() {
      try {
         File f = new File(this.configFileName);
         if (!f.delete()) {
            System.out.println("[WARN] Se ha producido una situaci�n excepcional al borrar el fichero " + f.getAbsolutePath());
         }

         if (!f.createNewFile()) {
            System.out.println("[WARN] Se ha producido una situaci�n excepcional al crear el fichero " + f.getAbsolutePath());
         }

         FileOutputStream fos = new FileOutputStream(f);
         this.writeString(fos, "Active=" + this.active.toString());
         fos.close();
      } catch (IOException var4) {
      }

   }
}