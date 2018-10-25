//Thomas Varano
//Feb 25, 2018

package com.varano.managers;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.Scanner;

import javax.swing.UIManager;

import com.varano.information.constants.ErrorID;
import com.varano.resources.Addresses;
import com.varano.resources.ResourceAccess;
import com.varano.ui.UIHandler;

public class FileHandler {
   public static String ENVELOPING_FOLDER;
   public static String RESOURCE_ROUTE;
   public static String LOG_ROUTE;
   public static String DEFAULT_SCHED_ROUTE, SCHED_ROUTE;
   public static String THEME_ROUTE, LAF_ROUTE;
   public static String WELCOME_ROUTE, GPA_ROUTE;
   public static String NOTIF_ROUTE;
   public static String IMPORT, EXPORT, SCHED_FINDER;
   public static String OLD_SCHED;
   
   public static final String SUFFIX = ".sched";
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
   
   /**
    * ensure names, users, etc. Initialize file locations if necessary, draw routes.
    */
   public static void initialFileWork() {
      long start = System.currentTimeMillis();
      
      boolean logData = Agenda.isApp;

      FileHandler.ensureFileRoute();

      //check parameters, draw routes, create files if needed 
      FileHandler.initAndCreateFiles();
      
      //set system.out to the log file
      if (logData) {
         try {
            File log = new File(FileHandler.LOG_ROUTE);
            PrintStream logStream = new PrintStream(log);
            System.setOut(logStream);
            System.setErr(logStream);
            Agenda.log ("streams set to "+FileHandler.LOG_ROUTE);
         } catch (java.io.FileNotFoundException e) {
            ErrorID.showError(e, true, "Make sure you downloaded Agenda and it is in an\n"
                  + "accessable folder (Applications, Desktop, etc.)");
         }
      } else {
         Agenda.log("logging to console / terminal");
      }
      //logs the time taken (in millis)
      Agenda.log("filework completed in "+(System.currentTimeMillis()-start));
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

   public static void initFileNames(String envelop) {
      ENVELOPING_FOLDER = envelop;
      RESOURCE_ROUTE = ENVELOPING_FOLDER+"InternalData/";
      LOG_ROUTE = RESOURCE_ROUTE +"AgendaLog.txt";
      
      SCHED_FINDER = RESOURCE_ROUTE + "ScheduleRoute.txt";
      DEFAULT_SCHED_ROUTE = RESOURCE_ROUTE + "default" + SUFFIX;
      SCHED_ROUTE = readScheduleRoute(SCHED_FINDER);
      OLD_SCHED =  RESOURCE_ROUTE + "ScheduleHold.txt";
      
      NOTIF_ROUTE = RESOURCE_ROUTE + "notif.txt";
      THEME_ROUTE = RESOURCE_ROUTE + "theme.txt";
      LAF_ROUTE = RESOURCE_ROUTE + "look.txt";
      
      WELCOME_ROUTE = RESOURCE_ROUTE + "showWelcome.txt"; 
      GPA_ROUTE = RESOURCE_ROUTE + "showGPA.txt";
      
      IMPORT = RESOURCE_ROUTE + "ScheduleImport.scpt";
      EXPORT = RESOURCE_ROUTE + "ScheduleExport.scpt";
   }
   
   public static void setSchedRoute() {
   		SCHED_ROUTE = readScheduleRoute(SCHED_FINDER);
   }
   
	private static String readScheduleRoute(String route) {
		File routeHolder = new File(route);
		Scanner s = null;
		String ret = DEFAULT_SCHED_ROUTE;
		try {
			s = new Scanner(routeHolder);
			ret = s.nextLine();
			s.close();
		} catch (FileNotFoundException e) {
			try {
				Agenda.log("route holder not found.");
				routeHolder.createNewFile();
				write(DEFAULT_SCHED_ROUTE, routeHolder.getAbsolutePath());
			} catch (IOException e1) {
				Agenda.logError("exception creating schedRouteHolder", e1);
			}
		}
		return ret;
	}
	
   
   public static final String TRUE = "t", FALSE = "f";

   public synchronized static boolean createFiles() {
      boolean created = new File(RESOURCE_ROUTE).mkdirs();
      Agenda.log("files created");
      transfer("README.txt", new File(ENVELOPING_FOLDER + "README.txt"), 0);
      transfer("ScheduleImport.scpt", new File(IMPORT), 0);
      transfer("ScheduleExport.scpt", new File(EXPORT), 0);
      try {
         if (new File(THEME_ROUTE).createNewFile())
            write(UIHandler.themes[0], THEME_ROUTE);
         if (new File(LAF_ROUTE).createNewFile()) 
            write(UIManager.getSystemLookAndFeelClassName(), LAF_ROUTE);
         if (new File(WELCOME_ROUTE).createNewFile()) 
            writeWelcomeTrue();
         if (new File(NOTIF_ROUTE).createNewFile())
            write(TRUE, NOTIF_ROUTE);
         if (new File(GPA_ROUTE).createNewFile())
         		write(FALSE, GPA_ROUTE);
      } catch (IOException e) {
         ErrorID.showError(e, false);
      }
      return created;
   }
   
   public static void writeWelcomeTrue() throws IOException {
      write(TRUE, WELCOME_ROUTE);
   }
   
   public static void write(String text, String route) throws IOException{      
      BufferedWriter bw = new BufferedWriter(new FileWriter(route));
      bw.write(text);
      bw.close(); 
   }

   public static boolean moveFiles(String oldLocation) {
      Agenda.log("attempting to move files");

      return new File(oldLocation).renameTo(new File(ENVELOPING_FOLDER));
   }
   
   public static void transfer(String localPath, File f, int skipLines) {
      Agenda.log("transferring data");
      try {
         if (!f.createNewFile()) return;
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
      } catch (IOException | NullPointerException e) {
         ErrorID.showError(e, true);
      }
   }
}
