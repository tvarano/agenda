//Thomas Varano
//Sep 4, 2018

package com.varano.resources.ioFunctions;

import com.varano.managers.EmailHandler;
import com.varano.managers.FileHandler;

public class ErrorReport {
	
	public static void sendError(String msg, Throwable e) {
		new Thread(new Sender(msg, e)).run();
	}
	
	private static class Sender implements Runnable {
		private String msg;
		private Throwable e;
		
		public Sender(String msg, Throwable e) {
			this.msg = msg;
			this.e = e;
		}
		
		public void sendError() {
			if (!EmailHandler.connect()) return;
			String completeMessage = "";
			completeMessage += java.time.LocalTime.now() + ", " + java.time.LocalDate.now()
					+ "\n\nUser: " + System.getProperty("user.name") + "\nMessage: ";
			completeMessage += (msg == null || msg.equals("")) ? "null" : msg;
			completeMessage += "\n\n";
			completeMessage += "Exception: " + e.getClass().getName() + " - "+ e.getMessage();
			
			EmailHandler.send("AGENDA ERROR", completeMessage, FileHandler.LOG_ROUTE);
		}

		@Override
		public void run() {
			sendError();
		}
	}
	
}
