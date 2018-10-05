package com.varano.information.constants;

import java.net.URL;
import java.time.DayOfWeek;

import com.varano.information.ClassPeriod;
import com.varano.information.Time;
import com.varano.managers.Agenda;
import com.varano.managers.OrderUtility;
import com.varano.resources.Addresses;

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
   R1 (DayType.NORMAL, false),
   R2 (DayType.NORMAL, false),
   R3 (DayType.NORMAL, false),
   R4 (DayType.NORMAL, false),
   ODD_BLOCK (DayType.BLOCK, false), 
   EVEN_BLOCK (DayType.BLOCK, false),
   HALF_R1 (DayType.HALF_DAY, false),
   HALF_R3 (DayType.HALF_DAY, false),
   HALF_R4 (DayType.HALF_DAY, false),
   DELAY_R1 (DayType.DELAYED_OPEN, false),
   DELAY_R3 (DayType.DELAYED_OPEN, false),
   DELAY_R4 (DayType.DELAYED_OPEN, false),
   DELAY_ODD (DayType.DELAY_ODD, true),
   DELAY_EVEN (DayType.DELAY_EVEN, true),
   NO_SCHOOL (DayType.NO_SCHOOL, false),
   INCORRECT_PARSE(DayType.NO_SCHOOL, false),
   TEST_ONE(DayType.TEST_DAY, true),
   TEST_TWO(DayType.TEST_DAY, true),
   TEST_THREE(DayType.TEST_DAY, true),
   DELAY_ARRIVAL(DayType.DELAY_ARR, true),
   SPECIAL(DayType.SPECIAL, true),
   FLIP_EVEN_BLOCK(DayType.BLOCK, false);
      
   private final int lunchSlot;
   private ClassPeriod[] times;
   private final DayType dayType;
   private final Time labSwitch;
   private final int index;
   private final boolean readReccomended;
   private final static boolean debug = false;
   
   private Rotation(DayType dt, boolean readReccomended) {
      this.readReccomended = readReccomended;
      this.dayType = dt; this.labSwitch = dt.getLabSwitch();
      this.index = ordinal()+1; lunchSlot = calcLunchSlot(); 
      try {
         if (readReccomended)
            onlineInit();
         else 
            offlineInit();
      } catch (Exception e) {
         Agenda.logError("error with "+name(), e);
         offlineInit();
      }
      if (debug) System.out.println("rotation "+index+" created");
   }
   
   public static int[] getSlotRotation (Rotation r) {
      return getSlotRotation(r.index);
   }
   
   public int[] slotRotation() {
      int[] ret = new int[times.length];
      for (int i = 0; i < ret.length; i++)
         ret[i] = times[i].getSlot();
      return ret;
   }
   
   private void offlineInit() {
      this.times = getSchedule(ordinal()+1);      
   }
   
   //-------------------------------------- Online Initialization -------------------------------------------
   
   public static void reread() {
      for (Rotation r : values()) {
         if (r.readReccomended)
            try {
               r.onlineInit();
            } catch (Exception e) {
               r.offlineInit();
            }
      }
   }
   
   private void onlineInit() throws Exception {
      Agenda.log("online start "+name() + " at " +getSite());
      times = getSchedule(formatString(retrieveHtml(getSite())), dayType);
      
      if (debug) { 
         System.out.println("--------------------"+name() + "TIMES-----------------");
         for (ClassPeriod c : times) {
            System.out.println(c.getInfo());
         }
         System.out.println("daytype "+dayType);
      }
   }
   
   private int[] formatString(String unf) throws Exception {
      java.util.Scanner s = new java.util.Scanner(unf);
      int[] slots = new int[dayType.getStartTimes().length];
      for (int i = 0; i < slots.length; i++)
         slots[i] = Integer.parseInt(s.nextLine());
      s.close();
      return slots;
   }
   
   private static final int MILLIS_TO_WAIT = 300;
   private static String retrieveHtml(URL site) throws Exception {
      return OrderUtility.futureCall(MILLIS_TO_WAIT, new java.util.concurrent.Callable<String>() {
         @Override
         public String call() throws Exception {
            return com.varano.resources.ResourceAccess.readHtml(site);
         }
      }, "retreieve dayType");
   }
   
   public java.net.URL getSite() {
      return Addresses.createURL(Addresses.ROTATION_HOME + name().toLowerCase() + ".txt");
   }
   
   //--------------------------------------------------------------------------------------------------------
   
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
         case RotationConstants.TEST_ONE : 
            return new int[] {1, RotationConstants.PASCACK_STUDY, RotationConstants.LUNCH, 5, RotationConstants.PASCACK_STUDY};
         case RotationConstants.TEST_TWO : 
            return new int[] {2, 4, RotationConstants.LUNCH, RotationConstants.PASCACK_STUDY, 6};
         case RotationConstants.TEST_THREE : 
            return new int[] {3, RotationConstants.PASCACK_STUDY, RotationConstants.LUNCH, 7, RotationConstants.PASCACK_STUDY};
         case RotationConstants.DELAY_ARR : 
            return new int[] {6, lunch, 2, 4};
         case RotationConstants.SPECIAL :
            return new int[] {RotationConstants.SPECIAL_OFFLINE_INDEX};
         case RotationConstants.FLIP_EVEN_BLOCK : 
         		return new int[] {2, 4, lunch, 6, RotationConstants.PASCACK}; 
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
         case RotationConstants.ODD_BL : case RotationConstants.EVEN_BL : case RotationConstants.FLIP_EVEN_BLOCK : 
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
         case RotationConstants.TEST_ONE : case RotationConstants.TEST_TWO : case RotationConstants.TEST_THREE :
            return DayType.TEST_DAY;
         case RotationConstants.DELAY_ARR :
            return DayType.DELAY_ARR;
         case RotationConstants.SPECIAL : 
            return DayType.SPECIAL;
      }
      return null;
   }

   public static ClassPeriod[] getSchedule(int rotationIndex) {
      if (debug) System.out.println("index="+rotationIndex);
      if (debug) System.out.println("nosc "+RotationConstants.getNoSchoolClass());
      if (rotationIndex == RotationConstants.NO_SCHOOL_INDEX) 
         return new ClassPeriod[]{RotationConstants.getNoSchoolClass()};
      return getSchedule(getSlotRotation(rotationIndex), getType(rotationIndex));
   }
   
   public static ClassPeriod[] getSchedule(int[] slots, DayType dt) {
      ClassPeriod[] retval = new ClassPeriod[slots.length];
      String name = "";
      for (int i = 0; i < retval.length; i++){
         retval[i] = new ClassPeriod(slots[i]);
         if (slots[i] == RotationConstants.LUNCH) {
            name = "Lunch";
            retval[i].setRoomNumber("Cafe");
            retval[i].setTeacher(ClassPeriod.UNREQ_TEACH);
         }
         else if (slots[i] == RotationConstants.PASCACK)
            name = "Pascack Period";
         else if (slots[i] == RotationConstants.PASCACK_STUDY)
            name = RotationConstants.pascackStudyName;
         else if (slots[i] == RotationConstants.PASCACK_STUDY) 
            name = RotationConstants.pascackStudyName;
         else if (slots[i] == RotationConstants.SPECIAL_OFFLINE_INDEX)
            name = RotationConstants.getSpecialOffline().getName();
         else 
            name = "Period " + slots[i];
         retval[i].setName(name); retval[i].setStartTime(
               dt.getStartTimes()[i]); 
         retval[i].setEndTime(dt.getEndTimes()[i]);
         retval[i].calculateDuration();
      }
      return retval;
   }
   
   public ClassPeriod get(int slot) {
      for (ClassPeriod c : times) 
         if (c.getSlot() == slot)
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
      return dayType.equals(DayType.DELAYED_OPEN) || dayType.equals(DayType.DELAY_EVEN) 
            || dayType.equals(DayType.DELAY_ODD) || dayType.equals(DayType.DELAY_ARR);
   }
   public boolean isHalf() {
      return dayType.equals(DayType.HALF_DAY);
   }
   public boolean isTestDay() {
      return dayType.equals(DayType.TEST_DAY);
   }
   public boolean isOther() {
      return !(dayType.equals(DayType.NORMAL) || dayType.equals(DayType.BLOCK)|| isDelay() || isHalf() || isTestDay());
   }
}