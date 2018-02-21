//Thomas Varano
//Feb 20, 2018

package restarter;

import java.io.File;
import java.io.IOException;

/**
 * Actually calls the restart method, used in Agenda
 * 
 * @author Thomas Varano
 *
 */
public final class RestartCall {

   public static int exec(Class<?> c)
         throws IOException, InterruptedException {
      String javaHome = System.getProperty("java.home");
      String javaBin = javaHome + File.separator + "bin" + File.separator
            + "java";
      String classpath = System.getProperty("java.class.path");
      String className = c.getCanonicalName();

      ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath,
            className);

      Process process = builder.start();
      process.waitFor();
      return process.exitValue();
   }

   public static int callRestart() {
      try {
         return exec(RestarterSrc.class);
      } catch (IOException | InterruptedException e) {
         constants.ErrorID.showError(e, false);
      }
      return 1;      
   }
   
   public static void main(String[] args) {
      callRestart();
   }
}
