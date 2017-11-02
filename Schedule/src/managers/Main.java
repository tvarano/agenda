package managers;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

//Thomas Varano
//[Program Descripion]
//Sep 20, 2017


public class Main extends JPanel
{
   private static final long serialVersionUID = 1L;
   public static final String APP_NAME = "Tommy's adventure";
   private static final int MIN_W = 655, MIN_H = 313;
   public static final int PREF_W = MIN_W+1, PREF_H = 460;
   private PanelManager manager;
   
   public Main() { 
	   UIHandler.init();
	   try {
		Thread.sleep(50);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
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
      JFrame frame = new JFrame(APP_NAME);
      int frameToPaneAdjustment = 22;
      frame.getContentPane().add(new Main());
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setMinimumSize(new Dimension(MIN_W, MIN_H+frameToPaneAdjustment));
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
