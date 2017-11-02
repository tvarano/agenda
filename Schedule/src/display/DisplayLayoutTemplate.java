package display;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import constants.Rotation;
import information.Schedule;
import ioFunctions.OldReader;

   public class DisplayLayoutTemplate extends JPanel implements KeyListener
   {
      private static final long serialVersionUID = 1L;
      private static final int PREF_W = 800, PREF_H = 600;
      private static final int MIN_W = 400, MIN_H = 175;
      private RenderingHints hints = new RenderingHints(
         RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      private Font font = new Font("Prestige Elite Std", Font.PLAIN, 25);
      
      private CurrentClassPane westPane;
      private Schedule todaySched, mainSched;
      private ScheduleInfoSelector eastPane;

      public DisplayLayoutTemplate() {
         todaySched = new OldReader().readAndOrderSchedule(Rotation.ODD_BLOCK);
         mainSched = new OldReader().readDefaultSchedule();
         setLayout(new BorderLayout());
//         add(list, BorderLayout.EAST);
         System.out.println("here"+todaySched);
         eastPane = new ScheduleInfoSelector(todaySched, mainSched, this);
         westPane  = new CurrentClassPane(todaySched.getClasses()[3],todaySched, this);
         this.addKeyListener(this);
         add(eastPane, BorderLayout.EAST);
         add(westPane, BorderLayout.CENTER);
         setBackground(Color.WHITE);
         setFocusable(true);
         requestFocus();
      }

      @Override
      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D) g;
         g2.setRenderingHints(hints);

      }

      public Dimension getMinimumSize() {
         return new Dimension(MIN_W,MIN_H);
      }
      public Dimension getPreferredSize() {
         return new Dimension(PREF_W, PREF_H);
      }

      private static void createAndShowGUI() {
         JFrame frame = new JFrame("TITLE");
         int frameToPaneAdjustment = 22;
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.getContentPane().add(new DisplayLayoutTemplate());
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

      @Override
      public void keyTyped(KeyEvent e) {
         System.out.println("dimensions"+getWidth()+","+getHeight());
      }

      @Override
      public void keyPressed(KeyEvent e) {
      }

      @Override
      public void keyReleased(KeyEvent e) {
      }
}
