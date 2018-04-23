package com.varano.information.constants;
import java.util.ArrayList;

import com.varano.information.ClassPeriod;
import com.varano.information.Schedule;
import com.varano.information.Time;

//Thomas Varano
//[Program Descripion]
//Sep 12, 2017

/**
 * A class containing all convenience methods and static indexes / references used in rotation.
 * 
 * @see Rotation
 * @author Thomas Varano
 *
 */
public final class RotationConstants
{
   // NOTE unable to code for reliability / flexibility due to the fact that 
   // the numbers need to be final to properly initialize rotations 
   public static final int R1 = 1, R2 = 2, R3 = 3, R4 = 4, ODD_BL= 5, EVEN_BL = 6;
   public static final int HALF_R1 = 7, HALF_R3 = 8, HALF_R4 = 9;
   public static final int DELAY_R1 = 10, DELAY_R3 = 11, DELAY_R4 = 12, DELAY_ODD = 13, DELAY_EVEN = 14;
   public static final int NO_SCHOOL_INDEX = 15, INCORRECT_PARSE = 16, TEST_ONE = 17, TEST_TWO = 18, TEST_THREE = 19, 
         DELAY_ARR = 20, SPECIAL = 21;
   public static final int LUNCH = 9, PASCACK = 10, NO_SCHOOL_TYPE = 11, PASCACK_STUDY_1 = 12, PASCACK_STUDY_2 = 13,
         SPECIAL_OFFLINE_INDEX = 14, PARCC = 15, NO_SLOT = -1;
   public static final int[] SPECIAL_CLASSES = {0, 8, PASCACK};
   
   public static final String[] NAMES = {"R1", "R2", "R3", "R4", "Odd Block", "Even Block", "R1 Half Day", 
         "R3 Half Day", "R4 Half Day", "R1 Delayed Opening", "R3 Delayed Opening", "R4 Delayed Opening",
         "Odd Block Delayed Opening", "Even Block Delayed Opening", "No School", "INCORRECT_PARSE", "Day One", "Day Two", 
         "Day Three", "10:00 Opening", "Special"};
   
   public static final String pascack_1_name = "PAS_STUD_ONE", pascack_2_name = "PAS_STUD_TWO";
   public static final String pascackStudyName = "Pascack Study Period";
   public static final String getName(int rotationIndex) {
      return NAMES[rotationIndex-1];
   }
   
   
   public static Rotation getRotation(String name) {
      for (int i = 0; i < NAMES.length; i++) {
         if (name.equals(NAMES[i]))
            return Rotation.getFromIndex(i+1);
      }
      return null;
   }
   
   public static final Schedule defaultSchedule() {
      return new Schedule(Rotation.R1.getTimes(), new Lab[0]);
   }
   
   public static boolean isPascack(ClassPeriod c) {
      return (c.getSlot() == PASCACK || c.getSlot() == PASCACK_STUDY_1 || c.getSlot() == PASCACK_STUDY_2); 
   }
   
   public static boolean canShowPeriod(ClassPeriod c) {
      return !isPascack(c) && c.getSlot() != LUNCH && c.getSlot() != NO_SCHOOL_TYPE;
   }
   
   public static boolean isZeroFriendly(Rotation r) {
      return !(r.isDelay() || r.isTestDay());
   }
   
   public static boolean isEightFriendly(Rotation r) {
      return !(r.isHalf() || r.getDayType().equals(DayType.TEST_DAY));
   }
   
   public static Schedule getAllClasses(Schedule s) {
      Schedule retval = new Schedule();
      retval.setName(s.getName() + "(all Classes)");
      ArrayList<ClassPeriod> classes = new ArrayList<ClassPeriod>();
      if (s.indexOf(0) >= 0)
         classes.add(getPeriodZero());
      for (ClassPeriod c : Rotation.R1.getTimes())
         classes.add(c);
      if (s.indexOf(8) >= 0)
         classes.add(getPeriodEight());
      classes.add(getPascack());
      retval.setClasses(classes.toArray(new ClassPeriod[classes.size()]));
      return retval;
   }
   
   public static Schedule getNamelessRotation(Schedule s, Rotation r) {
      Schedule retval = new Schedule();
      retval.setName(s.getName()+" (nameless Clone)");
      retval.setClasses(r.getTimes());
      return retval;
   }
   
   public static final ClassPeriod getPeriodZero() {
      return new ClassPeriod(0, "Period 0", new Time(7,15), new Time(7,56));
   }
   
   public static final ClassPeriod getPeriodEight() {
      return new ClassPeriod(8, "Period 8", new Time(14,57), new Time(15,44));
   }
   
   public static final ClassPeriod getPascack() {
      return new ClassPeriod(PASCACK, "Pascack Period", 
            Rotation.ODD_BLOCK.getTimes()[3].getStartTime(), Rotation.ODD_BLOCK.getTimes()[3].getEndTime());
   }
   
   public static final ClassPeriod getPascackStudyOne(Time start, Time end) {
      return new ClassPeriod(
            PASCACK_STUDY_1, pascackStudyName, start, end, ClassPeriod.UNREQ_TEACH, ClassPeriod.NO_ROOM);
   }
   public static final ClassPeriod getPascackStudyTwo(Time start, Time end) {
      return new ClassPeriod(
            PASCACK_STUDY_2, pascackStudyName, start, end, ClassPeriod.UNREQ_TEACH, ClassPeriod.NO_ROOM);
   }
   
   public static final ClassPeriod getSpecialOffline() {
      return new ClassPeriod(SPECIAL_OFFLINE_INDEX, "Error: Offline", Time.MIDNIGHT, Time.BEFORE_MIDNIGHT);
   }
   
   public static final ClassPeriod getParccPeriod(Time start, Time end) {
      return new ClassPeriod(PARCC, "Parcc Testing", start, end);
   }
   
   public static final ClassPeriod getNoSchoolClass() {
      return new ClassPeriod(NO_SCHOOL_TYPE, "No School", Time.MIDNIGHT, new Time(23,59), 
            ClassPeriod.UNREQ_TEACH, ClassPeriod.NO_ROOM);
   }
   
   private static Rotation toDelay0(Rotation r) {
      switch (r) {
         case R1 : return Rotation.DELAY_R1;
         case R3 : return Rotation.DELAY_R3;
         case R4 : return Rotation.DELAY_R4;
         case ODD_BLOCK : return Rotation.DELAY_ODD;
         case EVEN_BLOCK : return Rotation.DELAY_EVEN;
         case DELAY_R1 : case DELAY_R3 : case DELAY_R4 : case DELAY_ODD : case DELAY_EVEN :
            return r;
         default : 
         return Rotation.INCORRECT_PARSE;
      }
   }
   
   public static Rotation toDelay(Rotation r, boolean quiet, Rotation preferred) {
      Rotation ret = toDelay0(r);
      if (ret.equals(Rotation.INCORRECT_PARSE)) {
         if (!quiet)
            ErrorID.showUserError(ErrorID.WRONG_DELAY_SELECTED);
         return preferred;
      }
      return ret;      
   }
   
   public static Rotation toDelay(Rotation r) {
      return toDelay(r, false, Rotation.DELAY_R1);
   }
   
   public static Rotation toHalf(Rotation r, boolean quiet, Rotation preferred) {
      Rotation ret = toHalf0(r);
      if (ret.equals(Rotation.INCORRECT_PARSE)) {
         if (!quiet)
            ErrorID.showUserError(ErrorID.WRONG_HALF_SELECTED);
         return preferred;
      }
      return ret;      
   }
   
   private static Rotation toHalf0(Rotation r) {
      switch (r) {
         case R1 : return Rotation.HALF_R1;
         case R3 : return Rotation.HALF_R3;
         case R4 : return Rotation.HALF_R4;
         case HALF_R1 : case HALF_R3 : case HALF_R4 : return r;
         default : return Rotation.INCORRECT_PARSE;
      }
   }
   
   public static Rotation toHalf(Rotation r) {
      return toHalf(r, false, Rotation.HALF_R1);
   }
   
   public static Rotation todayRotation() {
      return Rotation.getRotation(java.time.LocalDate.now().getDayOfWeek());
   }
   
   public static boolean equalsAllTypes(Rotation a, Rotation b) {
      if (a == null || b == null)
         return false;
      return (a.equals(b) || (toHalf0(a).equals(toHalf0(b)) && !toHalf0(a).equals(Rotation.INCORRECT_PARSE)) 
            || (toDelay0(a).equals(toDelay0(b)) && !toDelay0(a).equals(Rotation.INCORRECT_PARSE)));
   }
   
   public static Rotation toNormal(Rotation r) {
      switch (r) {
         case HALF_R1 : case DELAY_R1 : case DELAY_ARRIVAL : return Rotation.R1;
         case HALF_R3 : case DELAY_R3 : return Rotation.R3;
         case HALF_R4 : case DELAY_R4 : return Rotation.R4;
         case DELAY_ODD : return Rotation.ODD_BLOCK;
         case DELAY_EVEN : return Rotation.EVEN_BLOCK;
         default : return r;
      }
   }
   
   public static final Rotation[] regularRotations() {
      return new Rotation[] {Rotation.R1, Rotation.ODD_BLOCK, Rotation.EVEN_BLOCK, Rotation.R4, Rotation.R3};
   }
   
   public static final Rotation[] halfRotations() {
      ArrayList<Rotation> ret = new ArrayList<Rotation>();
      for (Rotation r : Rotation.values())
         if (r.isHalf())
            ret.add(r);
      return ret.toArray(new Rotation[ret.size()]);
   }
   
   public static final Rotation[] delayRotations() {
      ArrayList<Rotation> ret = new ArrayList<Rotation>();
      for (Rotation r : Rotation.values())
         if (r.isDelay())
            ret.add(r);
      return ret.toArray(new Rotation[ret.size()]);
   }
   
   public static final Rotation[] testRotations() {
      ArrayList<Rotation> ret = new ArrayList<Rotation>();
      for (Rotation r : Rotation.values())
         if (r.isTestDay())
            ret.add(r);
      return ret.toArray(new Rotation[ret.size()]);
   }
   
   public static final Rotation[] specialRotations() {
      ArrayList<Rotation> ret = new ArrayList<Rotation>();
      ret.add(Rotation.R2);
      for (Rotation r : Rotation.values())
         if (r.isOther())
            ret.add(r);
      return ret.toArray(new Rotation[ret.size()]);
   }
   
   public static final String[] categoryNames = {"Normal", "Half Day", "Delayed Opening", "Testing", "Other"};
   public static final Rotation[][] categorizedRotations() {
      return new Rotation[][] {regularRotations(), halfRotations(), delayRotations(), testRotations(), specialRotations()};
   }
}
