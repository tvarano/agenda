//Thomas Varano
//May 10, 2018

package com.varano.managers;

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

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.varano.information.constants.ErrorID;
import com.varano.resources.Addresses;

public class EmailHandler {
   private static boolean debug = true;
   
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
      compose.setPreferredSize(new Dimension(north.getWidth(), 200));
      compose.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
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
      
      if (choice == 0) return false;
      
      String message = "CONTACT AT : "+retField.getText() + "\n\n"
            + "MESSAGE :\n"
            + compose.getText();
      try {
         send(SUBJECT, message);
      } catch (Exception e) {
         return false;
      }
      return true;
   }
   
   public static final String SUBJECT = "Agenda Contact";
   
   private static boolean connect() {
      final long wait = 500;
         try {
            return OrderUtility.futureCall(wait, new Callable<Boolean>() {
               public Boolean call() {
                  try {
                     getSession().getTransport().connect();
                     Agenda.log("email connection successful");
                     return true;
                  } catch (MessagingException e) {
                     Agenda.log("unable to connect for email");
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
      
      return props;
   }
   
   public static void send(String sub, String msg) {
      // Get properties object
      // get Session
      Session session = getSession();
      // compose message
      long start = 0;
      try {
         session.getTransport().connect();
         System.out.println("IT IS GOOD");
      } catch (MessagingException e1) {
         e1.printStackTrace();
      }
      try {
         MimeMessage message = new MimeMessage(session);
         message.addRecipient(Message.RecipientType.TO,
               new InternetAddress(Addresses.CONTACT_EMAIL));
         message.setSubject(sub);
         message.setText(msg);
         // send message
         start = System.currentTimeMillis();
         if (debug) System.out.println("send begun");
         Transport.send(message);
         if (debug) System.out.println("message sent successfully");
         if (debug) System.out.println("send took " + (System.currentTimeMillis() - start));
         JOptionPane.showMessageDialog(null, "message sent");
      } catch (MessagingException e) {
         if (debug) System.out.println("send failed in " + (System.currentTimeMillis() - start));
         JOptionPane.showMessageDialog(null, "failure");
         throw new RuntimeException(e);
      }
   }
   
   public static void main(String[] args) {
//      System.out.println(connect());
      System.out.println(showSendPrompt());
   }
}
