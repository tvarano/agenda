//Thomas Varano
//Feb 25, 2018

package com.varano.managers;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.varano.information.constants.ErrorID;
import com.varano.resources.Addresses;
import com.varano.resources.ResourceAccess;

public class FileHandler {
   public static String ENVELOPING_FOLDER;
   public static String RESOURCE_ROUTE;
   public static String LOG_ROUTE;
   public static String FILE_ROUTE;
   public static String THEME_ROUTE, LAF_ROUTE;
   public static String SCRIPT_ROUTE;
   public static String WELCOME_ROUTE;
   public static final String NO_LOCATION = "noLoc";
   
   public static void openURI(URI uri) {
      if (Desktop.isDesktopSupported()) {
         try {
            Desktop.getDesktop().browse(uri);
         } catch (IOException e) {
            ErrorID.showError(e, true);
         }
      } else {
         ErrorID.showUserError(ErrorID.IO_EXCEPTION);
      }
   }
   
   public static boolean ensureFileRoute() {
      return new File(Addresses.getHome())
            .mkdirs();
   }

   public static void initAndCreateFiles() {
      // read file and set/
      initFileNames(Addresses.getHome());
      
      //if you need, create your folder and initialize routes
      createFiles();
   }
   
   public static void openDesktopFile(String path) {
      if (Desktop.isDesktopSupported()) {
         try {
            Desktop.getDesktop().open(new File(path));
         } catch (IOException e1) {
            ErrorID.showError(e1, true);
         }
     }
   }
   
   public static void sendEmail() {
      int choice = JOptionPane.showOptionDialog(null, "Make the subject \"Agenda Contact\"\nMail to "+ 
            Addresses.CONTACT_EMAIL, 
            Agenda.APP_NAME + " Contact", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, 
            new String[] {"Use Desktop", "Use Gmail", "Cancel"}, "Use Desktop");
      if (choice == 2 || choice == -1) 
         return;
      else {
         if (Desktop.isDesktopSupported()) {
            try {
               if (choice == 0)
                  Desktop.getDesktop().mail(new URI("mailto:"+Addresses.CONTACT_EMAIL+"?subject=Agenda%20Contact"));
               else
                  Desktop.getDesktop().browse(new URI("https://mail.google.com/mail/u/0/#inbox?compose=new"));
            } catch (IOException | URISyntaxException e1) {
               ErrorID.showError(e1, true);
            }
         }
      }
   }

   public static void initFileNames(String envelop) {
      ENVELOPING_FOLDER = envelop;
      RESOURCE_ROUTE = ENVELOPING_FOLDER+"InternalData/";
      LOG_ROUTE = RESOURCE_ROUTE+"AgendaLog.txt";
      FILE_ROUTE = RESOURCE_ROUTE + "ScheduleHold.txt";
      THEME_ROUTE = RESOURCE_ROUTE + "theme.txt";
      LAF_ROUTE = RESOURCE_ROUTE + "look.txt";
      SCRIPT_ROUTE = RESOURCE_ROUTE + "Restart.sh";
      WELCOME_ROUTE = RESOURCE_ROUTE + "showWelcome.txt"; 
   }

   public synchronized static boolean createFiles() {
      boolean created = new File(RESOURCE_ROUTE).mkdirs();
      Agenda.log("files created");
      transfer("README.txt", new File(ENVELOPING_FOLDER + "README.txt"), 0);
      BufferedWriter bw;
      try {
         if (new File(THEME_ROUTE).createNewFile()) {
            bw = new BufferedWriter(new FileWriter(THEME_ROUTE));
            bw.write(UIHandler.themes[0]);
            bw.close();
         }
         if (new File(LAF_ROUTE).createNewFile()) {
            bw = new BufferedWriter(new FileWriter(LAF_ROUTE));
            bw.write(UIManager.getSystemLookAndFeelClassName());
            bw.close();
         }
         if (new File(WELCOME_ROUTE).createNewFile()) {
            writeWelcomeTrue();
         }
      } catch (IOException e) {
         ErrorID.showError(e, false);
      }
      return created;
   }
   
   public static void writeWelcomeTrue() throws IOException {
      BufferedWriter bw = new BufferedWriter(new FileWriter(WELCOME_ROUTE));
      bw.write("t");
      bw.close(); 
   }

   public static boolean moveFiles(String oldLocation) {
      Agenda.log("attempting to move files");

      return new File(oldLocation).renameTo(new File(ENVELOPING_FOLDER));
   }
   
   public static void transfer(String localPath, File f, int skipLines) {
      Agenda.log("transferring readme");
      try {
         if (f.createNewFile()) {
            Scanner in = new Scanner(ResourceAccess.getResourceStream(localPath));
            for (int i = 0; i < skipLines; i++)
               in.nextLine();
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            while (in.hasNextLine()) {
               bw.write(in.nextLine()+"\r\n");
            }
            f.setWritable(false);
            in.close();
            bw.close();
         }
      } catch (IOException | NullPointerException e) {
         ErrorID.showError(e, true);
      }
   }
}
