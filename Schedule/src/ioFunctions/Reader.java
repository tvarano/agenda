package ioFunctions;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

import constants.ErrorID;
import constants.Lab;
import constants.Rotation;
import constants.RotationConstants;
import information.ClassPeriod;
import information.Schedule;
import managers.Agenda;

public class Reader {
   private ObjectInputStream reader;
   private boolean debug;
   
   public Reader() {
      debug = false;
      init();
   }
   
   private void init() {
      try {
         reader = new ObjectInputStream(new FileInputStream(SchedWriter.FILE_ROUTE));
         if (debug) System.out.println(new File(SchedWriter.FILE_ROUTE).getAbsolutePath());
      } catch (IOException e) {
         if (debug) e.printStackTrace();
         reWriteSched();
         init();
      }
   }
   
   public Schedule readSched() {
      init();
      Schedule ret = null;
      try {
         ret = (Schedule) reader.readObject();
      } catch (ClassNotFoundException | ClassCastException | IOException e) {
         reWriteSched();
         ErrorID.showUserError(ErrorID.FILE_TAMPER);
         return readSched();
      }
      try {
         reader.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
      close();
      ret = formatSchedule(ret);
      if (Agenda.statusU) Agenda.log(ret.getName()+" read");
      return ret;
   }
   
   public Schedule formatSchedule(Schedule in) {
      Schedule retval = assignLabClassRooms(setTimes(checkSpecialPeriods(in)));
      retval.init();
      return retval;
   }
   
   private Schedule setTimes(Schedule in) {
      ClassPeriod[] template = Rotation.R1.getTimes();
      ClassPeriod[] ret = in.getClasses();
      boolean skip;
      int tempIndex = 0;
      for (int index = 0; index < ret.length; index++) {
         skip = false;
         for (int i : RotationConstants.SPECIAL_CLASSES)
            if (ret[index].getSlot() == i) {
               skip = true;
            }
         if (skip)
            continue;
         try {
            ret[index].setTimeTemplate(template[tempIndex]);
         } catch(ArrayIndexOutOfBoundsException e) {
            ErrorID.showError(e, false);
         }
         tempIndex++;
      }
      in.calculateSchoolDay();
      return in;
   }
   
   private Schedule checkSpecialPeriods(Schedule in) {
      for (ClassPeriod c : in.getClasses()) {
         if (c.getSlot() == 0)
            c.setTimeTemplate(RotationConstants.PERIOD_ZERO);
         else if (c.getSlot() == 8)
            c.setTimeTemplate(RotationConstants.PERIOD_EIGHT);
         else if (c.getSlot() == RotationConstants.PASCACK)
            c.setTimeTemplate(RotationConstants.PASCACK_PERIOD);
         else if (c.getSlot() == RotationConstants.LUNCH)
            c.setName("Lunch");
      }
      return in;
   }
   
   private Schedule assignLabClassRooms(Schedule in) {
      in.setLabs(findLabClassRooms(in.getLabs(), in));
      return in;
   }
   
   private Lab[] findLabClassRooms(Lab[] labs, Schedule in) {
      for (Lab l : labs) {
         if (l != null) {
            if (debug) System.out.println(l + " called "+in.get(l.getClassSlot()));
            l.setClassPreferences(in.get(l.getClassSlot()));
            if (debug) System.out.println(l + "=" + l.getTimeAtLab().getInfo());
         }
      }
      return labs;
   }
   
   public Schedule readAndOrderSchedule(Rotation r) {
      Schedule s = readSched();
      return OrderUtility.reorderClasses(r, s, s.getClasses());
   }
   
   private void close() {
      try {
         reader.close();
      } catch (IOException e) {
         e.printStackTrace();
         ErrorID.showError(e, false);
      }
   }
   
   public void reWriteSched() {
      if (debug) System.out.println("rewriting sched");
      SchedWriter w = new SchedWriter();
      w.write(new Schedule(Rotation.R1.getTimes(), Lab.LAB1));
   }
   
   public static void transferReadMe(File f) {
      if (Agenda.statusU) Agenda.log("transferring readme");
      try {
         Scanner in = new Scanner(new File("README.md"));
         BufferedWriter bw = new BufferedWriter(new FileWriter(f));
         while (in.hasNextLine()) {
            bw.write(in.nextLine()+"\r\n");
         }
         f.setWritable(false);
         in.close();
         bw.close();
      } catch (IOException e) {
         ErrorID.showError(e, true);
      }
   }
   
   public static void main(String[] args) {
      Reader r = new Reader();
      System.out.println(r.readSched().getLabs());
   } 
}