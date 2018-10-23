//Thomas Varano
//Oct 16, 2018

package com.varano.managers;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.varano.ui.UIHandler;

public class ProcessHandler {
	private ThreadGroup group;
	
	public ProcessHandler() {
		group = new ThreadGroup("initializers");
	}
	
	private LoadingScreen createLoadingScreenHandler(JFrame f, long start) {
		return new LoadingScreen(f, start);
	}
	
	private MainThread createMainThread(JFrame f, long start) {
		return new MainThread(f, start);
	}
	
	private class LoadingScreen extends Thread {
		private JFrame frame;
		private long startTime;
		
		final int frameToPaneAdjustment = 22; 
		public LoadingScreen(JFrame frame, long startTime) {
			super(group, "Loading Screen");
			this.frame = frame;
			this.startTime = startTime;
		}
		
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
               String load = "loading...";
               g2.drawString(load, getWidth() / 2
                     - getFontMetrics(g2.getFont()).stringWidth(load) / 2, 150);
               Agenda.log("Drawing strings took " + (System.currentTimeMillis() - startTime) + "\n");
            }
         };
         p.setDoubleBuffered(true);
         frame.add(p);
         frame.setMinimumSize(
               new Dimension(Agenda.MIN_W, Agenda.MIN_H + frameToPaneAdjustment));
         frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
         frame.pack();
         frame.setLocationRelativeTo(null);
         frame.setVisible(true);
		}
	}
	
	private class MainThread extends Thread {
		private JFrame frame;
		private long start;
		
		public MainThread(JFrame f, long start) {
			super(group, "mainThread");
			frame = f;
			this.start = start;
		}
		
		public void run() {
			final long localStart = System.currentTimeMillis();
			Agenda main = new Agenda(frame);
         frame.getContentPane().getComponent(0).repaint();
         frame.setTitle(Agenda.APP_NAME);
         frame.getContentPane().remove(0);
         frame.getContentPane().add(main);
         frame.setExtendedState(JFrame.NORMAL);
         frame.pack();
         frame.setLocationRelativeTo(null);
         Agenda.log("Second thread completed in " + (System.currentTimeMillis() - localStart) + "\n");
         Agenda.showWelcome();
         UpdateHandler.updateInquiry();
         Agenda.log("Program Initialized in " + (System.currentTimeMillis() - start) + " millis\n");      
		}
	}
	
   static void createAndShowGUI() {
      final long start = System.currentTimeMillis();
      ProcessHandler handler = new ProcessHandler();
      
      JFrame frame = new JFrame(Agenda.APP_NAME+" -- loading...");
      
      LoadingScreen loadingScreen = handler.createLoadingScreenHandler(frame, start);
      MainThread initAgenda = handler.createMainThread(frame, start);
      
      loadingScreen.start();
      initAgenda.start();
   }
   
   
   /**
    * Calls for a future method with timeout capabilities.
    * NOTE: Agenda.logError just prints an error. Change it to whatever you want to print the error.
    * @param millisToWait How long to wait for the method
    * @param method the method to call. The easiest way to make this is an anonymous class. You can change the
    *    parameter in the <> to change the return value
    * @param description the description of the method in logs
    * @return the return value of the <code>method</code> parameter
    * @throws ExecutionException if the method itself is executed incorrectly
    * @throws TimeoutException if the method is timed out, or takes longer than the given millis to wait
    * @throws InterruptedException if the method is interrupted
    */
   public static <T> T futureCall(long millisToWait, java.util.concurrent.Callable<T> method, String description) 
         throws Exception {
      final ExecutorService executor = Executors.newSingleThreadExecutor();
      long start = System.currentTimeMillis();
      // schedule the work

      final Future<T> future = executor
            .submit(method);
      try {
         // wait for task to complete
         final T result = future.get(millisToWait,
               TimeUnit.MILLISECONDS);
         Agenda.log(description + " took " + (System.currentTimeMillis() - start));
         return result;
      } catch (TimeoutException e) {
         Agenda.logError(description + " timed out", e);
         future.cancel(true);
         throw e;
      }
   }
}
