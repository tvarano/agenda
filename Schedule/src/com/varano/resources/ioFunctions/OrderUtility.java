package com.varano.resources.ioFunctions;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.varano.information.ClassPeriod;
import com.varano.information.Schedule;
import com.varano.information.constants.Rotation;
import com.varano.information.constants.RotationConstants;
import com.varano.managers.Agenda;

//Thomas Varano
//[Program Descripion]
//Oct 20, 2017

public final class OrderUtility
{
   private final static boolean debug = false, detailedDebug = false;
   
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
      int[] order = r.slotRotation();
      
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
            newArray[newArrayIndex].setTimeTemplate(r.get(RotationConstants.PASCACK_STUDY_1));
            newArrayIndex++;
            rotationIndex++;
         } else if (order[i] == RotationConstants.PASCACK_STUDY_2) {
            newArray[newArrayIndex] = RotationConstants.getPascackStudyTwo();
            newArray[newArrayIndex].setTimeTemplate(r.get(RotationConstants.PASCACK_STUDY_2));
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
   /**
    * Calls for a future method with timeout capabilities.
    * NOTE: Agenda.logError just prints an error. Change it to whatever you want to print the error.
    * @param millisToWait How long to wait for the method
    * @param method the method to call. The easiest way to make this is an anonymous class. You can change the
    *    parameter in the <> to change the return value
    * @param description the description of the method in logs
    * @return the return value of the <code>method</code> parameter
    * @throws ExecutionException if the method itself is executed incorrectly
    * @throws TimeoutException if the method is timed out, or takes longer than the given millis to wait
    * @throws InterruptedException if the method is interrupted
    */
   public static <T> T futureCall(long millisToWait, java.util.concurrent.Callable<T> method, String description) 
         throws ExecutionException, TimeoutException, InterruptedException, Exception {
      final ExecutorService executor = Executors.newSingleThreadExecutor();
      long start = System.currentTimeMillis();
      // schedule the work

      final Future<T> future = executor
            .submit(method);
      try {
         // wait for task to complete
         final T result = future.get(millisToWait,
               TimeUnit.MILLISECONDS);
         Agenda.log(description + " took " + (System.currentTimeMillis() - start));
         return result;
      } catch (TimeoutException e) {
         Agenda.logError(description + " timed out", e);
         future.cancel(true);
         throw e;
      }
   }
}
