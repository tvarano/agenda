package ioFunctions;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import constants.Lab;
import constants.Rotation;
import constants.RotationConstants;
import information.ClassPeriod;
import information.Schedule;

//Thomas Varano
//Aug 31, 2017

/**
 * @deprecated this is replaced by {@link Reader}
 * @author varanoth
 *
 */
public class OldReader
{
   private File schedDoc;
   private static boolean debug = false, statusU = false;
   
   public OldReader() {
      this.schedDoc = new File(OldWriter.FILE_ROUTE);
   }
   
   public Schedule readDefaultSchedule() {
      try {
         Scanner s = new Scanner(schedDoc);
         if (statusU) System.out.println("reading: "+OldWriter.FILE_ROUTE);
         return readAndReturn(s);
      }
      catch (FileNotFoundException e) {
         if (statusU){
            System.err.println(e);
            System.out.println("creating file:" + OldWriter.FILE_ROUTE);
         }
         rewriteSched();
      }
      if (statusU) System.out.println("reader RETRYING READ");
      return readDefaultSchedule();
   }
   
   private void rewriteSched() {
      OldWriter w = new OldWriter();
      Schedule defSched = new Schedule(Rotation.R1.getTimes(), Lab.LAB1);
      w.writeSchedule(defSched);
   }
   
   public Schedule readAndOrderSchedule(Rotation r) {
      Schedule sched = readDefaultSchedule();
      sched.setClasses(OrderUtility.reorderClasses(r, sched.getClasses()));
      return sched;
   }
      
   private Schedule readAndReturn(Scanner s) {
      if (debug) System.out.println("read called");
      if (!s.hasNextLine())
         rewriteSched();
      String line = s.nextLine();
      Lab[] labs = readLabs(line);
      ClassPeriod[] classArray = readClasses(s);
      Schedule retval = new Schedule(classArray, labs);
      if (debug) System.out.println("sched created");
      retval.setLabs(assignLabClassRooms(labs, retval));
      return retval;
   }
   
   private Lab[] readLabs(String line) {
      if (debug) System.out.println("labs read");
      Lab[] labs;
      if (!line.equals(";")) {
         int labSlot = Integer.parseInt(line.substring(0, line.indexOf(';')));
         int labSlotLength = line.substring(0,line.indexOf(';')).length();
         labs = new Lab[labSlotLength];
         for (int index = 0; index < labs.length; index++) {
            labs[index] = Lab.toLab(labSlot%10);
            labSlot /= 10;
         }
         if (debug) System.out.println("lab "+labSlot+"="+labs[0]);
      }
      else labs = new Lab[0];
      if (debug) System.out.println(labs[0]);
      return labs;
   }
   
   private Lab[] assignLabClassRooms(Lab[] labs, Schedule s) {
      if (debug) System.out.println("LABS ASSIGNED");
      for (Lab l : labs) {
         if (debug) System.out.println(l + " called "+s.get(l.getClassSlot()));
         l.setClassPreferences(s.get(l.getClassSlot()));
         if (debug) System.out.println(l + "=" + l.getTimeAtLab().getInfo());
      }
      return labs;
   }
   
   private ClassPeriod[] readClasses(Scanner s) {
      if (debug) System.out.println("classes read");
      String line = "";
      ArrayList<ClassPeriod> classes = new ArrayList<ClassPeriod>();
      int timeIndex = 0;
      ClassPeriod timeTemplate = null;
      while (s.hasNextLine()) {
         line = s.nextLine();
         if (debug)
            System.out.println("LINE" + line);
         int slot = Integer.parseInt(line.charAt(0)+"");
         String className = line.substring(line.indexOf('[')+1,
               line.indexOf(']'));
         String roomNumber = line.substring(line.indexOf(']')+1, line.indexOf('|'));
         String teacher = line.substring(line.indexOf('|')+1, line.indexOf(';'));
         if (slot == 0) 
            timeTemplate = RotationConstants.PERIOD_ZERO;         
         else if (slot == 8)
            timeTemplate = RotationConstants.PERIOD_EIGHT;
         else {
            timeTemplate = Rotation.R1.getTimes()[timeIndex];
            timeIndex++;
         }
         classes.add(new ClassPeriod(slot, className, timeTemplate.getStartTime(), timeTemplate.getEndTime(), teacher, roomNumber));
         if (debug) System.out.println("\tclassPeriod"+slot+"= "+classes.get(classes.size()-1));
      }
      ClassPeriod[] classArray = new ClassPeriod[0];
      classArray = classes.toArray(classArray);
      return classArray;
   }

   
   public File getSchedDoc() {
      return schedDoc;
   }
   public void setSchedDoc(File schedDoc) {
      this.schedDoc = schedDoc;
   }

   public static void main(String[] args) {
      OldReader r = new OldReader();
//      Schedule s = r.readDefaultSchedule();
      Schedule s = r.readAndOrderSchedule(Rotation.EVEN_BLOCK);
      System.out.println("FINAL ORDER:");
      for (ClassPeriod c : s.getClasses()) {
         System.out.println("\t" +c);
      }
      for (Lab l : s.getLabs()) {
         System.out.println(l);
         System.out.println("per"+l.getTimeAtLab().getInfo());
      }
   }
}
