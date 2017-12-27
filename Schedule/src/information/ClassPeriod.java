package information;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

import constants.Lab;
import constants.RotationConstants;
import input.GPAInput;

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
   public static final int NO_WEIGHT = 0, HALF_YEAR = 1, FULL_YEAR = 2, FULL_LAB = 3;
   public static final int DEF_STRING_LENGTH = 20;
   public static final int DEF_STRING_WIDTH = 85;
   private int slot, courseWeight;
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
      if (courseWeight == NO_WEIGHT)
         courseWeight = FULL_YEAR;
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
      if (slot == RotationConstants.NO_SLOT)
         setName("New Class");
   }
   
   public ClassPeriod(String name, Time startTime, Time endTime) {
      this(RotationConstants.NO_SLOT, name, startTime, endTime);
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
      return getClass().getName() + "[" + name + ", slot=" + slot + " ("
            + startTime + "-" + endTime + ") Rm." + roomNumber + ", teacher ="
            + teacher + ", showName =" + showName + "]";

   }
   public void setTimeTemplate(ClassPeriod c) {
      if (c == null) return;
      setStartTime(c.getStartTime()); setEndTime(c.getEndTime());
   }
   public boolean isIncomplete() {
      return (slot == RotationConstants.NO_SLOT || name.equals(" ") || teacher.equals(NO_TEACH) || roomNumber == NO_ROOM);
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
      if (str == null || font == null)
         return 0;
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
      setGrade(c.getGrade()); setCourseWeight(c.getCourseWeight());
   }
   
   public ClassPeriod clone() {
     ClassPeriod c = new ClassPeriod();
     c.setData(this);
     return c;
   }
   
   public double getGrade() {
      return (grade == 0) ? 100 : grade;
   }
   
   public String getLetterGrade() {
      int rg = (int) Math.round(getGrade());
      int grade = 60;
      for (int i = GPAInput.letterGrades.length - 1; i >= 0; i--) {
         if (rg <= grade)
            return GPAInput.letterGrades[i];
         if (i % 3 == 0)
            grade += 3;
         else if (i % 3 == 1)
            grade += 4;
         else
            grade += 3;
      }      
      return "ERROR IN GRADING";
   }

   public void setGrade(double grade) {
      this.grade = grade;
   }

   public void setGrade(String letterGrade) {
      int grade = 97;
      for (int i = 0; i < GPAInput.letterGrades.length; i++) {
         if (letterGrade.equalsIgnoreCase(GPAInput.letterGrades[i]))
            setGrade(grade);
         if (grade == 62)
            grade = 55;
         else if (i % 3 == 0)
            grade -= 2;
         else if (i % 3 == 1)
            grade -= 3;
         else
            grade -= 5;
      }
   }

   public int getCourseWeight() {
      return courseWeight == NO_WEIGHT ? FULL_YEAR : courseWeight;
   }
   public void setCourseWeight(int courseWeight) {
      this.courseWeight = courseWeight;
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

   public static class LabPeriod extends ClassPeriod implements Serializable {
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
