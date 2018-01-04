//Thomas Varano
//[Program Descripion]
//Jan 3, 2018

package managers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Creator
{
   private static Agenda program;
   private static JFrame parentFrame;
   private static void createProgram() {
      EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            System.out.println("StART");
            program = new Agenda(parentFrame);
         }
      });
   }

   private static void createFrame() {
      EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            parentFrame = new JFrame(Agenda.APP_NAME + " " + Agenda.BUILD);
            int frameToPaneAdjustment = 22;
            parentFrame.setMinimumSize(new Dimension(Agenda.MIN_W,
                  Agenda.MIN_H + frameToPaneAdjustment));
            parentFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            parentFrame.setVisible(true);
            parentFrame.setLocationRelativeTo(null);
            JPanel in = new JPanel();
            in.setBackground(Color.BLUE);
            parentFrame.getContentPane().add(in);
            parentFrame.pack();
            System.out.println("AEHF");
         }
      });
   }

   private static void finish() {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            parentFrame.getContentPane().add(program);
            parentFrame.pack();
            parentFrame.getContentPane().remove(0);
            parentFrame.setLocationRelativeTo(null);
         }
      });
   }

   private static void createAndShowGUI() {
      // long start = System.currentTimeMillis();
      createFrame();
      createProgram();
      finish();

      // if (statusU)
      // log("Program Initialized in " + (System.currentTimeMillis() - start) +
      // " millis");
   }
   public static void main(String[] args) {
      createAndShowGUI();
   }
}
