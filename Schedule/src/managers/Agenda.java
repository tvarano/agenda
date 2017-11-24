package managers;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.MenuBar;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalTime;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import constants.ErrorID;
import ioFunctions.Reader;

//Thomas Varano
//[Program Descripion]
//Sep 20, 2017


public class Agenda extends JPanel
{
   private static final long serialVersionUID = 1L;
   public static final String APP_NAME = "Agenda";
   public static final String BUILD = "1.2.4 (Alpha)";
   public static final int MIN_W = 723, MIN_H = 313;
   public static final int PREF_W = MIN_W, PREF_H = 460;
   private PanelManager manager;
   private static JFrame parentFrame;
   private static MenuBar bar;
   public static boolean statusU;
   
   public static String ENVELOPING_FOLDER;
   public static String RESOURCE_ROUTE;
   public static String LOG_ROUTE;
   public static String FILE_ROUTE;
   
   @SuppressWarnings("resource")
   public Agenda() {
      boolean logData = false;
      statusU = false;
      //if folder location is unassigned, assign it
      try {
         String mainFolder = null;
         if (!new Scanner(new File("FolderRoute.txt")).hasNextLine())
            setFileLocation();
         mainFolder = readFileLocation();
         initFileNames(mainFolder);
      } catch (FileNotFoundException e) {
         ErrorID.showError(e, true);
      }
      if (!new File(ENVELOPING_FOLDER).exists()) {
         setFileLocation();
      }
    
      //if you need, create your folder
      if (new File(RESOURCE_ROUTE).mkdirs()) {
         Reader.transferReadMe(
               new File(ENVELOPING_FOLDER + "README.txt"));
         BufferedWriter bw;
         try {
            bw = new BufferedWriter(
                  new FileWriter(RESOURCE_ROUTE + "theme.txt"));
            bw.write(UIHandler.themes[0]);
            bw.close();
         } catch (IOException e) {
            ErrorID.showError(e, false);
         }
      }

      if (logData) {
         try {
            File log = new File(LOG_ROUTE);
            PrintStream logStream = new PrintStream(log);
            System.setOut(logStream);
            System.setErr(logStream);
         } catch (IOException e) {
            ErrorID.showError(e, true);
         }
      }

      if (statusU)
         log("Main began initialization");
      UIHandler.init();
	   manager = new PanelManager(this, bar);
	   manager.setCurrentPane(false);
   }
   
   public static void setFileLocation() {
       writeFileLocation(askFileLocation());
       initFileNames(readFileLocation());
   }
   public static String askFileLocation() {
      JFileChooser c = new JFileChooser(System.getProperty("user.home"));
      c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      c.setDialogTitle("Choose The Location for Internal Files");
      while (c.showSaveDialog(null) != JFileChooser.APPROVE_OPTION);
      return c.getSelectedFile().getAbsolutePath();
   }
   
   public static String writeFileLocation(String s) {
      try {
         BufferedWriter bw = new BufferedWriter(new FileWriter(
               new File("FolderRoute.txt")));
         bw.write(s);
         bw.close();
      } catch (IOException e) {
         ErrorID.showError(e, true);
      }
      return s;
   }
   
   public static String readFileLocation() {
      Scanner s = null;
      try {
         s = new Scanner(new File("FolderRoute.txt"));
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
      String ret = s.nextLine();
      s.close();
      return ret;
   }

   public static void initFileNames(String envelop) {
      ENVELOPING_FOLDER = envelop+"/";
      RESOURCE_ROUTE = ENVELOPING_FOLDER+"InternalData/";
      LOG_ROUTE = RESOURCE_ROUTE+"AgendaLog.txt";
      FILE_ROUTE = RESOURCE_ROUTE + "ScheduleHold.txt";
   }
   
   public static MenuBar getBar() {
      return bar;
   }
   
   public static void log(String s) {
      System.out.println(LocalTime.now() + " : "+s);
   }
   
   public static void logError(String s, Throwable e) {
      System.err.println(LocalTime.now() + " : ERROR: "+s);
   }
   
   public Dimension getMinimumSize() {
      return new Dimension(MIN_W,MIN_H);
   }
   public Dimension getPreferredSize() {
      return new Dimension(PREF_W, PREF_H);
   }
   
   private static void createAndShowGUI() {
      long start = System.currentTimeMillis();
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            parentFrame = new JFrame(APP_NAME + " " + BUILD);
            int frameToPaneAdjustment = 22;
            bar = UIHandler.configureMenuBar(parentFrame);
            Agenda main = new Agenda();
            parentFrame.getContentPane().add(main);
            parentFrame.setMinimumSize(new Dimension(MIN_W, MIN_H + frameToPaneAdjustment));
            parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            parentFrame.pack();
            parentFrame.setLocationRelativeTo(null);
            parentFrame.setVisible(true);
            if (statusU) log("Program Initialized in "+ (System.currentTimeMillis() - start) + " millis");
         }
      });
   }
   public static void restart() {
      if (statusU) log("Program Restarted\n");
      parentFrame.dispose();
      bar = null;
      main(null);
   }
   
   public static void main(String[] args) {
      statusU = true;
      if (statusU) log("Program Initialized");
      createAndShowGUI();
   }
}
