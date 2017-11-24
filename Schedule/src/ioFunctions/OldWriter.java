package ioFunctions;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import constants.Lab;
import constants.Rotation;
import information.ClassPeriod;
import information.Schedule;
import managers.Agenda;

//Thomas Varano
//Aug 31, 2017


/**
 * @deprecated this is replaced by {@link SchedWriter}
 * @author varanoth
 * @see {@link SchedWriter}
 */
public class OldWriter 
{
   private PrintWriter pw;
   //TODO app title change
   public static final String FILE_ROUTE = 
         System.getProperty("user.home")+"/Documents/"+Agenda.APP_NAME+"Document.txt";
   private boolean test = false;
   private static boolean debug = true;
   private static boolean statusU = true;
   
   public OldWriter() {
      if (statusU) System.out.println("creating writer");
      String fileRoute;
      if (test)
         fileRoute = System.getProperty("user.home")+"/Documents/testWriter.txt";
      else
         fileRoute = FILE_ROUTE;
      try {
         pw = new PrintWriter(new FileWriter(fileRoute, false));
         if (debug) System.out.println(pw);
      } catch(IOException e1) {e1.printStackTrace();}   }
   
   //writes then closes.
   public void writeSchedule(Schedule s) {
      String output = "";
      ClassPeriod[] c = s.getClasses();
      for (Lab l : s.getLabs())
         pw.print(Lab.toInt(l));
      pw.println(";");
         
      for(int i = 0; i < c.length; i++) {
         if (debug) System.out.println("printing class" +c[i]);
         output = c[i].getSlot()+
               "[" + c[i].getName() + "]"  + c[i].getRoomNumber() + "|" + c[i].getTeacher() + ";";
         if (debug) System.out.println("\tprinting:"+output);
         pw.println(output);
      }
      if (statusU) System.out.println("schedule written");
      close();
   }
   
   public void close() {
      pw.flush();
      pw.close();
   }
   
   public static void main(String[] args) {
      OldWriter w = new OldWriter();
      System.out.println("hey");
      Schedule sched = new Schedule(Rotation.R1.getTimes(), Lab.LAB1);
      w.writeSchedule(sched);
      System.out.println("hey1");
   }
}
