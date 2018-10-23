//Thomas Varano
//May 10, 2018

package com.varano.resources.ioFunctions.email;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.varano.information.constants.ErrorID;
import com.varano.managers.Agenda;
import com.varano.managers.ProcessHandler;
import com.varano.resources.Addresses;
import com.varano.ui.UIHandler;

public class EmailHandler {
   private static boolean debug = false;
   
   public static void showThirdPartyUse() {
      Agenda.log("using third party email connection");
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
   
   public static boolean showSendPrompt() {
      if (!connect()) {
         showThirdPartyUse();
         return false;
      }
      
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
      
      if (choice != 1) return false;
      
      String message = "CONTACT AT : "+retField.getText() + "\n\n"
            + "MESSAGE :\n"
            + compose.getText();
      try {
         send(SUBJECT, message);
      } catch (Exception e) {
         return false;
      }
      Agenda.log("email sent");
      return true;
   }
   
   public static final String SUBJECT = "Agenda Contact";
   
   public static boolean connect() {
      final long start = System.currentTimeMillis();
      final long wait = 3000;
         try {
            return ProcessHandler.futureCall(wait, new Callable<Boolean>() {
               public Boolean call() {
                  try {
                     getSession().getTransport("smtp").connect();
                     Agenda.log("email connection successful in "+ (System.currentTimeMillis() - start));
                     return true;
                  } catch (MessagingException e) {
                     Agenda.log("unable to connect for email");
                     if (debug) Agenda.logError("email connection", e);
                     return false;
                  }   
               }
            }, "Connection for Email");
         } catch (Exception e) {
            return false;
         }
   }
     
   private static Session getSession() {
      final String p = "AlphaOmega123";
      return Session.getDefaultInstance(props(),
            new javax.mail.Authenticator() {
         protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(Addresses.CONTACT_EMAIL, p);
         }
      });
   }
   
   private static Properties props() {
      String port = "465";
      
      Properties props = new Properties();
      props.put("mail.smtp.host", "smtp.gmail.com");
      props.put("mail.smtp.socketFactory.port", port);
      props.put("mail.smtp.socketFactory.class",
            "javax.net.ssl.SSLSocketFactory");
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.port", port);
      props.put("mail.smtp.timeout", "10000");    
      props.put("mail.smtp.connectiontimeout", "10000");
      
      //for oauth2
//      props.put("mail.imap.ssl.enable", "true"); // required for Gmail
//      props.put("mail.imap.auth.mechanisms", "XOAUTH2");
      
      return props;
   }
   
   public static void send(String sub, String msg) {
   		send(sub, msg, null);
   }
   
   public static void send(String sub, String msg, String attachmentRoute) {
   	
   		Agenda.log("Sending message...");
      Session session = getSession();
      // compose message
      long start = 0;
      try {
         MimeMessage message = new MimeMessage(session);
         message.addRecipient(Message.RecipientType.TO,
               new InternetAddress(Addresses.CONTACT_EMAIL));
         message.setSubject(sub);
         Agenda.log("Message constructed");
         
         if (attachmentRoute == null || attachmentRoute == "")
         		message.setText(msg);
         else {
         		Agenda.log("Attachment present at "+attachmentRoute);
	         BodyPart messageBodyPart = new MimeBodyPart();
	         messageBodyPart.setText(msg);
	
	         Multipart multipart = new MimeMultipart();
	         multipart.addBodyPart(messageBodyPart);
	
	         messageBodyPart = new MimeBodyPart();
	         DataSource source = new FileDataSource(attachmentRoute);
	         Agenda.log("DataSource at "+source.toString());
	         messageBodyPart.setDataHandler(new DataHandler(source));
	         messageBodyPart.setFileName(attachmentRoute);
	         multipart.addBodyPart(messageBodyPart);
	         message.setContent(multipart);
         }
         
         // send message
         start = System.currentTimeMillis();
         if (debug) System.out.println("send begun");
         Transport.send(message);
         if (debug) System.out.println("message sent successfully");
         if (debug) System.out.println("send took " + (System.currentTimeMillis() - start));
      } catch (MessagingException e) {
         if (debug) System.out.println("send failed in " + (System.currentTimeMillis() - start));
         throw new RuntimeException(e);
      }
      
      //---------USING OAUTH 2.0 ATTEMPT---------
//      Properties props = new Properties();
//      
//      Store store = session.getStore("imap");
//      store.connect("imap.gmail.com", Addresses.CONTACT_EMAIL, oauth2_access_token);
   }
   
//   public static void main(String[] args) {
//      System.out.println(connect());
//      Agenda.statusU = true;
//      System.out.println(showSendPrompt());
//      send("title", "body", "/Users/varanoth/Desktop/AgendaLog.txt");
//   }
}
