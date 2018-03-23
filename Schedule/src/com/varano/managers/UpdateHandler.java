//Thomas Varano
//Mar 22, 2018

package com.varano.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.swing.JOptionPane;

import com.varano.resources.Addresses;
import com.varano.resources.ioFunctions.OrderUtility;

public class UpdateHandler {
   
   public static final String SOURCE_PATH = "http://agendapascack.x10host.com/updates/updater-src.txt";
   public static final String DOWNLOAD_PATH = System.getProperty("user.home") + "/Downloads/Agenda-Update.jar";
   
   public static void update() throws Exception {
      try {
         OrderUtility.futureCall(10000, UpdateHandler::download, "download updater jar");
         Process run = new ProcessBuilder("java", "-jar", DOWNLOAD_PATH).start();
         InputStream in = run.getInputStream();
         byte[] bts = in.readAllBytes();
         for (byte b : bts)
            System.out.print((char)b);
         System.out.println("run: " + run.waitFor());
         
      } catch (IOException | InterruptedException e) {
         Agenda.logError("error in downloading or running updater jar", e);
      }
   }
   
   private static int download() throws Exception{      
      URL sourceURL = new URL(SOURCE_PATH);
      File download = new File(DOWNLOAD_PATH);
      copyFileUsingStream(sourceURL.openStream(), download);
      return 0;
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
   
   public static String version() throws Exception {
      return OrderUtility.futureCall(100, UpdateHandler::version0, "retrieve version");
   }
   
   private static String version0() throws Exception {      
      URL updateInfo = new URL(Addresses.UPDATES_HOME + "update-info.txt");
      BufferedReader in = new BufferedReader(new InputStreamReader(updateInfo.openStream()));
      return in.readLine();
   }
   
   public static boolean updateAvailable() throws Exception {
      Agenda.log("checking for updates...");
      return (!version().equals(Agenda.BUILD));
   }
   
   /**
    * @return true if the user wants to update.
    */
   public static boolean askUpdate() {
      Agenda.log("asking if update");
      return JOptionPane.showConfirmDialog(null, "An update is available.\nWould you like to install?", Agenda.APP_NAME,
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null) == 0;
   }
   
   public static void updateInquiry() {
      try {
         if (updateAvailable()) {
            Agenda.log("update available");
            if (askUpdate())
               update();
         } else Agenda.log("no updates available");
      } catch (Exception e) {
         Agenda.logError("unable to check for updates", e);
      }
   }
   
   public static void main(String[] args) {
      askUpdate();
   }
}
