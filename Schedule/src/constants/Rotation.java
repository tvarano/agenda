package constants;

import java.time.DayOfWeek;

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
public enum Rotation
{
   R1 (getSchedule(RotationConstants.R1), DayType.NORMAL, RotationConstants.R1),
   R2 (getSchedule(RotationConstants.R2), DayType.NORMAL, RotationConstants.R2),
   R3 (getSchedule(RotationConstants.R3), DayType.NORMAL, RotationConstants.R3),
   R4 (getSchedule(RotationConstants.R4), DayType.NORMAL, RotationConstants.R4),
   ODD_BLOCK (getSchedule(RotationConstants.ODD_BL), DayType.BLOCK, RotationConstants.ODD_BL), 
   EVEN_BLOCK (getSchedule(RotationConstants.EVEN_BL), DayType.BLOCK, RotationConstants.EVEN_BL),
   HALF_R1 (getSchedule(RotationConstants.HALF_R1), DayType.HALF_DAY, RotationConstants.HALF_R1),
   HALF_R3 (getSchedule(RotationConstants.HALF_R3), DayType.HALF_DAY, RotationConstants.HALF_R3),
   HALF_R4 (getSchedule(RotationConstants.HALF_R4), DayType.HALF_DAY, RotationConstants.HALF_R4),
   DELAY_R1 (getSchedule(RotationConstants.DELAY_R1), DayType.DELAYED_OPEN, RotationConstants.DELAY_R1),
   DELAY_R3 (getSchedule(RotationConstants.DELAY_R3), DayType.DELAYED_OPEN, RotationConstants.DELAY_R3),
   DELAY_R4 (getSchedule(RotationConstants.DELAY_R4), DayType.DELAYED_OPEN, RotationConstants.DELAY_R4),
   DELAY_ODD (getSchedule(RotationConstants.DELAY_ODD), DayType.DELAY_ODD, RotationConstants.DELAY_ODD),
   DELAY_EVEN (getSchedule(RotationConstants.DELAY_EVEN), DayType.DELAY_EVEN, RotationConstants.DELAY_EVEN),
   NO_SCHOOL (getSchedule(RotationConstants.NO_SCHOOL_INDEX), DayType.NO_SCHOOL, RotationConstants.NO_SCHOOL_INDEX),
   INCORRECT_PARSE(new ClassPeriod[0], DayType.NO_SCHOOL, RotationConstants.INCORRECT_PARSE)
   ;
      
   private final int lunchSlot;
   private final ClassPeriod[] times;
   private final DayType dayType;
   private final Time labSwitch;
   private final int index;
   private final static boolean debug = false;
   
   private Rotation(ClassPeriod[] times, DayType dt, int index) {
         this.times = times; this.labSwitch = dt.getLabSwitch(); this.dayType = dt;
         this.index = index; lunchSlot = calcLunchSlot(); 
         if (debug) System.out.println("rotation "+index+" created");
   }
   
   public static int[] getSlotRotation (Rotation r) {
      return getSlotRotation(r.index);
   }
   
   private int calcLunchSlot() {
      DayType dt = getDayType();
      if (dt.equals(DayType.BLOCK)) 
         return 2;
      if (dt.equals(DayType.NORMAL))
         return 4;
      return -1;
   }
   
   public static Rotation getFromIndex(int rotationIndex) {
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
         default :
            return new int[0];
      }
   }
   
   /**
    * cannot use a for loop due to the fact that the enum hasn't been initialized in some uses.
    * @param rotationType
    * @return
    */
   private static DayType getType(int rotationType) {
      switch(rotationType) {
         case RotationConstants.R1 : case RotationConstants.R2 : case RotationConstants.R3  : case RotationConstants.R4 : 
            return DayType.NORMAL;
         case RotationConstants.ODD_BL : case RotationConstants.EVEN_BL :
            return DayType.BLOCK;
         case RotationConstants.HALF_R1 : case RotationConstants.HALF_R3 : case RotationConstants.HALF_R4 : 
            return DayType.HALF_DAY;
         case RotationConstants.DELAY_R1 : case RotationConstants.DELAY_R3 : case RotationConstants.DELAY_R4 : 
            return DayType.DELAYED_OPEN;
         case RotationConstants.DELAY_ODD : 
            return DayType.DELAY_ODD;
         case RotationConstants.DELAY_EVEN : 
            return DayType.DELAY_EVEN;
         case RotationConstants.NO_SCHOOL_INDEX : 
            return DayType.NO_SCHOOL;
            
      }
      return null;
   }

   private static ClassPeriod[] getSchedule(int rotationIndex) {
      if (debug) System.out.println("index="+rotationIndex);
      DayType dt = getType(rotationIndex);
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
   
   public static Rotation getRotation(DayOfWeek day) {
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

   public DayType getDayType() {
      return dayType;
   }
   public boolean isDelay() {
      return dayType.equals(DayType.DELAYED_OPEN) || dayType.equals(DayType.DELAY_EVEN) || dayType.equals(DayType.DELAY_ODD);
   }
   public boolean isHalf() {
      return dayType.equals(DayType.HALF_DAY);
   }
}