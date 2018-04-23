package com.varano.resources.ioFunctions;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.varano.information.ClassPeriod;
import com.varano.information.Schedule;
import com.varano.information.Time;
import com.varano.information.constants.ErrorID;
import com.varano.information.constants.Lab;
import com.varano.information.constants.Rotation;
import com.varano.information.constants.RotationConstants;
import com.varano.managers.Agenda;
import com.varano.managers.FileHandler;
import com.varano.managers.OrderUtility;

public class SchedReader {
   private ObjectInputStream reader;
   private boolean debug;
   
   public SchedReader() {
      debug = false;
      init();
   }
   
   private void init() {
      try {
         reader = new ObjectInputStream(new FileInputStream(FileHandler.FILE_ROUTE));
         if (debug) System.out.println(new File(FileHandler.FILE_ROUTE).getAbsolutePath());
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
         ErrorID.showError(e, true);
      }
      close();
      ret = formatSchedule(ret);
      Agenda.log(ret.getName()+" read");
      if (debug) System.out.println("READ 57 SCHED READ gpa " + ret.getGpaClasses().toString());
      ret.sort();
      return ret;
   }
   
   public Schedule formatSchedule(Schedule in) {
      Schedule retval = assignLabClassRooms(setTimes(checkSpecialPeriods(in)));
      retval.init();
      for (ClassPeriod c : retval.getClasses())
         c.calculateDuration();
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
         int s = c.getSlot();
         if (s == 0)
            c.setTimeTemplate(RotationConstants.getPeriodZero());
         else if (s == 8)
            c.setTimeTemplate(RotationConstants.getPeriodEight());
         else if (RotationConstants.isPascack(c)) {
            c.setData(in.getPascackPreferences());
            if (s == RotationConstants.PASCACK)
               c.setTimeTemplate(RotationConstants.getPascack());
            else if (s == RotationConstants.PASCACK_STUDY_1)
               c.setName(RotationConstants.getPascackStudyOne(Time.NO_TIME, Time.NO_TIME).getName());
            else 
               c.setName(RotationConstants.getPascackStudyOne(Time.NO_TIME, Time.NO_TIME).getName());
         } else if (s == RotationConstants.LUNCH)
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
      w.write(RotationConstants.defaultSchedule());
   }
   
}