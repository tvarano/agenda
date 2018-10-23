package com.varano.managers;

import java.awt.CardLayout;
import java.awt.CheckboxMenuItem;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.desktop.AppForegroundEvent;
import java.awt.desktop.AppForegroundListener;
import java.awt.desktop.AppHiddenEvent;
import java.awt.desktop.AppHiddenListener;
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
import javax.swing.UIManager;

import com.varano.information.constants.ErrorID;
import com.varano.ui.UIHandler;
import com.varano.ui.input.GPAInput;

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
   public static final String BUILD = "1.9";
   public static final String LAST_UPDATED = "Sept 2018";
   public static final int MIN_W = 733, MIN_H = 360; 
   public static final int PREF_W = MIN_W, PREF_H = 460;
   private PanelManager manager;
   private JFrame parentFrame;
   private MenuBar bar;
   private boolean showNotif;
   private static boolean log;
   public static final boolean fullRelease = true;
   public static final boolean isApp = System.getProperty("user.dir").indexOf(".app") > 0; 
   
   public Agenda(JFrame frame) {
      setName("main class");
           
      FileHandler.initialFileWork();
      log(getClass().getSimpleName()+" began initialization");

      UIHandler.init();  
      try {
         showNotif = readForNotif();
      } catch (IOException e) {
         showNotif = false;
      }
      this.parentFrame = frame;
      configureMenuBar();
      manager = new PanelManager(this, bar);
      manager.setCurrentPane(PanelManager.DISPLAY);
      parentFrame.addWindowListener(new java.awt.event.WindowAdapter() {
         @Override
         public void windowClosing(java.awt.event.WindowEvent windowEvent) {
         		try {
         			manager.aboutToClose();
         			log("program closed");
         			System.exit(0);
         		} catch (java.util.concurrent.CancellationException e) {
         			log("program close cancelled");
         		}
         }
         
      });
      desktopSetup();
      
      toggleGpa(shouldShowGpa());
   }
   
   public static void showWelcome() {
      BufferedReader br;
      try {
         br = new BufferedReader(new FileReader(new File(FileHandler.WELCOME_ROUTE)));
         String line = br.readLine();
         br.close();
         Agenda.log("show welcome: " + line);
         if (line == null || line.equals(FileHandler.TRUE)) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(FileHandler.WELCOME_ROUTE)));
            if (UIHandler.showWelcome())
               bw.write(FileHandler.TRUE);
            else
               bw.write(FileHandler.FALSE);
            bw.close();
         }
      } catch (Exception e) {
         Agenda.logError("error with welcome", e);
      }
   }
   
   private void toggleGpa(boolean show) {
   		Agenda.log("toggle gpa to: "+show);
   		if (GPAInput.show == show) return;
   		GPAInput.show = show;
   		manager.aboutToClose();
   		manager.setCurrentPane(PanelManager.DISPLAY);
   		configureMenuBar();
   		if (show)
   			manager.createGPA();
   }
   
   private void configureMenuBar() {
	   	bar = com.varano.ui.MenuBarHandler.configureMenuBar(parentFrame, this);
      validateThemeChecks();
      validateLookChecks();
   }
   
   public static boolean shouldShowGpa() {
   	 BufferedReader br;
       try {
			br = new BufferedReader(new FileReader(new File(FileHandler.GPA_ROUTE)));
			String line = br.readLine();
			br.close();
			return line.equals(FileHandler.TRUE);
		} catch (IOException e) {
			ErrorID.showError(e, true);
		}
       return false;
   }
   
   private void desktopSetup() {
      if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop()
					.setQuitHandler(new java.awt.desktop.QuitHandler() {
						@Override
						public void handleQuitRequestWith(
								java.awt.desktop.QuitEvent arg0, java.awt.desktop.QuitResponse arg1) {
							try {
								log("program quit registered");
								manager.aboutToClose();
								arg1.performQuit();
							} catch (java.util.concurrent.CancellationException e) {
								log("program quit cancelled");
							}
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
               log("\nSystem awoke on "+java.time.LocalDate.now());
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
            public void appMovedToBackground(AppForegroundEvent arg0) {
            	Agenda.log("dropped to background");
            		manager.getDisplay().showDisp(false);
            }
            @Override
            public void appRaisedToForeground(AppForegroundEvent arg0) {
            	Agenda.log("raised to foreground");
            		manager.getDisplay().showDisp(true);
               manager.update();
            }
         });
         Desktop.getDesktop().addAppEventListener(new AppHiddenListener() {
            @Override
            public void appHidden(AppHiddenEvent arg0) {
            	Agenda.log("app hidden");
         			manager.getDisplay().showDisp(false);
               manager.getDisplay().stop();
            }
            @Override
            public void appUnhidden(AppHiddenEvent arg0) {
            	Agenda.log("app unhidden");
            		manager.getDisplay().showDisp(true);
               manager.getDisplay().resume();
               manager.update();
            }
            
         });
      }
   }
   
   private boolean readForNotif() throws IOException {
      BufferedReader br = null;
      try {
         br = new BufferedReader(new FileReader(FileHandler.NOTIF_ROUTE));
         return br.readLine().equals(FileHandler.TRUE);
      } finally {
         br.close();
      }
   }
   
   public PanelManager getManager() {
      return manager;
   }
   public boolean shouldShowNotif() {
      return showNotif;
   }
   public void setShowNotif(boolean b) {
      showNotif = b;
      try {
         if (showNotif)
         FileHandler.write(FileHandler.TRUE, FileHandler.NOTIF_ROUTE);
         else FileHandler.write(FileHandler.FALSE, FileHandler.NOTIF_ROUTE);
      } catch (IOException e) {
         Agenda.logError("cannot write notification showing", e);
      }
   }
   
   public MenuBar getBar() {
      return bar;
   }
   
	public void validateThemeChecks() {
		Agenda.log("validating theme checkboxes");
		Menu m = (Menu) bar.getMenu(1).getItem(2);
		String currentTheme = UIHandler.readTheme();
		for (int i = 0; i < m.getItemCount(); i++) {
			((CheckboxMenuItem) m.getItem(i)).setState(m.getItem(i).getLabel().equals(currentTheme));
		}
	}
	
	public void validateLookChecks() {
		Agenda.log("validating laf checkboxes");
		Menu m = (Menu) bar.getMenu(1).getItem(3);
		String currentLook = UIManager.getLookAndFeel().getName();
		for (int i = 0; i < m.getItemCount(); i++) {
			((CheckboxMenuItem) m.getItem(i)).setState(m.getItem(i).getLabel().equals(currentLook));
		}
	}
   
   public void show(String name) {
      ((CardLayout) getLayout()).show(this, name);
   } 
   public static void log(String text) {
      if (log)
         System.out.println(LocalTime.now() + " : "+text);
   } 
   public static void logError(String message, Throwable e) {
      if (log)
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
   

   
   
   public static void main(String[] args) {
      log = true;
      log("Program Initialized");
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            ProcessHandler.createAndShowGUI();
         }
      });
   }
}
