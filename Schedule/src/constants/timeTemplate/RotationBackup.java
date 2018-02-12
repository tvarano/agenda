package constants.timeTemplate;

import java.time.DayOfWeek;

import constants.RotationConstants;
import information.ClassPeriod;
import information.Time;

//Thomas Varano
//Sep 3, 2017

/**
 * Depicts the order in which classes go and what times they are scheduled at. 
 * <p>
 * Merely as a template, should not be used alone, but can instantiate default classes with no information but times. 
 * 
 * @author Thomas Varano
 *
 * @see RotationConstants
 */
public enum RotationBackup
{
   R1 (DayTypeBackup.NORMAL),
   R2 (DayTypeBackup.NORMAL),
   R3 (DayTypeBackup.NORMAL),
   R4 (DayTypeBackup.NORMAL),
   ODD_BLOCK (DayTypeBackup.BLOCK), 
   EVEN_BLOCK (DayTypeBackup.BLOCK),
   HALF_R1 (DayTypeBackup.HALF_DAY),
   HALF_R3 (DayTypeBackup.HALF_DAY),
   HALF_R4 (DayTypeBackup.HALF_DAY),
   DELAY_R1 (DayTypeBackup.DELAYED_OPEN),
   DELAY_R3 (DayTypeBackup.DELAYED_OPEN),
   DELAY_R4 (DayTypeBackup.DELAYED_OPEN),
   DELAY_ODD (DayTypeBackup.DELAY_ODD),
   DELAY_EVEN (DayTypeBackup.DELAY_EVEN),
   NO_SCHOOL (DayTypeBackup.NO_SCHOOL),
   INCORRECT_PARSE(DayTypeBackup.NO_SCHOOL),
   TEST_ONE(DayTypeBackup.TEST_DAY),
   TEST_TWO(DayTypeBackup.TEST_DAY),
   TEST_THREE(DayTypeBackup.TEST_DAY),
   DELAY_ARRIVAL(DayTypeBackup.DELAY_ARR);
      
   private final int lunchSlot;
   private final ClassPeriod[] times;
   private final DayTypeBackup dayType;
   private final Time labSwitch;
   private final int index;
   private final static boolean debug = false;
   
   private RotationBackup(DayTypeBackup dt) {
         this.dayType = dt;
         this.times = getSchedule(ordinal()+1); this.labSwitch = dt.getLabSwitch();
         this.index = ordinal()+1; lunchSlot = calcLunchSlot(); 
         if (debug) System.out.println("rotation "+index+" created");
   }
   
   public static int[] getSlotRotation (RotationBackup r) {
      return getSlotRotation(r.index);
   }
   
   private int calcLunchSlot() {
      DayTypeBackup dt = getDayType();
      if (dt.equals(DayTypeBackup.BLOCK)) 
         return 2;
      if (dt.equals(DayTypeBackup.NORMAL))
         return 4;
      return -1;
   }
   
   public static RotationBackup getFromIndex(int rotationIndex) {
      return values()[rotationIndex-1];
   }
   
   private static int[] getSlotRotation (int rotationDay) {
      final int lunch = RotationConstants.LUNCH;
      switch (rotationDay) {
         case RotationConstants.R1 : case RotationConstants.DELAY_R1 :
            return new int[]{1, 2, 3, 4, lunch, 5, 6, 7};
         case RotationConstants.R2 :
            return new int[]{2, 3, 4, 1, lunch, 5, 6, 7};
         case RotationConstants.R3 : case RotationConstants.DELAY_R3 : 
            return new int[]{3, 4, 1, 2, lunch, 6, 7, 5};
         case RotationConstants.R4 : case RotationConstants.DELAY_R4 : 
            return new int[]{4, 1, 2, 3, lunch, 7, 5, 6};
         case RotationConstants.ODD_BL : case RotationConstants.DELAY_ODD : 
            return new int[]{3, 1, lunch, 5, 7};
         case RotationConstants.EVEN_BL :
            return new int[]{2, 4, lunch,
                  RotationConstants.PASCACK, 6};
         case RotationConstants.DELAY_EVEN : 
            return new int[] {2,4,lunch,6};
         case RotationConstants.HALF_R1 : 
            return new int[] {1, 2, 3, 4, 5, 6, 7};
         case RotationConstants.HALF_R3 : 
            return new int[] {3,4,1,2,6,7,5};
         case RotationConstants.HALF_R4 :
            return new int[] {4,1,2,3,7,5,6};
         case RotationConstants.TEST_ONE : 
            return new int[] {1, RotationConstants.PASCACK_STUDY_1, RotationConstants.LUNCH, 5, RotationConstants.PASCACK_STUDY_2};
         case RotationConstants.TEST_TWO : 
            return new int[] {2, 4, RotationConstants.LUNCH, RotationConstants.PASCACK_STUDY_1, 6};
         case RotationConstants.TEST_THREE : 
            return new int[] {3, RotationConstants.PASCACK_STUDY_1, RotationConstants.LUNCH, 7, RotationConstants.PASCACK_STUDY_2};
         case RotationConstants.DELAY_ARR : 
            return new int[] {6, lunch, 2, 4};
         default :
            return new int[0];
      }
   }
   
   /**
    * cannot use a for loop due to the fact that the enum hasn't been initialized in some uses.
    * @param rotationType
    * @return
    */
   private static DayTypeBackup getType(int rotationType) {
      switch(rotationType) {
         case RotationConstants.R1 : case RotationConstants.R2 : case RotationConstants.R3  : case RotationConstants.R4 : 
            return DayTypeBackup.NORMAL;
         case RotationConstants.ODD_BL : case RotationConstants.EVEN_BL :
            return DayTypeBackup.BLOCK;
         case RotationConstants.HALF_R1 : case RotationConstants.HALF_R3 : case RotationConstants.HALF_R4 : 
            return DayTypeBackup.HALF_DAY;
         case RotationConstants.DELAY_R1 : case RotationConstants.DELAY_R3 : case RotationConstants.DELAY_R4 : 
            return DayTypeBackup.DELAYED_OPEN;
         case RotationConstants.DELAY_ODD : 
            return DayTypeBackup.DELAY_ODD;
         case RotationConstants.DELAY_EVEN : 
            return DayTypeBackup.DELAY_EVEN;
         case RotationConstants.NO_SCHOOL_INDEX : 
            return DayTypeBackup.NO_SCHOOL;
         case RotationConstants.TEST_ONE : case RotationConstants.TEST_TWO : case RotationConstants.TEST_THREE :
            return DayTypeBackup.TEST_DAY;
         case RotationConstants.DELAY_ARR :
            return DayTypeBackup.DELAY_ARR;
      }
      return null;
   }

   public static ClassPeriod[] getSchedule(int rotationIndex) {
      if (debug) System.out.println("index="+rotationIndex);
      DayTypeBackup dt = getType(rotationIndex);
      if (debug) System.out.println("nosc "+RotationConstants.getNoSchoolClass());
      if (rotationIndex == RotationConstants.NO_SCHOOL_INDEX) 
         return new ClassPeriod[]{RotationConstants.getNoSchoolClass()};
      int[] slots = getSlotRotation(rotationIndex);
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
   
   public ClassPeriod get(String name) {
      for (ClassPeriod c : times) 
         if (c.getName().equalsIgnoreCase(name))
            return c;
      return null;
   }
   
   public static RotationBackup getRotation(DayOfWeek day) {
      switch (day) {
         case MONDAY : return R1;
         case TUESDAY : return ODD_BLOCK;
         case WEDNESDAY : return EVEN_BLOCK;
         case THURSDAY : return R4;
         case FRIDAY : return R3;
         case SATURDAY : case SUNDAY : 
            return NO_SCHOOL;
         default : return R1;
      }
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
   public int getLunchSlot() {
      return lunchSlot;
   }

   public DayTypeBackup getDayType() {
      return dayType;
   }
   public boolean isDelay() {
      return dayType.equals(DayTypeBackup.DELAYED_OPEN) || dayType.equals(DayTypeBackup.DELAY_EVEN) 
            || dayType.equals(DayTypeBackup.DELAY_ODD) || dayType.equals(DayTypeBackup.DELAY_ARR);
   }
   public boolean isHalf() {
      return dayType.equals(DayTypeBackup.HALF_DAY);
   }
   public boolean isTestDay() {
      return dayType.equals(DayTypeBackup.TEST_DAY);
   }
   public boolean isOther() {
      return !(dayType.equals(DayTypeBackup.NORMAL) || isDelay() || isHalf() || isTestDay());
   }
}