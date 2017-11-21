//Thomas Varano
//[Program Descripion]
//Nov 9, 2017

package information;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import constants.ErrorID;

/**
 * @deprecated use the embedded class
 * @author varanoth
 *
 */
public class ErrorTransfer implements Transferable, Serializable
{
   private static final long serialVersionUID = 9144653112308648565L;
   private Throwable e;
   private String id;
   private static boolean debug = false;
   public static final String fileRoute = "Schedule/src/files/ErrorClipBoardTransfer.txt";

   public ErrorTransfer(Throwable e, String id) {
      setThrowable(e); setId(id);
   }
   
   public String getSerializedData() {
      Serializer.serializeDoc(this, fileRoute);
      return readDocument();
   }
   
   public void copy() {
      writeToDoc();
      Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      systemClipboard.setContents(this, null);
   }
   
   public void printStackTrace() {
      e.printStackTrace();
   }
   
   private static String readDocument() {
      Scanner s;
      try {
         s = new Scanner(new File(fileRoute));
      } catch (FileNotFoundException e) {
         e.printStackTrace();
         return "";
      }
      String retval = "";
      final String newLn = "\r\n";
      while (s.hasNextLine())
         retval+=s.nextLine()+newLn;
      s.close();
      retval+="\f";
      if (debug) System.out.println("DOCUMENT "+retval);
      return retval;
   }
   
   public static ErrorTransfer deserialize(String s) {
      return Serializer.deserializeDoc(fileRoute, ErrorTransfer.class);
   }
   
   public static void writeToDoc(String s) {
      if (debug) System.out.println("WRITING "+s);
      try {
         BufferedWriter bw = new BufferedWriter(new FileWriter(fileRoute));
         bw.write(s);
         bw.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   public void writeToDoc() {
      writeToDoc(getSerializedData());
   }
   
   public static ErrorTransfer deserializeFromDocument() {
      return Serializer.deserializeByte(readDocument(), ErrorTransfer.class);
   }
   
   @Override
   public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[] {DataFlavor.stringFlavor};
   }
   
   @Override
   public boolean isDataFlavorSupported(DataFlavor flavor) {
      return flavor.equals(DataFlavor.stringFlavor);
   }
   
   @Override
   public Object getTransferData(DataFlavor flavor)
         throws UnsupportedFlavorException, IOException {
      if (isDataFlavorSupported(flavor))
         return getSerializedData();
      else {
         return null;
      }
   }
   
   public String toString() {
      return getClass().getName()+"["+e.getClass()+" : "+id+"]";
   }
   
   public Throwable getThrowable() {
      return e;
   }
   public void setThrowable(Throwable e) {
      this.e = e;
   }
   public String getId() {
      return id;
   }
   public void setId(String id) {
      this.id = id;
   }
   
   protected static class Serializer {
      /**@deprecated
       * @param obj
       * @return
       */
      public static String serializeUTF(Object obj) {
         OutputStream outAnon = new OutputStream() {
            private StringBuilder sb = new StringBuilder();
            @Override
            public void write(int b) throws IOException {
               sb.append((char)b);
            } 
            public String toString() {
               return sb.toString();
            }
         };
         try {
            ObjectOutputStream out = new ObjectOutputStream(outAnon);
            out.writeObject(obj);
            return outAnon.toString();
         } catch (IOException e) {
            e.printStackTrace();
         }
         return null;
      }
      
      /** @deprecated nono
       * @param str
       * @param cls
       * @return
       */
      public static <T> T deserializeUTF(String str, Class<T> cls) {
         InputStream inUTF = null;
         try {
            inUTF = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8.name()));
         } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
         }
         InputStream inAnon = new InputStream() {
            int index;
            @Override
            public int read() throws IOException {
//               if (index >= str.length())
//                  return 0;
               int ret = (int)str.charAt(index);
               index++;
//               return index;
               return ret;
            }
         };
         ObjectInputStream in;
         try {
            in = new ObjectInputStream(inUTF);
            return cls.cast(in.readObject());
         } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
//            System.out.println("yeah nice try");
            return null;
         }
      }
      
      
      /**
       * Serialize any object using byte array (opposed to UTF)
       * @param obj
       * @return
       */
      public static String serializeByte(Object obj) {
          try {
              ByteArrayOutputStream bo = new ByteArrayOutputStream();
              ObjectOutputStream so = new ObjectOutputStream(bo);
              so.writeObject(obj);
              so.close();
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
      public static <T> T deserializeByte(String str, Class<T> cls) {
          try {
              // This encoding induces a bijection between byte[] and String (unlike UTF-8)
             if (debug) System.out.println("READING "+str);
              byte b[] = str.getBytes("ISO-8859-1"); 
              ByteArrayInputStream bi = new ByteArrayInputStream(b);
              ObjectInputStream si = new ObjectInputStream(bi);
              T retval = cls.cast(si.readObject());
              si.close();
              return retval;
          } catch (Exception e) {
              e.printStackTrace();
              ErrorID.showPrintingError(e);
          }
          return null;
      }
      
      
      public static void serializeDoc(Object o, String fileRoute) {
         File f = new File(fileRoute);
         ObjectOutputStream out;
         try {
            if (debug) System.out.println("WRITEO "+o);
            out = new ObjectOutputStream(new FileOutputStream(f));
            out.writeObject(o);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      public static <T> T deserializeDoc(String fileRoute, Class<T> cls) {
         File f = new File(fileRoute);
         try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
            Object r = in.readObject();
            in.close();
            return cls.cast(r);
         } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
         }
         return null;
      }
   }
}
