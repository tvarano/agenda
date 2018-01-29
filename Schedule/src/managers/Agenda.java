package managers;

import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MenuBar;
import java.awt.RenderingHints;
import java.awt.desktop.ScreenSleepEvent;
import java.awt.desktop.ScreenSleepListener;
import java.awt.desktop.SystemSleepEvent;
import java.awt.desktop.SystemSleepListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import constants.ErrorID;
import information.Addresses;
import ioFunctions.SchedReader;
import resources.ResourceAccess;

//Thomas Varano
//Main class
//Sep 20, 2017

/**
 * Main class. Begins the program and initializes all references.
 * 
 * @author Thomas Varano
 */
public class Agenda extends JPanel
{
   private static final long serialVersionUID = 1L;
   public static final String APP_NAME = "Agenda";
   public static final String BUILD = "v1.7.1 (Beta)";
   public static final int MIN_W = 733, MIN_H = 360; 
   public static final int PREF_W = MIN_W, PREF_H = 460;
   private PanelManager manager;
   private JFrame parentFrame;
   private MenuBar bar;
   public static boolean statusU;
   public static URI sourceCode;
   
   public Agenda(JFrame frame) {
      setName("main class");
      initialFileWork();
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
               manager.getDisplay().hardStop();
            }
            @Override
            public void systemAwoke(SystemSleepEvent arg0) {
               manager.getDisplay().hardResume();
               manager.getDisplay().checkAndUpdateTime();
               manager.getDisplay().checkAndUpdateDate();
            }
         });
         Desktop.getDesktop().addAppEventListener(new ScreenSleepListener() {
            @Override
            public void screenAboutToSleep(ScreenSleepEvent arg0) {
               manager.getDisplay().stop();
            }
            @Override
            public void screenAwoke(ScreenSleepEvent arg0) {
               manager.getDisplay().resume();
            }
         });
      }
   }
   
   /**
    * ensure names, users, etc. Initialize file locations if necessary, draw routes.
    */
   public static void initialFileWork() {
      long start = System.currentTimeMillis();
      try {
         sourceCode = new URI("https://github.com/tvarano54/schedule-new");
      } catch (URISyntaxException e2) {
         ErrorID.showError(e2, true);
      }
      boolean logData = true;

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
            log ("streams set to "+FileHandler.LOG_ROUTE);
         } catch (IOException e) {
            ErrorID.showError(e, true);
         }
      } else {
         log("logging to console / terminal");
      }
      //logs the time taken (in millis)
      log("filework completed in "+(System.currentTimeMillis()-start));
   }
   
   public PanelManager getManager() {
      return manager;
   }
   
   /**
    * Everything that has to handle files
    * @author varanoth
    */
   public static class FileHandler {
      public static String ENVELOPING_FOLDER;
      public static String RESOURCE_ROUTE;
      public static String LOG_ROUTE;
      public static String FILE_ROUTE;
      public static String THEME_ROUTE, LAF_ROUTE;
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
      
      public static boolean ensureFileRoute() {
         return new File(Addresses.AGENDA_HOME)
               .mkdirs();
      }

      public static void initAndCreateFiles() {
         // read file and set/
         initFileNames(Addresses.AGENDA_HOME);
         
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
      
      public static void sendEmail() {
         int choice = JOptionPane.showOptionDialog(null, "Make the subject \"Agenda Contact\"\nMail to "+ 
               Addresses.CONTACT_EMAIL, 
               Agenda.APP_NAME + " Contact", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, 
               new String[] {"Use Desktop", "Use Gmail", "Cancel"}, "Use Desktop");
         if (choice == 2 || choice == -1) 
            return;
         else {
            if (Desktop.isDesktopSupported()) {
               try {
                  if (choice == 0)
                     Desktop.getDesktop().mail(new URI("mailto:"+Addresses.CONTACT_EMAIL+"?subject=Agenda%20Contact"));
                  else
                     Desktop.getDesktop().browse(new URI("https://mail.google.com/mail/u/0/#inbox?compose=new"));
               } catch (IOException | URISyntaxException e1) {
                  ErrorID.showError(e1, true);
               }
            }
         }
      }
   
      public static void initFileNames(String envelop) {
         ENVELOPING_FOLDER = envelop;
         RESOURCE_ROUTE = ENVELOPING_FOLDER+"InternalData/";
         LOG_ROUTE = RESOURCE_ROUTE+"AgendaLog.txt";
         FILE_ROUTE = RESOURCE_ROUTE + "ScheduleHold.txt";
         THEME_ROUTE = RESOURCE_ROUTE + "theme.txt";
         LAF_ROUTE = RESOURCE_ROUTE + "look.txt";
      }
      
      public synchronized static void createFiles() {
         if (new File(RESOURCE_ROUTE).mkdirs()) {
            log("files created");
               SchedReader.transfer("README.txt",
                     new File(ENVELOPING_FOLDER + "README.txt"));
               BufferedWriter bw;
               try {
                  bw = new BufferedWriter(
                        new FileWriter(THEME_ROUTE));
                  bw.write(UIHandler.themes[0]);
                  bw.close();
                  bw = new BufferedWriter(new FileWriter(LAF_ROUTE));
                  bw.write(UIManager.getSystemLookAndFeelClassName());
                  bw.close();
               } catch (IOException e) {
                  ErrorID.showError(e, false);
               }
            }
      }
      
      public static boolean moveFiles(String oldLocation) {
         log("attempting to move files");
         
         return new File(oldLocation).renameTo(new File(ENVELOPING_FOLDER));
      }
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
         System.err.println(LocalTime.now() + " : ERROR: " + message + " : \n\t" + e.getMessage());
   }
   
   public Dimension getMinimumSize() {
      return new Dimension(MIN_W,MIN_H);
   }
   public Dimension getPreferredSize() {
      return new Dimension(PREF_W, PREF_H);
   }
   
   private static void createAndShowGUI() {
      long start = System.currentTimeMillis();
      JFrame frame = new JFrame("LOADING....");
      int frameToPaneAdjustment = 22;
      
      
      // loading screen, frame adjustments
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            JPanel p = new JPanel() {
               private static final long serialVersionUID = 1L;
               int dots = 3;
               
               @Override 
               public void paintComponent(Graphics g) {
                  super.paintComponent(g);
                  Graphics2D g2 = (Graphics2D) g;
                  g2.addRenderingHints(new RenderingHints(
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
                  g2.drawImage((Image) ResourceAccess.getImage("loading.gif").getImage(), 0, 0, 100, 100, null);
                  g2.setFont(UIHandler.font.deriveFont(36F).deriveFont(Font.BOLD));
                  String s = "LOADING";
                  for (int i = 0; i < dots; i++)
                     s += ".";
                  g2.drawString(s, 260, 150);
                  dots++;
                  log("Drawing strings took " + (System.currentTimeMillis() - start));
               }
            };
            frame.add(p);
            frame.setMinimumSize(new Dimension(MIN_W, MIN_H + frameToPaneAdjustment));
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
            frame.setTitle(APP_NAME + " " + BUILD);
            frame.getContentPane().remove(0);
            frame.getContentPane().add(main);
            frame.pack();
            frame.setLocationRelativeTo(null);
            log("Program Initialized in " + (System.currentTimeMillis() - start) + " millis");
         }
      });
   }
   public void restart() {
      manager.getDisplay().writeMain();
      log("Program Restarted with no arguments\n");
      restartApplication(new Runnable() {
         @Override
         public void run() {
            log("Restart Successful.\n");
         }
      });
   }
   
   /**
    * Sun property pointing the main class and its arguments. Might not be defined
    * on non Hotspot VM implementations.
    */
   public static final String SUN_JAVA_COMMAND = "sun.java.command";

   /**
    * Restart the current Java application
    * however, only if the program is run through eclipse or with a classPath
    * 
    * @param runBeforeRestart
    *            some custom code to be run before restarting
    * @throws IOException
    */
   public void restartAppCP(Runnable runBeforeRestart) {
      try {
         // java binary
         String java = System.getProperty("java.home") + "/bin/java";
         // vm arguments
         List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
         StringBuffer vmArgsOneLine = new StringBuffer();
         for (String arg : vmArguments) {
            // if it's the agent argument : we ignore it otherwise the
            // address of the old application and the new one will be in conflict
            if (!arg.contains("-agentlib")) {
               vmArgsOneLine.append(arg);
               vmArgsOneLine.append(" ");
            }
         }
         // init the command to execute, add the vm args
         final StringBuffer cmd = new StringBuffer("" + java + " " + vmArgsOneLine);

         // program main and program arguments
         String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(" ");
         // only running if its a classpath
         cmd.append("-cp " + System.getProperty("java.class.path") + " " + mainCommand[0]);
         // finally add program arguments
         for (int i = 1; i < mainCommand.length; i++) {
            cmd.append(" ");
            cmd.append(mainCommand[i]);
         }
         // execute the command in a shutdown hook, to be sure that all the
         // resources have been disposed before restarting the application
         Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
               try {
                  Runtime.getRuntime().exec(cmd.toString());
               } catch (IOException e) {
                  e.printStackTrace();
               }
            }
         });
         // execute some custom code before restarting
         if (runBeforeRestart != null) {
            runBeforeRestart.run();
         }
         log("restarting...");
         // exit
         System.exit(0);
      } catch (Exception e) {
         // something went wrong
         ErrorID.showError(new ExecutionException("Error while trying to restart the application", e), false);
      }
   }
  
   public void restartApplication(Runnable runBeforeRestart) {
      final String javaBin = System.getProperty("java.home") + File.separator
            + "bin" + File.separator + "java";
      File currentJar = null;
      try {
         currentJar = new File(Agenda.class.getProtectionDomain()
               .getCodeSource().getLocation().toURI());
      } catch (URISyntaxException e) {
         ErrorID.showError(e, false);
      }

      // if not a jar, restart using the classpath way
      if (!currentJar.getName().endsWith(".jar")) {
         restartAppCP(runBeforeRestart);
      }

      // Build command: java -jar application.jar
      final ArrayList<String> command = new ArrayList<String>();
      command.add(javaBin);
      command.add("-jar");
      command.add(currentJar.getPath());

      final ProcessBuilder builder = new ProcessBuilder(command);
      try {
         builder.start();
      } catch (IOException e) {
         ErrorID.showError(e, false);
      }
      // execute the command in a shutdown hook, to be sure that all the
      // resources have been disposed before restarting the application
      Runtime.getRuntime().addShutdownHook(new Thread() {
         @Override
         public void run() {
            try {
               Runtime.getRuntime().exec(builder.toString());
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      });
      // run the custom code
      if (runBeforeRestart != null) {
         runBeforeRestart.run();
      }
     log("restarting...");
     System.exit(0);
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
