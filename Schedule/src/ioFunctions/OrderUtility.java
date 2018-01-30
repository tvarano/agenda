package ioFunctions;
import constants.Rotation;
import constants.RotationConstants;
import information.ClassPeriod;
import information.Schedule;
import managers.Agenda;

//Thomas Varano
//[Program Descripion]
//Oct 20, 2017

public final class OrderUtility
{
   private static boolean debug = false, detailedDebug = false;
   
   public static Schedule reorderClasses(Rotation r, Schedule s, ClassPeriod[] template) {
      Agenda.log("ordering schedule: "+s.getName() + " to "+r);
      if (debug) System.out.println("ordering schedule: "+s.getName() + " to "+r);
      ClassPeriod[] newArray = reorderClasses(r, s.getClasses());
      s.setClasses(newArray);
      if (debug) System.out.println(Rotation.NO_SCHOOL.getTimes()[0]);
      s = trim(s);
      s.calculateSchoolDay();
      s.setPascackData();
      return s;
   }
   
   public static Schedule reorderAndClone(Rotation r, Schedule s, ClassPeriod[] template) {
      return reorderClasses(r, s.clone(), template);
   }
   
   public static ClassPeriod[] reorderClasses(Rotation r, ClassPeriod[] unOrderedArray) {
      if (r.equals(Rotation.NO_SCHOOL))
         return Rotation.NO_SCHOOL.getTimes();
      if (debug) System.out.println("**********\nordering class array...");
      if (detailedDebug) printData(r, unOrderedArray);
      int extraClasses = unOrderedArray.length - Rotation.R1.getTimes().length;
      int totalAmt  = r.getTimes().length + extraClasses;
      for (ClassPeriod c : unOrderedArray) {
         if (c.getSlot() == 0 && r.isDelay())
            totalAmt--;
         else if (c.getSlot() == 8 && r.isHalf())
            totalAmt--;
      }
      ClassPeriod[] newArray = new ClassPeriod[totalAmt];
      int[] order = Rotation.getSlotRotation(r);
      
      //check for zero period, etc.
      int arrayStart = 0; 
      int newArrayIndex = 0, rotationIndex = 0;
      for (int i = 0; i < unOrderedArray.length; i++) {
         if (unOrderedArray[i].getSlot() == 0) {
            if (RotationConstants.isZeroFriendly(r)) {
               unOrderedArray[i].setTimeTemplate(RotationConstants.getPeriodZero());
               newArray[0] = unOrderedArray[i];
               newArrayIndex++;
            }
            arrayStart++;
         }
         else if (unOrderedArray[i].getSlot() == 8) {
            if (RotationConstants.isEightFriendly(r)) {
               newArray[newArray.length-1] = unOrderedArray[i];
               unOrderedArray[i].setTimeTemplate(RotationConstants.getPeriodEight());
            }
         }
      }
      
      //every other class
      for (int i = 0; i < order.length; i++) {
         if (detailedDebug) System.out.println("order run "+i);
         for (int o = arrayStart; o < unOrderedArray.length; o++) {
            if (order[i] == unOrderedArray[o].getSlot()) {
               newArray[newArrayIndex] = new ClassPeriod(
                     unOrderedArray[o].getSlot(), unOrderedArray[o].getName(),
                     r.getTimes()[rotationIndex].getStartTime(),
                     r.getTimes()[rotationIndex].getEndTime(),
                     unOrderedArray[o].getTeacher(),
                     unOrderedArray[o].getRoomNumber());
               newArray[newArrayIndex].setData(unOrderedArray[o]);
               if (detailedDebug) {
                  System.out.println("new array["+newArrayIndex+"] set to old["+o);
                  System.out.println("\tindex is; "+unOrderedArray[o].getSlot());
               }
               newArrayIndex++;
               rotationIndex++;
            }
         }
         // take special periods 
         if (order[i] == RotationConstants.PASCACK) {
            if (detailedDebug) System.out.println("entering pascack...");
            newArray[newArrayIndex] = RotationConstants.getPascack();
            newArrayIndex++;
            rotationIndex++;
         
         } else if (order[i] == RotationConstants.PASCACK_STUDY_1) {
            newArray[newArrayIndex] = RotationConstants.getPascackStudyOne();
            newArray[newArrayIndex].setTimeTemplate(r.get(RotationConstants.pascack_1_name));
            newArrayIndex++;
            rotationIndex++;
         } else if (order[i] == RotationConstants.PASCACK_STUDY_2) {
            newArray[newArrayIndex] = RotationConstants.getPascackStudyTwo();
            newArray[newArrayIndex].setTimeTemplate(r.get(RotationConstants.pascack_2_name));
            newArrayIndex++;
            rotationIndex++;
          
         }
      }
      if (detailedDebug) {
         System.out.println("finished ordering... PRODUCT");
         printData(r, newArray);
      }
      return newArray;
   }
   
   public static ClassPeriod[] trim(ClassPeriod[] in) {
      ClassPeriod[] ret;
      int amtClasses = 0;
      for (ClassPeriod p : in)
         if (p != null)
            amtClasses++;
      ret = new ClassPeriod[amtClasses];
      for (int i = 0; i < ret.length; i++) 
         ret[i] = in[i];
      if (detailedDebug) System.out.println("TRIMMING " + in.length + " TO "+ret.length);
      return ret;
   }
   
   public static Schedule trim(Schedule s) {
      s.setClasses(trim(s.getClasses()));
      return s;
   }
   
   private static void printData(Rotation r, ClassPeriod[] oldArray) {
         System.out.println("--------");
         for (ClassPeriod c : oldArray)
            System.out.println(c);
         System.out.println("to "+r+"\n--------");
   }
}
