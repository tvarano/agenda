//Thomas Varano
//Dec 2, 2018

package com.varano.resources.ioFunctions.email;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.mail.MessagingException;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.varano.information.constants.ErrorID;
import com.varano.managers.Agenda;
import com.varano.managers.FileHandler;
import com.varano.resources.Addresses;
import com.varano.ui.UIHandler;

public class OauthEmailHandler {
	
	/*
	 * TODO
	 * 
	 * get credentials for the agenda mailer account
	 * save creds in the helper jar, auto generated. encrypt and decrypt details.
	 * 
	 * auto reply!!!
	 */
	
	
	
	
	public static boolean hasConnection() {
		try {
			new URL("https://mail.google.com/").openConnection();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void sendEmail() {
		if (!hasConnection()) {
			ErrorID.showUserError(ErrorID.NO_INTERNET);
			return;
		}
		
	}
	
	public static String requestBody() {
		JPanel content = new JPanel(new BorderLayout());
      JPanel north = new JPanel();
      JLabel prompt = new JLabel("Your email address:");
      JTextField retField = new JTextField();
      retField.setPreferredSize(new Dimension(225, UIHandler.FIELD_HEIGHT));
      JTextArea compose = new JTextArea();
      compose.setPreferredSize(new Dimension(content.getWidth(), 200));
      compose.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
      compose.setBackground(Color.white);
      final int gap = 5;
      compose.setMargin(new Insets(gap, gap, gap, gap));
      compose.setTabSize(3);
      
      north.add(prompt);
      north.add(retField);
      content.add(north, BorderLayout.NORTH);
      content.add(compose, BorderLayout.CENTER);
      
      int choice = JOptionPane.showOptionDialog(
            null, content, "Contact", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, new String[] {"Cancel", "Send"}, "Cancel");
      
      if (choice != 1) return null;
      
      String message = "CONTACT AT : "+retField.getText() + "\n\n"
            + "MESSAGE :\n"
            + compose.getText();
      return message;
	}
	
	public static boolean sendMessage(String to, String subject, String body) throws IOException, MessagingException {
		EmailWriter.writeEmail(EmailWriter.createEmail(to, subject, body), new File(FileHandler.MAIL_TRANSFER));
		Process p = new ProcessBuilder(
				FileHandler.J_COMMAND_EXEC, FileHandler.MAILER_ROUTE, FileHandler.MAIL_TRANSFER).start();
//		p.waitFor();
		return p.exitValue() == 0;
	}
	
//	public static boolean sendMessage(String to, String subject, String body) {
//		return sendMessage(to, subject, body, null);
//	}
//	
	public static boolean sendMessage(String subject, String body) throws IOException, MessagingException {
		return sendMessage(Addresses.CONTACT_EMAIL, subject, body);
	}
	
	public void sendAutomatedResponse(String to) {
		try {
			sendMessage(to, Agenda.APP_NAME + ": Response Recieved",
					"Hi,\n"
					+ "This is an automated confirmation that your email has been received.\n"
					+ "Thank you for using" +  Agenda.APP_NAME + ". We value your feedback!\n"
					+ "- Tom");
		} catch (IOException | MessagingException e) {
			Agenda.logError("exception in response message", e);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(requestBody());
	}
}
