package com.varano.managers;

import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MenuBar;
import java.awt.RenderingHints;
import java.awt.desktop.AppForegroundEvent;
import java.awt.desktop.AppForegroundListener;
import java.awt.desktop.ScreenSleepEvent;
import java.awt.desktop.ScreenSleepListener;
import java.awt.desktop.SystemSleepEvent;
import java.awt.desktop.SystemSleepListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;

import javax.swing.JFrame;
import javax.swing.JPanel;

//Thomas Varano
//Sep 20, 2017

/*
 * NOTES BEFORE EXPORT
 * is statusU set to true?
 * are all unwanted debug prints not printing?
 * is the build correct?
 */

/**
 * Main class. Begins the program and initializes all references.
 * 
 * @author Thomas Varano
 */
public class Agenda extends JPanel
{
   private static final long serialVersionUID = 1L;
   public static final String APP_NAME = "Agenda";
   public static final String BUILD = "1.7.9";
   public static final String LAST_UPDATED = "April 2018";
   public static final int MIN_W = 733, MIN_H = 360; 
   public static final int PREF_W = MIN_W, PREF_H = 460;
   private PanelManager manager;
   private JFrame parentFrame;
   private MenuBar bar;
   public static boolean statusU;
   public static final boolean isApp = System.getProperty("user.dir").indexOf(".app") > 0; 
   
   public Agenda(JFrame frame) {
      setName("main class");
      
      FileHandler.initialFileWork();
      log(getClass().getSimpleName()+" began initialization");

      UIHandler.init();      
      this.parentFrame = frame;
      bar = UIHandler.configureMenuBar(frame, this);
      manager = new PanelManager(this, bar);
      manager.setCurrentPane(PanelManager.DISPLAY);
      parentFrame.addWindowListener(new java.awt.event.WindowAdapter() {
         @Override
         public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            manager.beforeClose();
            log("program closed");
            System.exit(0);
         }
      });
      desktopSetup();
   }
   
   public static void showWelcome() {
      BufferedReader br;
      try {
         br = new BufferedReader(new FileReader(new File(FileHandler.WELCOME_ROUTE)));
         String line = br.readLine();
         br.close();
         Agenda.log("show welcome: " + line);
         if (line == null || line.equals("t")) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(FileHandler.WELCOME_ROUTE)));
            if (UIHandler.showWelcome())
               bw.write("t");
            else
               bw.write("f");
            bw.close();
         }
      } catch (IOException e) {
         Agenda.logError("error with welcome", e);
      }
   }
   
   private void desktopSetup() {
      if (Desktop.isDesktopSupported()) {
         Desktop.getDesktop().setQuitHandler(new java.awt.desktop.QuitHandler() {
            @Override
            public void handleQuitRequestWith(java.awt.desktop.QuitEvent arg0,
                  java.awt.desktop.QuitResponse arg1) {
               manager.beforeClose();
               log("program quit");
               arg1.performQuit();
            }
         });
         Desktop.getDesktop().addAppEventListener(new SystemSleepListener() {
            @Override
            public void systemAboutToSleep(SystemSleepEvent arg0) {
               log("system slept");
               manager.getDisplay().hardStop();
            }
            @Override
            public void systemAwoke(SystemSleepEvent arg0) {
               log("System awoke on "+java.time.LocalDate.now() + "\n");
               manager.getDisplay().hardResume();
               manager.getDisplay().checkAndUpdateTime();
               manager.getDisplay().checkAndUpdateDate();
            }
         });
         Desktop.getDesktop().addAppEventListener(new ScreenSleepListener() {
            @Override
            public void screenAboutToSleep(ScreenSleepEvent arg0) {
               log("screen slept");
               manager.getDisplay().stop();
            }
            @Override
            public void screenAwoke(ScreenSleepEvent arg0) {
               log("screen awoke on "+java.time.LocalDate.now());
               manager.getDisplay().resume();
            }
         });
         Desktop.getDesktop().addAppEventListener(new AppForegroundListener() {

            @Override
            public void appMovedToBackground(AppForegroundEvent arg0) {}

            @Override
            public void appRaisedToForeground(AppForegroundEvent arg0) {
               manager.update();
            }
         });
      }
   }
   
   public PanelManager getManager() {
      return manager;
   }
   
   public MenuBar getBar() {
      return bar;
   }
   public void show(String name) {
      ((CardLayout) getLayout()).show(this, name);
   } 
   public static void log(String text) {
      if (statusU)
         System.out.println(LocalTime.now() + " : "+text);
   } 
   public static void logError(String message, Throwable e) {
      if (statusU)
         System.err.println(LocalTime.now() + " : ERROR: " + message + " : \n\t" 
      + e.getClass().getName() + " - "+ e.getMessage());
   }
   public JFrame getFrame() {
      return parentFrame;
   }
   public Dimension getMinimumSize() {
      return new Dimension(MIN_W,MIN_H);
   }
   public Dimension getPreferredSize() {
      return new Dimension(PREF_W, PREF_H);
   }
   
   public void repaint() {
      super.repaint();
      if (manager != null)
         manager.repaint();
   }
   
   private static void createAndShowGUI() {
      long start = System.currentTimeMillis();
      JFrame frame = new JFrame("Agenda -- LOADING....");
      int frameToPaneAdjustment = 22; 
      
      // loading screen, frame adjustments
      // setting visible outside of the drawing, strings take just as long to draw anyways
         //might as well give a loading screen
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            JPanel p = new JPanel() {
               private static final long serialVersionUID = 1L;
               
               //drawing strings while continuing calculations
               @Override 
               public void paintComponent(Graphics g) {
                  super.paintComponent(g);
                  Graphics2D g2 = (Graphics2D) g;
                  g2.addRenderingHints(new RenderingHints(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON));
                  java.awt.Image logo = com.varano.resources.ResourceAccess
                        .getIcon("Agenda Logo.png").getImage();
                  g2.drawImage(logo, getWidth() / 2 - logo.getWidth(this) / 2,
                        getHeight() / 2 - logo.getHeight(this) / 2 + 15, this);
                  g2.setFont(UIHandler.font.deriveFont(36F).deriveFont(Font.BOLD));
                  String load = "LOADING...";
                  g2.drawString(load, getWidth() / 2
                        - getFontMetrics(g2.getFont()).stringWidth(load) / 2, 150);
                  log("Drawing strings took " + (System.currentTimeMillis() - start));
               }
            };
            p.setDoubleBuffered(true);
            frame.add(p);
            frame.setMinimumSize(
                  new Dimension(MIN_W, MIN_H + frameToPaneAdjustment));
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
         }
      });
      frame.setVisible(true);
      // effective EDT
      EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            Agenda main = new Agenda(frame);
            frame.getContentPane().getComponent(0).repaint();
            frame.setTitle(APP_NAME);
            frame.getContentPane().remove(0);
            frame.getContentPane().add(main);
            frame.setExtendedState(JFrame.NORMAL);
            frame.pack();
            frame.setLocationRelativeTo(null);
            log("Program Initialized in " + (System.currentTimeMillis() - start) + " millis\n");
            showWelcome();
            UpdateHandler.updateInquiry();
         }
      });
   }
   
   
   public static void main(String[] args) {
      statusU = true;
      log("Program Initialized");
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            createAndShowGUI();
         }
      });
   }
}
