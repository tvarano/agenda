package managers;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.MenuBar;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import constants.ErrorID;
import ioFunctions.Reader;

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
   private static PanelManager manager;
   private static JFrame parentFrame;
   private static MenuBar bar;
   public static boolean statusU;
   public static Runnable mainThread;
   //TODO restarting isnt wokring
   
   @SuppressWarnings("resource")
   public Agenda() {
      boolean logData = false;
      //if folder location is unassigned, assign it
      try {
         String mainFolder = null;
         //if fileRoute doesn't exist...
         if (!new Scanner(new File("FolderRoute.txt")).hasNextLine())
            FileHandler.setFileLocation();
         //read file and set
         mainFolder = FileHandler.readFileLocation();
         FileHandler.initFileNames(mainFolder);
      } catch (FileNotFoundException e) {
         ErrorID.showError(e, true);
      }
      //if you need, create your folder. 
      //also, initialize routes
      FileHandler.createFiles();

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

      if (statusU)
         log("Main began initialization");
      UIHandler.init();
	   manager = new PanelManager(this, bar);
	   manager.setCurrentPane(false);
   }
   
   public static class FileHandler {
	   public static String ENVELOPING_FOLDER;
	   public static String RESOURCE_ROUTE;
	   public static String LOG_ROUTE;
	   public static String FILE_ROUTE;
	   public static String THEME_ROUTE, LAF_ROUTE;
	   public static void setFileLocation() {
	       writeFileLocation(askFileLocation());
	       initFileNames(readFileLocation());
	   }
	   public static String askFileLocation() {
	      JFileChooser c = new JFileChooser(System.getProperty("user.home"));
	      c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	      c.setDialogTitle("Choose The Location for Internal Files");
	      int choice;
	      do {
	    	  choice = c.showSaveDialog(null);
	      } while (choice != JFileChooser.APPROVE_OPTION && choice != JFileChooser.CANCEL_OPTION);
	      if (choice == JFileChooser.CANCEL_OPTION)
	    	  System.exit(0);
	      return c.getSelectedFile().getAbsolutePath();
	   }
	   
	   public static String writeFileLocation(String s) {
	      try {
	         BufferedWriter bw = new BufferedWriter(new FileWriter(
	               new File("FolderRoute.txt")));
	         bw.write(s);
	         bw.close();
	      } catch (IOException e) {
	         ErrorID.showError(e, true);
	      }
	      return s;
	   }
	   
	   public static String readFileLocation() {
	      Scanner s = null;
	      try {
	         s = new Scanner(new File("FolderRoute.txt"));
	      } catch (FileNotFoundException e) {
	         e.printStackTrace();
	      }
	      String ret = s.nextLine();
	      s.close();
	      return ret;
	   }
	
	   public static void initFileNames(String envelop) {
	      ENVELOPING_FOLDER = envelop+"/";
	      RESOURCE_ROUTE = ENVELOPING_FOLDER+"InternalData/";
	      LOG_ROUTE = RESOURCE_ROUTE+"AgendaLog.txt";
	      FILE_ROUTE = RESOURCE_ROUTE + "ScheduleHold.txt";
	      THEME_ROUTE = RESOURCE_ROUTE + "theme.txt";
	      LAF_ROUTE = RESOURCE_ROUTE + "look.txt";
	   }
	   
	   public static void createFiles() {
		   if (new File(RESOURCE_ROUTE).mkdirs()) {
		         Reader.transferReadMe(
		               new File(ENVELOPING_FOLDER + "README.txt"));
		         BufferedWriter bw;
		         try {
		            bw = new BufferedWriter(
		                  new FileWriter(RESOURCE_ROUTE + "theme.txt"));
		            bw.write(UIHandler.themes[0]);
		            bw.close();
		            bw = new BufferedWriter(new FileWriter(RESOURCE_ROUTE + "look.txt"));
		            bw.write(UIManager.getSystemLookAndFeelClassName());
		            bw.close();
		         } catch (IOException e) {
		            ErrorID.showError(e, false);
		         }
		      }
	   }
	   
	   /**
	    * delete all files. Needs to be done in order to correctly delete them all.
	    */
	   public static void deleteFiles() {
		   deleteFile(new File(ENVELOPING_FOLDER));
	   }
	   
	   public static boolean deleteFile(File f) {
		   if (f.isDirectory()) {
			   System.out.println(f+" isDirectory");
			   for (File in : f.listFiles()) {
				   boolean del = deleteFile(in);
				   System.out.println(in+" del="+del);
			   }
		   }
		   System.out.println("deleted "+f);
		   return f.delete();
	   }
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
		long start = System.currentTimeMillis();
		parentFrame = new JFrame(APP_NAME + " " + BUILD);
		int frameToPaneAdjustment = 22;
		bar = UIHandler.configureMenuBar(parentFrame);
		parentFrame.setMinimumSize(new Dimension(MIN_W, MIN_H + frameToPaneAdjustment));
		parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		parentFrame.setVisible(true);
		parentFrame.setLocationRelativeTo(null);
		Agenda main = new Agenda();
		parentFrame.getContentPane().add(main);
		parentFrame.pack();
		parentFrame.setLocationRelativeTo(null);
		if (statusU)
			log("Program Initialized in " + (System.currentTimeMillis() - start) + " millis");
	}
   public static void restart() {
      if (statusU) log("Program Restarted\n");
//      close();
//      openFresh();
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
	 * Restart the current Java application</br>
	 * <b>completely copy-pasted but it works like a charm</b>
	 * 
	 * @param runBeforeRestart
	 *            some custom code to be run before restarting
	 * @throws IOException
	 */
	public static void restartApplication(Runnable runBeforeRestart) {
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
			final StringBuffer cmd = new StringBuffer("\"" + java + "\" " + vmArgsOneLine);

			// program main and program arguments
			String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(" ");
			// program main is a jar
			if (mainCommand[0].endsWith(".jar")) {
				// if it's a jar, add -jar mainJar
				cmd.append("-jar " + new File(mainCommand[0]).getPath());
			} else {
				// else it's a .class, add the classpath and mainClass
				cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0]);
			}
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
			// exit
			System.exit(0);
		} catch (Exception e) {
			// something went wrong
			ErrorID.showError(new IOException("Error while trying to restart the application", e), false);
		}
	}
   
	public static void close() {
		parentFrame.dispose();
		manager.dispose();
		bar = null;
	}
	
	public static void openFresh() {
		main(null);
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
