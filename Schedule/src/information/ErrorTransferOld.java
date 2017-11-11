//Thomas Varano
//[Program Descripion]
//Oct 31, 2017

package information;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

import constants.ErrorID;

/**
 * @deprecated 
 * @see {@link ErrorTransfer}
 * @author varanoth
 *
 */
public class ErrorTransferOld implements Transferable, Serializable
{
   private static final long serialVersionUID = 1L;
   private String text;
   private Throwable throwObject;
   private DataFlavor flavor; 
   private StringThrowBundle bundle;
   
   public ErrorTransferOld(String s, Throwable e) {
      setText(s);
      setThrowObject(e);
      bundle = new StringThrowBundle(e, s);
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
   
   /**
    * Serialize any object
    * @param obj
    * @return
    */
   public static String serialize(Object obj) {
       try {
           ByteArrayOutputStream bo = new ByteArrayOutputStream();
           ObjectOutputStream so = new ObjectOutputStream(bo);
           so.writeObject(obj);
           so.flush();
           // This encoding induces a bijection between byte[] and String (unlike UTF-8)
           return bo.toString("ISO-8859-1");
       } catch (Exception e) {
           e.printStackTrace();
       }
       return null;
   }
   /**
    * Deserialize any object
    * @param str
    * @param cls
    * @return
    */
   public static <T> T deserialize(String str, Class<T> cls) {
       // deserialize the object
       try {
           // This encoding induces a bijection between byte[] and String (unlike UTF-8)
           byte b[] = str.getBytes("ISO-8859-1"); 
           ByteArrayInputStream bi = new ByteArrayInputStream(b);
           ObjectInputStream si = new ObjectInputStream(bi);
           return cls.cast(si.readObject());
       } catch (Exception e) {
           e.printStackTrace();
       }
       return null;
   }
   
   
   public static class StringThrowBundle implements Transferable, Serializable {

      private static final long serialVersionUID = -5842582532220560337L;
      private String s;
      private Throwable e;
      public StringThrowBundle(Throwable e, String s) {
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
      public String getPrintText() {
         return serialize(this);
      }
      public void printInfo() {
         System.out.println(s);
         e.printStackTrace();
      }
      private String toString(StackTraceElement e) {
         return "\tat " + e.getMethodName() + "(" + e.getClassName() + "." + e.getLineNumber()+")";
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

      @Override
      public DataFlavor[] getTransferDataFlavors() {
         return null;
      }

      @Override
      public boolean isDataFlavorSupported(DataFlavor flavor) {
         return flavor.equals(DataFlavor.stringFlavor);
      }

      @Override
      public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
         return null;
      }
   }
}
