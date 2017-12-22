package information;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

import constants.Lab;
import constants.RotationConstants;

//Thomas Varano
//Aug 31, 2017

/**
 * Class to define a class period. Pretty simple, just name, start / end time and the slot its in. 
 * @author varanoth
 *
 */
public class ClassPeriod implements Comparable<ClassPeriod>, Serializable
{
   private static final long serialVersionUID = -8853513886388469596L;
   public static final String NO_TEACH = "Teacher Name";
   public static final String NO_ROOM = "000";
   public static final int FULL_YEAR = 0, HALF_YEAR = 1;
   public static final int DEF_STRING_LENGTH = 20;
   public static final int DEF_STRING_WIDTH = 150;
   private int slot, courseLength;
   private double grade;
   private static final FontRenderContext frc = new FontRenderContext(new AffineTransform(),true,true);
   private String roomNumber, memo;
   private boolean showName, canShowPeriod, honors;
   private static boolean debug = false;
   private String name, teacher;
   private Time startTime, endTime, duration;
   
   /**
    * Creates ClassPeriod. Would have called it {@link Class} but can't because it exists.
    * @param slot 
    *    the period slot the class is in 
    * @param name 
    *    name of the class. i.e. "CP US History I"
    * @param startTime
    *    time it starts
    * @param endTime
    *    time it ends
    */
   public ClassPeriod(int slot, String name, Time startTime, Time endTime, String teacher, String roomNumber) {
      setSlot(slot); setName(name); setStartTime(startTime); setEndTime(endTime); 
      setTeacher(teacher); setRoomNumber(roomNumber); setShowName(true); 
      setCanShowPeriod(slot != RotationConstants.LUNCH && slot != RotationConstants.PASCACK);
      if (debug)
         System.out.println("INITIALIZED"+getInfo());
      duration = Time.calculateDuration(startTime, endTime);  
   }
   
   public ClassPeriod(int slot, String name, String teacher, String roomNumber) {
      this(slot, name, new Time(), new Time(), teacher, roomNumber);
   }
   
   public ClassPeriod(int slot, String name, Time startTime, Time endTime) {
      this(slot, name, startTime, endTime, NO_TEACH, NO_ROOM);
   }
   public ClassPeriod(int slot) {
      this(slot, "Period "+slot, NO_TEACH, NO_ROOM);
   }
   
   public ClassPeriod(String name, Time startTime, Time endTime) {
      this(-1, name, startTime, endTime);
   }
   
   public ClassPeriod(ClassPeriod anchor) {
      this(anchor.getSlot(), anchor.getName(), anchor.getStartTime(), anchor.getEndTime(), anchor.getTeacher(),
            anchor.getRoomNumber());
   }
   
   public void calculateDuration() {
      duration = Time.calculateDuration(startTime, endTime);        
   }
   public boolean contains(Time t) {
      return (t.getTotalMins() >= startTime.getTotalMins() && t.getTotalMins() < endTime.getTotalMins());
   }
   public ClassPeriod() {
      this(" ", new Time(), new Time());
   }
   
   public ClassPeriod(String name) {
      this(name, new Time(), new Time());
   }

   public static String trimString(String s) {
      return trimString(s, DEF_STRING_LENGTH);
   }
   
   public static String trimString(String s, int len) {
      if (s.length() > len) {
         if (debug) System.out.println("trimmed string "+s+" to "+len+"characters");
         return s.substring(0, len)+"...";
      }
      return s;
   }
   public String getInfo() {
    return getClass().getName()+"[" + name + " slot="+slot+"("+startTime+"-"+endTime+") Rm." +roomNumber+ " showName="+showName+"]";

   }
   public void setTimeTemplate(ClassPeriod c) {
      if (c == null) return;
      setStartTime(c.getStartTime()); setEndTime(c.getEndTime());
   }
   public boolean isIncomplete() {
      return (slot == -1 || name.equals(" ") || teacher.equals(NO_TEACH) || roomNumber == NO_ROOM);
   }
   public String toString() {
      return (showName || !canShowPeriod) ? getTrimmedName() : "Period "+slot;
   }
   
   public String formattedString(Font font, int preferredSize) {
      final int prefW = (preferredSize == -1) ? DEF_STRING_WIDTH : preferredSize;
      String ret = getTrimmedName();
      while (stringWidth(ret, font) < prefW) {
         ret = " " + ret;
      }
      return ret;
   }
   
   private static int stringWidth(String str, Font font) {
      return (int)(font.getStringBounds(str, frc).getWidth());
      
   }
   
   public int nameWidth(Font font) {
      return stringWidth(getTrimmedName(), font);
   }
   
   public int getSlot() {
      return slot;
   }
   public void setSlot(int slot) {
      this.slot = slot;
   }
   public String getTrimmedName() {
      return trimString(getName());
   }
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public Time getStartTime() {
      return startTime;
   }
   public void setStartTime(Time startTime) {
      this.startTime = startTime;
   }
   public Time getEndTime() {
      return endTime;
   }
   public void setEndTime(Time endTime) {
      this.endTime = endTime;
   }
   public Time getDuration() {
      return duration;
   }
   public String getTrimmedRoomNumber() {
      return trimString(getRoomNumber());
   }
   public String getRoomNumber() {
      return roomNumber;
   }
   public void setRoomNumber(String roomNumber) {
      this.roomNumber = roomNumber;
   }
   public String getTrimmedTeacher() {
      return trimString(getTeacher());
   }
   public String getTeacher() {
      return teacher;
   }
   public void setTeacher(String teacher) {
      this.teacher = teacher;
   }
   public String getMemo() {
      if (memo == null)
         return "";
      return memo;
   }

   public void setMemo(String memo) {
      this.memo = memo;
   }

   public boolean isShowName() {
      return showName;
   }
   public void setShowName(boolean showName) {
      this.showName = showName;
   }
   public boolean canShowPeriod() {
      return canShowPeriod;
   }
   public void setCanShowPeriod(boolean canShowPeriod) {
      this.canShowPeriod = canShowPeriod;
   }
   public void setData(ClassPeriod c) {
      setName(c.getName()); setRoomNumber(c.getRoomNumber()); setTeacher(c.getTeacher()); setSlot(c.getSlot());
      setGrade(c.getGrade());
   }
   
   public double getGrade() {
      return (grade == 0) ? 100 : grade;
   }
   
   public String getLetterGrade() {
      //TODO return letter grade based on number
      double rg = Math.round(getGrade());
      if (rg > 98)
         return "A+";
//      else if (grade > )
      return "F";
   }

   public void setGrade(double grade) {
      this.grade = grade;
   }
   
   public void setGrade(String letterGrade) {
      //TODO do grade settign
   }

   public int getCourseLength() {
      return courseLength;
   }

   public void setCourseLength(int courseLength) {
      this.courseLength = courseLength;
   }

   public boolean isHonors() {
      return honors;
   }

   public void setHonors(boolean honors) {
      this.honors = honors;
   }

   public boolean equals(ClassPeriod c) {
      if (c == null)
         return false;
      return c.getName() == name 
            && c.getSlot() == slot &&
            c.getStartTime().equals(startTime) && c.getEndTime().equals(endTime) && 
            c.getTeacher().equals(teacher);
      }

   @Override
   public int compareTo(ClassPeriod c) {
      return getSlot() - c.getSlot();
   }

   public static class LabPeriod extends ClassPeriod implements Serializable{
      private static final long serialVersionUID = 1L;
      private Lab lab;
      
      public LabPeriod(Lab l, ClassPeriod anchor) {
         super(anchor);
         setName("LAB");
         setLab(l);
      }
      
      public LabPeriod (int slot, String name, Time startTime, Time endTime, Lab l) {
         super(slot, name, startTime, endTime);
         setLab(l);
      }

      public Lab getLab() {
         return lab;
      }
      public void setLab(Lab lab) {
         this.lab = lab;
      }
   }
}
