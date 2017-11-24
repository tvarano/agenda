package managers;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.MenuBar;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
   
   public Agenda() { 
      boolean logData = false;
      statusU = false;
      if (logData) {
         try {
            if (new File(SchedWriter.RESOURCE_ROUTE).mkdirs()) {
               Reader.transferReadMe(new File(SchedWriter.ENVELOPING_FOLDER+"README.txt"));
               BufferedWriter bw = new BufferedWriter(new FileWriter(SchedWriter.RESOURCE_ROUTE+"theme.txt"));
               bw.write(UIHandler.themes[0]);
               bw.close();
            }
            File log = new File(SchedWriter.LOG_ROUTE);
            PrintStream logStream = new PrintStream(log);
            System.setOut(logStream);
            System.setErr(logStream);
         } catch (IOException e) {
            ErrorID.showError(e, true);
         }
      }
      if (statusU) log("Main began initialization");
	   UIHandler.init();
	   manager = new PanelManager(this, bar);
	   manager.setCurrentPane(false);
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
      //TODO answer is in threading 
      long start = System.currentTimeMillis();
      EventQueue.invokeLater(new Runnable() {
         public void run() {
//            JFrame loadF = UIHandler.createLoadingScreen(new JFrame());
            parentFrame = new JFrame(APP_NAME + " " + BUILD);
            int frameToPaneAdjustment = 22;
            bar = UIHandler.configureMenuBar(parentFrame);
            Agenda main = new Agenda();
            parentFrame.getContentPane().add(main);
            parentFrame.setMinimumSize(new Dimension(MIN_W, MIN_H + frameToPaneAdjustment));
            parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            parentFrame.pack();
            parentFrame.setLocationRelativeTo(null);
//            loadF.dispose();
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
