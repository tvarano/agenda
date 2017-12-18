package managers;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.MenuBar;
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
import ioFunctions.SchedReader;

//Thomas Varano
//Main class
//Sep 20, 2017


public class Agenda extends JPanel
{
   private static final long serialVersionUID = 1L;
   public static final String APP_NAME = "Agenda";
   public static final String BUILD = "v1.6.4 ß";
   public static final int MIN_W = 733, MIN_H = 360; 
   public static final int PREF_W = MIN_W, PREF_H = 460;
   private PanelManager manager;
   private static JFrame parentFrame;
   private static MenuBar bar;
   public static boolean statusU, inEclipse;
   public static Runnable mainThread;
   public static URI sourceCode;
   
   public Agenda() {
      initialFileWork();
      
      bar = UIHandler.configureMenuBar(parentFrame, this);

      if (statusU) log("Main began initialization");
      UIHandler.init();
      manager = new PanelManager(this, bar);
      manager.setCurrentPane(false);
      parentFrame.addWindowListener(new java.awt.event.WindowAdapter() {
         @Override
         public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            manager.getDisplay().writeMain();
            if (statusU) log("program closed");
            System.exit(0);
         }
      });
   }
   
   /**
    * ensure names, users, etc. Initialize file locations if necessary, draw routes.
    */
   public synchronized void initialFileWork() {
      long start = System.currentTimeMillis();
      try {
         sourceCode = new URI("https://github.com/tvarano54/schedule-new");
      } catch (URISyntaxException e2) {
         ErrorID.showError(e2, true);
      }
      boolean logData = true;

      FileHandler.ensureRouteFile();

      //check parameters, draw routes, create files if needed 
      FileHandler.initAndCreateFiles();

      if (logData) {
         try {
            File log = new File(FileHandler.LOG_ROUTE);
            PrintStream logStream = new PrintStream(log);
            System.setOut(logStream);
            System.setErr(logStream);
         } catch (IOException e) {
            ErrorID.showError(e, true);
         }
      }
      if (statusU) log("filework completed in "+(System.currentTimeMillis()-start));
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
      
      public static boolean ensureRouteFile() {
         return new File(System.getProperty("user.home") + "/Applications/Agenda/")
               .mkdirs();
      }

      public static void initAndCreateFiles() {
         // read file and set/
         String mainFolder = System.getProperty("user.home") + "/Applications/Agenda/"; 
         initFileNames(mainFolder);
         
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
         int choice = JOptionPane.showOptionDialog(null, "Make the subject \"Agenda Contact\"\nMail to varanoth@pascack.org", 
               Agenda.APP_NAME + " Contact", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, 
               new String[] {"Use Desktop", "Use Gmail", "Cancel"}, "Use Desktop");
         if (choice == 2 || choice == -1) 
            return;
         else {
            if (Desktop.isDesktopSupported()) {
               try {
                  if (choice == 0)
                     Desktop.getDesktop().mail(new URI("mailto:varanoth@pascack.org?subject=Agenda%20Contact"));
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
         if (statusU) log("files created");
         if (new File(RESOURCE_ROUTE).mkdirs()) {
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
         if (statusU) log("attempting to move files");
         
         return new File(oldLocation).renameTo(new File(ENVELOPING_FOLDER));
      }
   }
   
   public static MenuBar getBar() {
      return bar;
   }
   
   public static void log(String text) {
      System.out.println(LocalTime.now() + " : "+text);
   }
   
   public static void logError(String message, Throwable e) {
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
      parentFrame = new JFrame(APP_NAME + " " + BUILD);
      int frameToPaneAdjustment = 22;
      parentFrame.setMinimumSize(new Dimension(MIN_W, MIN_H + frameToPaneAdjustment));
      parentFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      parentFrame.setVisible(true);
      parentFrame.setLocationRelativeTo(null);
      Agenda main = new Agenda();
      parentFrame.getContentPane().add(main);
      parentFrame.pack();
      parentFrame.setLocationRelativeTo(null);
      if (statusU)
         log("Program Initialized in " + (System.currentTimeMillis() - start) + " millis");
   }
   public void restart() {
      manager.getDisplay().writeMain();
      if (statusU) log("Program Restarted with no arguments\n");
      restartApplication(new Runnable() {
         @Override
         public void run() {
            if (statusU)
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
         if (statusU) log("restarting...");
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

      // is it a jar file? if not, restart using the classpath way
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
     if (statusU) log("restarting...");
     System.exit(0);
   }

   public static void main(String[] args) {
      statusU = true;
      if (statusU) log("Program Initialized");
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            createAndShowGUI();
         }
      });
   }
}
