package managers;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.MenuBar;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalTime;

import javax.swing.JFrame;
import javax.swing.JPanel;

import constants.ErrorID;
import ioFunctions.Reader;
import ioFunctions.SchedWriter;

//Thomas Varano
//[Program Descripion]
//Sep 20, 2017


public class Main extends JPanel
{
   private static final long serialVersionUID = 1L;
   public static final String APP_NAME = "Agenda";
   public static final String BUILD = "1.2.4 (Alpha)";
   public static final int MIN_W = 730, MIN_H = 313;
   public static final int PREF_W = MIN_W+1, PREF_H = 460;
   private PanelManager manager;
   private static MenuBar bar;
   public static boolean statusU;
   
   public Main() { 
      boolean logData = false;
      statusU = true;
      if (logData) {
         try {
            if (new File(SchedWriter.RESOURCE_ROUTE).mkdirs())
               Reader.transferReadMe(new File(SchedWriter.ENVELOPING_FOLDER+"README.txt"));
            File log = new File(SchedWriter.LOG_ROUTE);
            PrintStream logStream = new PrintStream(log);
            System.setOut(logStream);
            System.setErr(logStream);
         } catch (FileNotFoundException e) {
            ErrorID.showError(e, true);
         }
      }
      if (statusU) log("Main began initialization");
	   UIHandler.init();
	   manager = new PanelManager(this, bar);
	   manager.setCurrentPane(false);
   }
   
   public static void log(String s) {
      System.out.println(LocalTime.now() + " : "+s);
   }
   
   public static void logError(String s) {
      System.err.println(LocalTime.now() + " : ERROR: "+s);
   }
   
   public Dimension getMinimumSize() {
      return new Dimension(MIN_W,MIN_H);
   }
   public Dimension getPreferredSize() {
      return new Dimension(PREF_W, PREF_H);
   }
   private static void createAndShowGUI() {
      //TODO answer is in threading 
      long start = System.currentTimeMillis();
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            JFrame loadF = UIHandler.createEmptyLoad();
            UIHandler.createLoadingScreen(loadF);
         }
      });
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            JFrame frame = new JFrame(APP_NAME + " " + BUILD);
            int frameToPaneAdjustment = 22;
            Main main = new  Main();
            frame.getContentPane().add(main);
            frame.setMinimumSize(new Dimension(MIN_W, MIN_H + frameToPaneAdjustment));
            bar = UIHandler.configureMenuBar(frame);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            if (statusU) log("Program Initialized in "+ (System.currentTimeMillis() - start) + " millis");
         }
      });
   }
   public static void main(String[] args) {
      createAndShowGUI();
   }
}
