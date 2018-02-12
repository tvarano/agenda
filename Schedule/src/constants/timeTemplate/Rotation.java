//Thomas Varano
//Feb 11, 2018

package constants.timeTemplate;

import java.util.ArrayList;

import constants.RotationConstants;
import constants.timeTemplate.DayType;
import information.ClassPeriod;
import information.Time;

@Deprecated
public class Rotation {
   private static ArrayList<Rotation> types;
   private DayType dayType;
   private RotationBackup backup;
   private int lunchSlot;
   private ClassPeriod[] times;
   private Time labSwitch;
   private int index;
   private static boolean debug;
   
   private Rotation(RotationBackup backup, DayType dt) {
      this.dayType = dt;
      this.backup = backup;
      index = backup.ordinal() + 1;
   }
   
   public static void init() {
      debug = true;
      types = new ArrayList<Rotation>();
      for (RotationBackup b : RotationBackup.values()) {
         types.add(new Rotation(b, DayType.get(b.getDayType())));
      }
   }
   
   public static final Rotation get(constants.Rotation backup) {
      return types.get(backup.ordinal()-1);
   }
   
   public static ClassPeriod[] getSchedule(int rotationIndex) {
      if (debug) System.out.println("index="+rotationIndex);
      DayType dt = getType(rotationIndex);
      if (debug) System.out.println("nosc "+RotationConstants.getNoSchoolClass());
      if (rotationIndex == RotationConstants.NO_SCHOOL_INDEX) 
         return new ClassPeriod[]{RotationConstants.getNoSchoolClass()};
      int[] slots = RotationBackup.getSlotRotation(RotationBackup.getFromIndex(rotationIndex));
      ClassPeriod[] retval = new ClassPeriod[slots.length];
      String name = "";
      for (int i = 0; i < retval.length; i++){
         retval[i] = new ClassPeriod(slots[i]);
         if (slots[i] == RotationConstants.LUNCH) {
            name = "Lunch";
            retval[i].setRoomNumber("Cafe");
         }
         else if (slots[i] == RotationConstants.PASCACK)
            name = "Pascack Period";
         else if (slots[i] == RotationConstants.PASCACK_STUDY_1)
            name = RotationConstants.pascack_1_name;
         else if (slots[i] == RotationConstants.PASCACK_STUDY_2) {
            name = RotationConstants.pascack_2_name;
         }
         else 
            name = "Period " + slots[i];
         retval[i].setName(name); retval[i].setStartTime(
               dt.getStartTimes()[i]); 
         retval[i].setEndTime(dt.getEndTimes()[i]);
         retval[i].calculateDuration();
      }
      return retval;
   }
   
   private static DayType getType(int rotationType) {
      switch(rotationType) {
         case RotationConstants.R1 : case RotationConstants.R2 : case RotationConstants.R3  : case RotationConstants.R4 : 
            return DayType.normal();
         case RotationConstants.ODD_BL : case RotationConstants.EVEN_BL :
            return DayType.block();
         case RotationConstants.HALF_R1 : case RotationConstants.HALF_R3 : case RotationConstants.HALF_R4 : 
            return DayType.halfDay();
         case RotationConstants.DELAY_R1 : case RotationConstants.DELAY_R3 : case RotationConstants.DELAY_R4 : 
            return DayType.delayedOpen();
         case RotationConstants.DELAY_ODD : 
            return DayType.delayOdd();
         case RotationConstants.DELAY_EVEN : 
            return DayType.delayEven();
         case RotationConstants.NO_SCHOOL_INDEX : 
            return DayType.noSchool();
         case RotationConstants.TEST_ONE : case RotationConstants.TEST_TWO : case RotationConstants.TEST_THREE :
            return DayType.testDay();
         case RotationConstants.DELAY_ARR :
            return DayType.delayArrival();
      }
      return null;
   }

   public static final Rotation get(RotationBackup backup) {
      return types.get(backup.ordinal() - 1);
   }
   
   /*
    * Convenience methods for all of the rotations
    */
   public static final Rotation r1() {
      return get(RotationBackup.R1);
   }
   public static final Rotation r2() {
      return get(RotationBackup.R2);
   }
   public static final Rotation r3() {
      return get(RotationBackup.R3);
   }
   public static final Rotation r4() {
      return get(RotationBackup.R4);
   }
   public static final Rotation oddBlock() {
      return get(RotationBackup.ODD_BLOCK);
   }
   public static final Rotation evenBlock() {
      return get(RotationBackup.EVEN_BLOCK);
   }
   public static final Rotation halfR1() {
      return get(RotationBackup.HALF_R1);
   }
   public static final Rotation halfR3() {
      return get(RotationBackup.HALF_R3);
   }
   public static final Rotation halfR4() {
      return get(RotationBackup.HALF_R4);
   }
   public static final Rotation delayR1() {
      return get(RotationBackup.DELAY_R1);
   }
   public static final Rotation delayR3() {
      return get(RotationBackup.DELAY_R3);
   }
   public static final Rotation delayR4() {
      return get(RotationBackup.DELAY_R4);
   }
   public static final Rotation delayOdd() {
      return get(RotationBackup.DELAY_ODD);
   }
   public static final Rotation delayEven() {
      return get(RotationBackup.DELAY_EVEN);
   }
   public static final Rotation noSchool() {
      return get(RotationBackup.NO_SCHOOL);
   }
   public static final Rotation testOne() {
      return get(RotationBackup.TEST_ONE);
   }
   public static final Rotation testTwo() {
      return get(RotationBackup.TEST_TWO);
   }
   public static final Rotation testThree() {
      return get(RotationBackup.TEST_THREE);
   }
   public static final Rotation delayArrival() {
      return get(RotationBackup.DELAY_ARRIVAL);
   }
   
   public static ArrayList<Rotation> types() {
      return types;
   }
   public DayType getDayType() {
      return dayType;
   }
   public RotationBackup getBackup() {
      return backup;
   }
   public int getLunchSlot() {
      return lunchSlot;
   }
   public ClassPeriod[] getTimes() {
      return times;
   }
   public Time getLabSwitch() {
      return labSwitch;
   }
   public int getIndex() {
      return index;
   }
}
