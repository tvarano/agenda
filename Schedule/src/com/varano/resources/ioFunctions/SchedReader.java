package com.varano.resources.ioFunctions;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.varano.information.ClassPeriod;
import com.varano.information.Schedule;
import com.varano.information.constants.ErrorID;
import com.varano.information.constants.Lab;
import com.varano.information.constants.Rotation;
import com.varano.information.constants.RotationConstants;
import com.varano.managers.Agenda;
import com.varano.managers.FileHandler;
import com.varano.managers.OrderUtility;

public class SchedReader {
   private ObjectInputStream reader;
   private String fileRoute;
   private boolean debug;
   
   public SchedReader(String fileRoute) {
   		debug = false;
   		this.fileRoute = fileRoute;
//   		init(fileRoute);
   }
   
   public SchedReader() {
   		this(FileHandler.SCHED_ROUTE);
   }
   
   private void init(String fileRoute) {
   	//read the schedule as normal
      try {
         reader = new ObjectInputStream(new FileInputStream(fileRoute));
         if (debug) System.out.println(new File(fileRoute).getAbsolutePath());
      } catch (IOException e) {
      	// if the file does not exist or something else happens...
         if (debug) e.printStackTrace();
         try {
         	// try reading the old way of holding files and transfer to the new way
         		reader = new ObjectInputStream(new FileInputStream(FileHandler.OLD_SCHED));
         		new SchedWriter().write(this.readSched());
         		reader.close();
         } catch (Exception e1) {
         	// if that doesn't work, probably nothing exists. start from scratch.
	         reWriteSched();
         } finally {
         	//after it all, init
         		init(fileRoute);
         }
      }
   }
   
   public boolean checkValidity() {
   		File file = new File(fileRoute);
   		if (!file.exists()) return false;
   		
   		try {
   			reader = new ObjectInputStream(new FileInputStream(fileRoute));
   			@SuppressWarnings("unused")
				Schedule s = (Schedule)reader.readObject();
   			return true;
   		} catch (Exception e) {
   			return false;
   		}
   }
   
   public Schedule readSched() {
      init(fileRoute);
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
      Agenda.log(ret.getName()+" read from "+fileRoute);
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
         for (int i : RotationConstants.TIME_INSENSITIVE_CLASSES)
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
            else if (s == RotationConstants.PASCACK_STUDY)
               c.setName(RotationConstants.pascackStudyName);
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