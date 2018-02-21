//Thomas Varano
//Feb 20, 2018

package restarter;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

import constants.ErrorID;

public class RestarterSrc {
   public static final String getExec() {
      if (System.getProperty("user.dir").indexOf(".app") > 0)
         return System.getProperty("user.dir").substring(0,
               System.getProperty("user.dir").indexOf(".app")) + ".app";
      return System.getProperty("user.dir");
   }

   public static void main(String[] args) {
      System.out.println("RESTARTING.....");
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            try {
               Thread.sleep(100);
               Desktop.getDesktop().open(new File(getExec()));
            } catch (IOException | InterruptedException e) {
               ErrorID.showError(e, false);
            }
         }
      });
   }
}