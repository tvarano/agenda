//Thomas Varano
//[Program Descripion]
//Oct 31, 2017

package information;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Scanner;

public class ErrorTransfer implements Transferable
{
   private String text;
   private Throwable throwObject;
   private DataFlavor flavor; 
   private StringThrowBundle bundle;
   
   public ErrorTransfer(String s, Throwable e) {
      setText(s);
      setThrowObject(e);
      bundle = new StringThrowBundle(s, e);
      flavor = new DataFlavor(StringThrowBundle.class, s);      
   }

   public String getText() {
      return text;
   }
   public void setText(String text) {
      this.text = text;
   }
   public Throwable getThrowObject() {
      return throwObject;
   }
   public void setThrowObject(Throwable throwObject) {
      this.throwObject = throwObject;
   }

   @Override
   public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[] {DataFlavor.stringFlavor, flavor};
   }

   @Override
   public boolean isDataFlavorSupported(DataFlavor flavor) {
      if (flavor.equals(DataFlavor.stringFlavor))
         return true;
      return false;
   }

   @Override
   public Object getTransferData(DataFlavor flavor)
         throws UnsupportedFlavorException, IOException {
      if (flavor.equals(DataFlavor.stringFlavor))
         return bundle.getPrintText();
      return null;
   }
   
   public static class StringThrowBundle implements Serializable{

      private static final long serialVersionUID = -5842582532220560337L;
      private String s;
      private Throwable e;
      public StringThrowBundle(String s, Throwable e) {
         setString(s);
         setThrowable(e);
      }
      
      public Throwable getThrowable() {
         return e;
      }
      public void setThrowable(Throwable e) {
         this.e = e;
      }
      public String getString() {
         return s;
      }
      public void setString(String s) {
         this.s = s;
      }
      public Object getPrintText() {
         String fileRoute = "ErrorSerializeTransfer.txt";
         String ret = "";
         try {
            ObjectOutputStream print = new ObjectOutputStream(new FileOutputStream(fileRoute));
            print.writeObject(this);
            print.close();
         } catch (IOException io) {
            io.printStackTrace();
         }
         try {
            Scanner s = new Scanner(new FileInputStream(fileRoute));
            while (s.hasNextLine())
               ret += "\n" + s.nextLine();
            s.close();
         } catch (FileNotFoundException fnf) {
            fnf.printStackTrace();
         }
         return ret;
      }
      public void printInfo() {
         System.out.println(s);
         e.printStackTrace();
      }
      private String toString(StackTraceElement e) {
         String ret = "";
         ret += "\tat " + e.getMethodName() + "(" + e.getClassName() + "." + e.getLineNumber()+")";
         return ret;
      }
      public String toString() {
         String ret = s + "\n";
         ret += e;
         for (StackTraceElement s : e.getStackTrace())
            ret += toString(s) + "\n";
         if (e.getCause() != null) {
            ret += "caused by: " + e.getCause();
            for (StackTraceElement s : e.getCause().getStackTrace())
               ret += toString(s);
         }
         
         return ret;
      }
   }
}
