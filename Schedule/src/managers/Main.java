package managers;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalTime;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import constants.ErrorID;
import ioFunctions.SchedWriter;

//Thomas Varano
//[Program Descripion]
//Sep 20, 2017


public class Main extends JPanel
{
   private static final long serialVersionUID = 1L;
   public static final String APP_NAME = "Agenda";
   public static final String BUILD = "1.2.3 (Alpha)";
   private static final int MIN_W = 730, MIN_H = 313;
   public static final int PREF_W = MIN_W+1, PREF_H = 460;
   private PanelManager manager;
   public static boolean statusU;
   
   public Main() { 
      statusU = true;
      try {
         File log = new File(SchedWriter.LOG_ROUTE);
         PrintStream logStream = new PrintStream(log);
         System.setOut(logStream);
         System.setErr(logStream);
      } catch (FileNotFoundException e) {
         ErrorID.showError(e, true);
      }
      if (statusU) System.out.println(LocalTime.now()+" : Program Initialized");
	   UIHandler.init();
	   manager = new PanelManager(this, (JFrame)getParent());
	   manager.setCurrentPane(false);
   }
   
   public Dimension getMinimumSize() {
      return new Dimension(MIN_W,MIN_H);
   }
   public Dimension getPreferredSize() {
      return new Dimension(PREF_W, PREF_H);
   }
   private static void createAndShowGUI() {
      JFrame frame = new JFrame(APP_NAME + " " + BUILD);
      int frameToPaneAdjustment = 22;
      frame.getContentPane().add(new Main());
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setMinimumSize(new Dimension(MIN_W, MIN_H + frameToPaneAdjustment));
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);   
   }
   public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            createAndShowGUI();
         }
      });
   }
}
