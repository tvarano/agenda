package constants;
import java.util.ArrayList;

import information.ClassPeriod;
import information.Schedule;
import information.Time;
import managers.Agenda;

//Thomas Varano
//[Program Descripion]
//Sep 12, 2017

public final class RotationConstants
{
   public static final int R1 = 1, R2 = 2, R3 = 3, R4 = 4, ODD_BL= 5, EVEN_BL = 6;
   public static final int HALF_R1 = 7, HALF_R3 = 8, HALF_R4 = 9, 
         DELAY_R1 = 10, DELAY_R3 = 11, DELAY_R4 = 12, DELAY_ODD = 13, DELAY_EVEN = 14;
   public static final int LUNCH = 9, PASCACK = 10;
   public static final int[] SPECIAL_CLASSES = {0, 8, PASCACK};
   
   private static final String[] NAMES = {"R1", "R2", "R3", "R4", "Odd Block", "Even Block", "R1 Half Day", 
         "R3 Half Day", "R4 Half Day", "R1 Delayed Opening", "R3 Delayed Opening", "R4 Delayed Opening"
         };
   
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
      return new Schedule(Rotation.R1.getTimes(), Lab.LAB1);
   }
   
   public static Schedule getAllClasses(Schedule s) {
      Schedule retval = new Schedule();
      retval.setName(s.getName() + "(all Classes)");
      ArrayList<ClassPeriod> classes = new ArrayList<ClassPeriod>();
      if (s.indexOf(0) >= 0)
         classes.add(PERIOD_ZERO);
      for (ClassPeriod c : Rotation.R1.getTimes())
         classes.add(c);
      if (s.indexOf(8) >= 0)
         classes.add(PERIOD_EIGHT);
      classes.add(PASCACK_PERIOD);
      retval.setClasses(classes.toArray(new ClassPeriod[classes.size()]));
      return retval;
   }
   
   public static Schedule getNamelessRotation(Schedule s, Rotation r) {
      Schedule retval = new Schedule();
      retval.setName(s.getName()+" (nameless Clone)");
      retval.setClasses(r.getTimes());
      return retval;
   }
   
   public static final ClassPeriod PERIOD_ZERO = new ClassPeriod(0, "Period 0", new Time(7,15), new Time(7,56)),
         PERIOD_EIGHT = new ClassPeriod(8, "Period 8", new Time(14,57), new Time(15,44)), 
         PASCACK_PERIOD = new ClassPeriod(RotationConstants.PASCACK, "Pascack Period", 
               Rotation.ODD_BLOCK.getTimes()[3].getStartTime(), Rotation.ODD_BLOCK.getTimes()[3].getEndTime());
   
   public static final ClassPeriod getPascack() {
      ClassPeriod retval = PASCACK_PERIOD;
      retval.setCanShowPeriod(false);
      return retval;
   }
   
   public static Rotation toDelay(Rotation r) {
      switch (r) {
         case R1 : return Rotation.DELAY_R1;
         case R3 : return Rotation.DELAY_R3;
         case R4 : return Rotation.DELAY_R4;
         case ODD_BLOCK : return Rotation.DELAY_ODD;
         case EVEN_BLOCK : return Rotation.DELAY_EVEN;
         default : if (Agenda.statusU) Agenda.log("SOMETHING WENT WRONG WITH TO DELAY");
         return Rotation.DELAY_R1;
      }
   }
   
   public static Rotation toHalf(Rotation r) {
      switch (r) {
         case R1 : return Rotation.HALF_R1;
         case R3 : return Rotation.HALF_R3;
         case R4 : return Rotation.HALF_R4;
         default : ErrorID.showUserError(ErrorID.HALF_BLOCK_SELECTED); 
            return Rotation.HALF_R1;
      }
   }
   
   public static Rotation toNormal(Rotation r) {
      switch (r) {
         case HALF_R1 : case DELAY_R1 : return Rotation.R1;
         case HALF_R3 : case DELAY_R3 : return Rotation.R3;
         case HALF_R4 : case DELAY_R4 : return Rotation.R4;
         case DELAY_ODD : return Rotation.ODD_BLOCK;
         case DELAY_EVEN : return Rotation.EVEN_BLOCK;
         default : return r;
      }
   }
   
}
