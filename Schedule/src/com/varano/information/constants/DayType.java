package com.varano.information.constants;
import java.net.URL;
import java.util.ArrayList;

import com.varano.information.Time;
import com.varano.managers.Agenda;
import com.varano.resources.Addresses;
import com.varano.resources.ioFunctions.OrderUtility;

//Thomas Varano
//[Program Descripion]
//Sep 4, 2017

public enum DayType
{
   NORMAL (new Time[]{new Time(8,00), new Time(8,52), new Time(9,46), new Time(10,38), new Time(11,30), new Time(12,21),
               new Time(13,13), new Time(14,05)}, 
         new Time[] {new Time(8,48), new Time(9,42), new Time(10,34), new Time(11,26), new Time(12,17), new Time(13,9),
               new Time(14,1), new Time(14,53)
               }, 
         new Time(11,53)
   ),
   BLOCK (new Time[]{new Time(8,00), new Time(9,31), new Time(11,04), new Time(11,55), new Time(13,26)
            }, 
         new Time[]{new Time(9,27), new Time(11,00), new Time(11,51), new Time(13,22), new Time(14,53)
            },
         new Time(11,27)
   ),
   HALF_DAY(new Time[] {
         new Time(8,00), new Time(8,35), new Time(9,12), new Time(9,47), new Time(10,22), new Time(10,56), new Time(11,30)},
         new Time[] {
            new Time(8,31), new Time(9,8), new Time(9,43), new Time(10,18), new Time(10,52), new Time(11,26), new Time(12,00)}
   ), 
   DELAYED_OPEN(
         new Time[]{new Time(9, 30), new Time(10, 9), new Time(10, 50), new Time(11, 29), new Time(12, 8), new Time(12, 59),
               new Time(13, 38), new Time(14, 17)},
         new Time[]{new Time(10, 5), new Time(10, 46), new Time(11, 25),
               new Time(12, 4), new Time(12, 55), new Time(13, 34),
               new Time(14, 13), new Time(14, 53)}
   ),
   DELAY_ODD(new Time[] {new Time(9,30), new Time(10,39), new Time(11,48), new Time(12,39), new Time(13,48)},
         new Time[] {new Time(10,35), new Time(11,44), new Time(12,35), new Time(13,44), new Time(14,53)}
   ),
   DELAY_EVEN(new Time[] {new Time(9,30), new Time(11,2), new Time(12,34), new Time(13,25)},
         new Time[] {new Time(10,58), new Time(12,30), new Time(13,21), new Time(14,53)}
   ),
   NO_SCHOOL(new Time[] {new Time(0,0)}, new Time[] {new Time(23,59)}),
   TEST_DAY(new Time[] {new Time(8,0), new Time(9,29), new Time(11,01), new Time(11,57), new Time(13,26)}, 
         new Time[] {new Time(9,25), new Time(10,57), new Time(11,53), new Time(13,22), new Time(14,53)}),
   DELAY_ARR(new Time[] {new Time(10,00), new Time(11,22), new Time(12,13), new Time(13,35)}, 
         new Time[] {new Time(11,18), new Time(12,9), new Time(13,31), new Time(14,53)}),
   SPECIAL(new Time[] {new Time(0,0)}, new Time[] {new Time(0,1)});
   
   private Time[] startTimes, endTimes;
   private Time labSwitch;
   
   private DayType(Time[] startTimes, Time[] endTimes, Time labSwitch) {
      try {
         if (ordinal() != 6)
            onlineInit();
         else
            offlineInit(startTimes, endTimes, labSwitch);
      } catch (Exception e) {
         offlineInit(startTimes, endTimes, labSwitch);
      }
   }
   
   private DayType(Time[] startTimes, Time[] endTimes) {
      this(startTimes, endTimes, null);
   }
   
   private void offlineInit(Time[] startTimes, Time[] endTimes, Time labSwitch) {
      Agenda.log(name() + " initialized offline");
      this.startTimes = startTimes; this.endTimes = endTimes; this.labSwitch = labSwitch;
   }
   
   public boolean hasLab() {
      return labSwitch != null;
   }
   //------------------------------- online initialization --------------------------------
   
   public static void reread() {
      for (DayType d : values()) {
         try {
            if (d.ordinal() != 6)
               d.onlineInit();
         } catch (Exception e) {}
      }
   }
   
   private void onlineInit() throws Exception {
      Agenda.log("start "+name() + " at " +getSite());
      formatString(retrieveHtml(getSite()));
   }
   
   private static final String START = "start", END = "end", LAB = "lab";
   private void formatString(String unf)
         throws Exception {
      java.util.Scanner s = new java.util.Scanner(unf);
      s.nextLine();
      ArrayList<Time> starts = new ArrayList<Time>();
      ArrayList<Time> ends = new ArrayList<Time>();
      String line = "";
      if (!s.nextLine().equalsIgnoreCase(START)) {
         s.close();
         throw new java.util.zip.DataFormatException(
               "format for " + name() + " dayType incorrect");
      }
      while (!(line = s.nextLine()).equalsIgnoreCase(END))
         starts.add(Time.fromString(line));
      while (!(line = s.nextLine()).equalsIgnoreCase(LAB))
         ends.add(Time.fromString(line));
      labSwitch = Time.fromString(s.nextLine());
      startTimes = starts.toArray(new Time[starts.size()]);
      endTimes = ends.toArray(new Time[ends.size()]);
      s.close();
   }
   
   private int millisToWait() {
      return (ordinal() == 9) ? 700 : MILLIS_TO_WAIT;
   }
   
   private static final int MILLIS_TO_WAIT = 250;
   private String retrieveHtml(URL site) throws Exception {
      return OrderUtility.futureCall(millisToWait(), new java.util.concurrent.Callable<String>() {
         @Override
         public String call() throws Exception {
            return com.varano.resources.ResourceAccess.readHtml(site);
         }
      }, "retreieve dayType");
   }
   
   public URL getSite() {
      return Addresses.createURL(Addresses.DAY_TYPE_HOME + name().toLowerCase() + ".txt");
   }
   
   //---------------------------------------------------------------------------------------------

//   public boolean equals(DayType otherDT) {
//      for (int i = 0; i < startTimes.length; i++) {
//         if(!startTimes[i].equals(otherDT.getStartTimes()[i]) || !endTimes[i].equals(otherDT.getEndTimes()[i]))
//            return false;
//      }
//      return true;
//   }
   
   public Time getDayDuration() {
      return Time.calculateDuration(startTimes[0], endTimes[endTimes.length-1]);
   }
   
   public Time[] getStartTimes() {
      return startTimes;
   }
   public Time[] getEndTimes() {
      return endTimes;
   }
   public Time getLabSwitch() {
      return labSwitch;
   }
}
