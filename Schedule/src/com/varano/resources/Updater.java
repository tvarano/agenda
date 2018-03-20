//Thomas Varano
//Mar 15, 2018

package com.varano.resources;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Updater {
   //get error logging working
   
   //copy file works
   //even from inside package
   
   static String home = System.getProperty("user.home");
   
   static final String CFG_VERSION_KEY = "app.version=";
   
   static final String CFG_JAR_KEY = "app.mainjar=";
   
   static final String INFO_VERSION_KEY_A = "CFBundleShortVersionString";

   static final String INFO_VERSION_KEY_B = "CFBundleVersion";

   static final String APP_NAME = "Agenda";
   
   private static String askHome() {
      System.out.println("ask home");
      JLabel userHome = new JLabel(Updater.home + "/");
      JLabel suffix = new JLabel("/Agenda.app");
      JTextField f = new JTextField();
      JPanel p = new JPanel();
      f.setPreferredSize(new java.awt.Dimension(350, 25));
      p.add(userHome); p.add(f); p.add(suffix);
      JOptionPane.showMessageDialog(null, p, "Where is the Application Kept?", JOptionPane.INFORMATION_MESSAGE, null);
      return f.getText();
   }
   
   private static String otherVersion() {
      return "1.7.6";
   }
   
   /**
    * hard coded. Update version every time
    * @param args
    */
   public static void main(String[] args) {
      //Quit Agenda
      String newJarName = "Agenda.jar";
      boolean appWasRunning;
      try {
         Process quit = new ProcessBuilder("killall", "Agenda").start();
         appWasRunning = quit.exitValue() == 0;
         quit.waitFor();
      } catch (IOException | InterruptedException e1) {
         e1.printStackTrace();
         appWasRunning = false;
      }
      System.out.println("running: "+appWasRunning);
      
      String home;
      String version = "1.7.7";
      if (args.length > 0 && args[0] != null)
         home = args[0];
      else
         home = Updater.home + "/Desktop/Update/Agenda.app";
//         home = "/Applications/Agenda.app";
      if (!new File(home).exists())
         home = askHome();
      String javaHome = home + "/Contents/Java/";
      String dest = javaHome + APP_NAME + ".jar";
      
      
      //delete old jar
      boolean removed = new File(dest).delete();
      if (!removed) {
         //account for old program, where the jar was Agenda-1.7.6.jar
         System.out.println("Agenda-"+otherVersion()+".jar");
         if (!new File(javaHome + "Agenda-"+otherVersion()+".jar").delete()) {
            showFailure("Error removing old jar.");
            System.exit(0);
         }
      }
      
      //trying to copy
      try {
         transfer(dest);
      } catch (IOException e) {
         System.err.println("attempt to copy failed");
         showFailure("Error Copying Files.");
         System.exit(1);
      }
      
      //change references in Agenda.cfg
      try {
         String cfg = "";
         Scanner in = new Scanner(new File(javaHome + "Agenda.cfg"));
         while (in.hasNextLine())
            cfg += in.nextLine() + "\n";
         in.close();
         BufferedWriter bw = new BufferedWriter(new FileWriter(new File(javaHome + "Agenda.cfg")));
         in = new Scanner(cfg);
         while (in.hasNextLine()) {
            String ln = in.nextLine();
            if (ln.contains(CFG_VERSION_KEY))
               ln = ln.substring(0, ln.indexOf('=') + 1) + version;
            else if (ln.contains(CFG_JAR_KEY))
               ln = ln.substring(0, ln.indexOf('=') + 1) + newJarName;
            bw.write(ln + "\n");
         }
         bw.close();
         in.close();
      } catch (IOException e1) {
         e1.printStackTrace();
         showFailure("Cannot update configuration references.");
      }
      
      //change references in info.plist
      //<string>1.7.6</string>
      try {
         String info = ""; 
         Scanner in = new Scanner(new File(home + "/Contents/Info.plist"));
         while (in.hasNextLine())
            info += in.nextLine() + "\n";
         in.close();
         BufferedWriter bw = new BufferedWriter(new FileWriter(new File(home + "/Contents/Info.plist")));
         in = new Scanner(info);
         while (in.hasNextLine()) {
            String ln = in.nextLine();
            if (ln.contains(INFO_VERSION_KEY_A) || ln.contains(INFO_VERSION_KEY_B)) {
               bw.write(ln + "\n");
               bw.write("<string>" + version + "</string>\n");
               in.nextLine();
            } else bw.write(ln + "\n");
         }
         bw.close();
         in.close();       
      } catch (Exception e1) {
         e1.printStackTrace();
         showFailure("Cannot update info.plist references.");
      }
      
      //if app was running, open a new instance
      if (appWasRunning) {
         try {
            Process restart = new ProcessBuilder("open", home).start();
            restart.waitFor();
            System.out.println("restart: " + restart.exitValue());
         } catch (IOException | InterruptedException e) {
            e.printStackTrace();
         }
      }
   }
   
   private static void showFailure(String reason) {
      JOptionPane.showMessageDialog(null, "Unable to update Agenda.\n"
            + reason + "\nPlease try again.", "Agenda Updater", JOptionPane.ERROR_MESSAGE, null);
   }
   private static void transfer(String dest) throws IOException {
      copyFileUsingStream(Updater.class.getResourceAsStream("src.jar"), new File(dest));
   }
   
   private static void copyFileUsingStream(InputStream source, File dest) throws IOException {
      OutputStream os = null;
      System.out.println("copying from: " + source);
      System.out.println("to: " + dest);
      try {
          os = new FileOutputStream(dest);
          byte[] buffer = new byte[1024];
          int length;
          while ((length = source.read(buffer)) > 0) {
              os.write(buffer, 0, length);
          }
      } finally {
          source.close();
          os.close();
      }
  }
}
