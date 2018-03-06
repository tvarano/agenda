//Thomas Varano
//Feb 20, 2018

package restarter;

import java.io.IOException;

import com.varano.constants.ErrorID;
import com.varano.managers.Agenda;
import com.varano.managers.FileHandler;
import com.varano.resources.Addresses;

/**
 * Actually calls the restart method, used in Agenda
 * 
 * @author Thomas Varano
 *
 */
public final class RestartCall {
   
   public static void callRestartScript() {
      new Thread() {
         @Override
         public void run() {
            try {
               Process verify = new ProcessBuilder("chmod", "755",
                     FileHandler.SCRIPT_ROUTE).start();
               verify.waitFor();
               Process restart = new ProcessBuilder("sh", FileHandler.SCRIPT_ROUTE, Addresses.getDir())
                           .start();
               restart.waitFor();
            } catch (InterruptedException | IOException e) {
               ErrorID.showError(e, false);
            }
         }
      }.start();
   }
   
   public static void callRestart() {
      Runtime.getRuntime().addShutdownHook(new Thread() {
         @Override
         public void run() {
            try {
               Runtime.getRuntime().exec(new String[] {"open", Addresses.getExec()});
               Agenda.log("shutdown activated");
            } catch (IOException e) {
               ErrorID.showError(e, false);
            }
         }
      });
      Agenda.log("exit called");
      System.exit(0);
   }
   
   public static void main(String[] args) {
      Agenda.initialFileWork();
      callRestart();
   }
}
