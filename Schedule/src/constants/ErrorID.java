package constants;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

import information.ClassPeriod;
import information.ErrorTransfer;
import managers.Main;

//Thomas Varano
//[Program Descripion]
//Oct 24, 2017

public enum ErrorID {
   IO_EXCEPTION("Internal Input / Output Error"),
   NULL_POINT("Internal Null Pointer Error"),
   FNF("File Not Found"),
   INITIALIZER("Exception in Initializer"),
   
   FILE_TAMPER("There was an error with reading your schedule.\n"
               + "It has been reset to the default"),
   INPUT_ERROR("Input Error. Make sure all fields are filled correctly"), 
   OTHER();

   public static final String ERROR_NAME = Main.APP_NAME + " ERROR";
   private final String ID;
   private final String message;

   private ErrorID(String message) {
      this.ID = Integer.toHexString((this.ordinal() + 1) * 10000);
      this.message = message;
   }

   private ErrorID() {
      this("Internal Error");
   }

   public String getID() {
      return ID;
   }

   public static void showRecoverableError(ErrorID error) {
      int choice = showInitialMessage(JOptionPane.WARNING_MESSAGE);
      if (choice == 0)
         JOptionPane.showMessageDialog(null,
               "Details:\n" + error.message + "\nErrorID: " + error.getID(),
               ERROR_NAME, JOptionPane.WARNING_MESSAGE);
   }
   
   private static int showInitialMessage(int messageType) {
      return JOptionPane.showOptionDialog(null,
            "An error has occurred.\nClick \"Info\" for more information.",
            ERROR_NAME, JOptionPane.OK_CANCEL_OPTION, messageType,
            null, new String[]{"Info", "Close"}, "Close");
   }

   public static void showGeneral(Throwable e, String ID) {
      String newLn = "\n";
      int choice = showInitialMessage(JOptionPane.ERROR_MESSAGE);
      if (choice == 0) {
         String message = getType(e).message;
         String internalMessage = (e.getMessage() == null) ? "" : e.getMessage();
         String causeMessage = (e.getCause() == null) ? "" : "Caused by: " + getID(e.getCause());
         String importantText = "ErrorID: " + ID + newLn + causeMessage + newLn + internalMessage;
         String text = "Details:\n" + message + newLn + newLn + importantText;
         JOptionPane.showOptionDialog(null,
               text,
               ERROR_NAME, JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, 
               null, new String[]{"Copy & Close", "Close"}, "Close");
         if (choice == 0) {
            Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            systemClipboard.setContents(new ErrorTransfer(importantText, e), null);
         }
      }
   }

   public static void showError(Throwable e, boolean recover) {
      String ID = getID(e);
      showGeneral(e, ID);
      if (!recover)
         System.exit(0);
   }
   
   public static String getID(Throwable e) {
      return e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber() + 
            ">" + getType(e).getID();
   }
   
   private static ErrorID getType(Throwable e) {
      if (e instanceof NullPointerException)
         return NULL_POINT;
      if (e instanceof FileNotFoundException)
         return FNF;
      if (e instanceof ExceptionInInitializerError)
         return INITIALIZER;
      if (e instanceof IOException)
         return IO_EXCEPTION;
      return OTHER;
   }
   
   public static ErrorID getError(String ID) {
      for (ErrorID e : values())
         if (e.getID().equals(ID))
            return e;
      return null;
   }
   
   public static Throwable deSerialize() {
      String transferDoc = "ErrorSerializeTransfer.txt";
      try {
         ObjectInputStream in = new ObjectInputStream(new FileInputStream(transferDoc));
         ErrorTransfer.StringThrowBundle ret = (ErrorTransfer.StringThrowBundle)in.readObject();
         in.close();
         return ret.getThrowable();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }
      return null;
   }
   
   public static Throwable deSerialize(String s) {
      String transferDoc = "ErrorSerializeTransfer.txt";
      PrintWriter pw;
      try {
         pw = new PrintWriter(transferDoc);
         pw.print(s);
         ObjectInputStream in = new ObjectInputStream(new FileInputStream(transferDoc));
         Throwable ret = (Throwable)in.readObject();
         in.close();
         return ret;
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }
      return null;
   }
   

   public static void main(String[] args) {
//      ClassPeriod c = null;
//      try {
//         c.getName();
//      } catch (NullPointerException e1) {
//         e1.printStackTrace();
//         System.out.println(e1.getStackTrace()[0].getLineNumber());
//         showError(e1, false);
//      }
      deSerialize().printStackTrace();
   }
}
