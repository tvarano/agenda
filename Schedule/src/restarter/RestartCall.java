//Thomas Varano
//Feb 20, 2018

package restarter;

import java.io.IOException;

import managers.Agenda;
import managers.FileHandler;
import resources.Addresses;

/**
 * Actually calls the restart method, used in Agenda
 * 
 * @author Thomas Varano
 *
 */
public final class RestartCall {
   
   public static void callRestartScript() throws IOException, InterruptedException {
      Process verify = new ProcessBuilder("chmod", "755", FileHandler.SCRIPT_ROUTE).start();
      verify.waitFor();
      Process restart = new ProcessBuilder("./"+FileHandler.SCRIPT_ROUTE, Addresses.getExec()).start();
      restart.waitFor();
   }
   
   public static void callRestart() {
      Runtime.getRuntime().addShutdownHook(new Thread() {
         @Override
         public void run() {
            try {
               Runtime.getRuntime().exec(new String[] {"open", Addresses.getExec()});
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      });
      System.exit(0);
   }
   
   public static void main(String[] args) {
      Agenda.initialFileWork();
      callRestart();
   }
}
