package constants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.time.LocalTime;

import javax.swing.JOptionPane;

import information.ClassPeriod;
import information.ErrorTransfer;
import ioFunctions.SchedWriter;
import managers.Main;
import managers.UIHandler;

//Thomas Varano
//[Program Descripion]
//Oct 24, 2017

public enum ErrorID {
   IO_EXCEPTION("Internal Input / Output Error"),
   NULL_POINT("Internal Null Pointer Error"),
   FNF("File Not Found"),
   INITIALIZER("Exception in Initializer"),
   SERIALIZE("Data Corruption in Reading / Writing Process"),
   
   FILE_TAMPER("There was an error with reading your schedule.\n"
               + "It has been reset to the default"),
   INPUT_ERROR("Input Error. Make sure all fields are filled correctly"), 
   HALF_BLOCK_SELECTED("You selected a block half day, which does not exist.\n"
         + "The rotation has been set to a half day R1."),
   OTHER();

   public static final String ERROR_NAME = Main.APP_NAME + " ERROR";
   public static final String fileRoute = "Schedule/src/files/ErrorClipBoardTransfer.txt";
   private final String ID;
   private final String message;
   private static boolean debug = true;

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

   public static void showUserError(ErrorID error) {
      if (Main.statusU) Main.logError("User Error "+error+"\n");
      int choice = showInitialMessage(JOptionPane.WARNING_MESSAGE);
      if (choice == 0)
         JOptionPane.showMessageDialog(null,
               "Details:\n" + error.message + "\nErrorID: " + error.getID(),
               ERROR_NAME, JOptionPane.WARNING_MESSAGE);
   }
   
   private static int showInitialMessage(int messageType) {
      String defMessage = "An error has occurred.\nClick \"Info\" for more information.";
      String usrMessage = "A user "+defMessage.substring(3);
      String message = (messageType == JOptionPane.ERROR_MESSAGE) ? 
            defMessage : usrMessage;
      return JOptionPane.showOptionDialog(null,
            message,
            ERROR_NAME, JOptionPane.OK_CANCEL_OPTION, messageType,
            null, new String[]{"Info", "Close"}, "Close");
   }

   public static void showGeneral(Throwable e, String ID, boolean copy) {
      if (Main.statusU) {System.err.print(LocalTime.now() + " : "); e.printStackTrace();}
      String newLn = "\n";
      int choice = showInitialMessage(JOptionPane.ERROR_MESSAGE);
      if (choice == 0) {
         String message = getType(e).message;
         String internalMessage = (e.getMessage() == null) ? "" : e.getMessage() + newLn;
         String causeMessage = (e.getCause() == null) ? "" : "Caused by: " + getID(e.getCause());
         String importantText = "ErrorID: " + ID + newLn + causeMessage + newLn + internalMessage;
         String prompt = "Go to" + newLn + SchedWriter.LOG_ROUTE + "\nFor your log data.";
         String text = "Details:\n" + message + newLn + importantText + prompt;
         JOptionPane.showOptionDialog(null,
               text,
               ERROR_NAME, JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, 
               null, (copy) ? new String[]{"Copy & Close", "Close"} : new String[] {"Close"}, "Close");
         if (choice == 0 && copy) {
            ErrorTransfer t = new ErrorTransfer(e, importantText);
            t.copy();
         }
      }
   }

   public static void showError(Throwable e, boolean recover) {
      String ID = getID(e);
      showGeneral(e, ID, true);
      if (!recover)
         System.exit(0);
   }
   
   public static String getID(Throwable e) {
      return e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber() + 
            ">" + getType(e).getID();
   }
   
   public static void showPrintingError(Throwable e) {
      showGeneral(e, getID(e), false);
   }
   
   private static ErrorID getType(Throwable e) {
      if (e instanceof StreamCorruptedException || e instanceof ClassNotFoundException)
         return SERIALIZE;
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
//      String transferDocRoute = "files/ErrorClipBoardTransfer.txt";
//      try {
//         ObjectInputStream in = new ObjectInputStream(new FileInputStream(transferDoc));
//         ErrorTransfer.StringThrowBundle ret = (ErrorTransfer.StringThrowBundle)in.readObject();
//         in.close();
//         return ret.getThrowable();
//      } catch (IOException e) {
//         e.printStackTrace();
//      } catch (ClassNotFoundException e) {
//         e.printStackTrace();
//      }
//      return null;
//      
      return ErrorTransfer.deserializeFromDocument().getThrowable();
   }

   public static void main(String[] args) {
      boolean create = true;
      boolean read = true;
      if (create) {
         UIHandler.init();
         ClassPeriod c = null;
         try {
            c.getName();
         } catch (NullPointerException e1) {
            if (read) {
               ErrorTransfer test = new ErrorTransfer(e1, getType(e1).getID());
               test.copy();
               System.out.println("SER DATA "+ test.getSerializedData());
               System.out.println("\nSER TEST "+ ErrorTransfer.deserializeFromDocument());
            }
            ErrorID.showError(e1, true);
            if (read)
               System.out.println("\nSER TEST 2 "+ErrorTransfer.deserializeFromDocument());
         }
         catch (Exception e) {
            showPrintingError(e);
         }
      }
      else {
         ErrorTransfer.deserializeFromDocument();
      }
   }
}
