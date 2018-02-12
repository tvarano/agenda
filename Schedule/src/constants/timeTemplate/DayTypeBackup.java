package constants.timeTemplate;
import information.Time;

//Thomas Varano
//Sep 4, 2017

public enum DayTypeBackup
{
   /**
    * A normal day rotation
    */
   NORMAL (new Time[]{new Time(8,00), new Time(8,52), new Time(9,46), new Time(10,38), new Time(11,30), new Time(12,21),
               new Time(13,13), new Time(14,05)}, 
         new Time[] {new Time(8,48), new Time(9,42), new Time(10,34), new Time(11,26), new Time(12,17), new Time(13,9),
               new Time(14,1), new Time(14,53)}, 
         new Time(11,53)
   ),
   /**
    * A block day
    */
   BLOCK (new Time[]{new Time(8,00), new Time(9,31), new Time(11,04), new Time(11,55), new Time(13,26)}, 
         new Time[]{new Time(9,27), new Time(11,00), new Time(11,51), new Time(13,22), new Time(14,53)},
         new Time(11,27)
   ),
   /**
    * A half day
    */
   HALF_DAY(new Time[] {
         new Time(8,00), new Time(8,35), new Time(9,12), new Time(9,47), new Time(10,22), new Time(10,56), new Time(11,30)},
         new Time[] {
            new Time(8,31), new Time(9,8), new Time(9,43), new Time(10,18), new Time(10,52), new Time(11,26), new Time(12,00)}
   ), 
   /**
    * A delayed opening even block
    */
   DELAYED_OPEN(
         new Time[]{new Time(9, 30), new Time(10, 9), new Time(10, 50), new Time(11, 29), new Time(12, 8), new Time(12, 59),
               new Time(13, 38), new Time(14, 17)},
         new Time[]{new Time(10, 5), new Time(10, 46), new Time(11, 25),
               new Time(12, 4), new Time(12, 55), new Time(13, 34),
               new Time(14, 13), new Time(14, 53)}
   ),
   /**
    * A delayed opening odd block
    */
   DELAY_ODD(new Time[] {new Time(9,30), new Time(10,39), new Time(11,48), new Time(12,39), new Time(13,48)},
         new Time[] {new Time(10,35), new Time(11,44), new Time(12,35), new Time(13,44), new Time(14,53)}
   ),
   /**
    * A delayed opening even block
    */
   DELAY_EVEN(new Time[] {new Time(9,30), new Time(11,2), new Time(12,34), new Time(13,25)},
         new Time[] {new Time(10,58), new Time(12,30), new Time(13,21), new Time(14,53)}
   ),
   /**
    * no school
    */
   NO_SCHOOL(new Time[] {new Time(0,0)}, new Time[] {new Time(23,59)}),
   /**
    * Testing schedule (midterms, finals)
    */
   TEST_DAY(new Time[] {new Time(8,0), new Time(9,29), new Time(11,01), new Time(11,57), new Time(13,26)}, 
         new Time[] {new Time(9,25), new Time(10,57), new Time(11,53), new Time(13,22), new Time(2,51)}),
   /**
    * 10:00 opening delayed arrival
    */
   DELAY_ARR(new Time[] {new Time(10,00), new Time(11,22), new Time(12,13), new Time(13,35)}, 
         new Time[] {new Time(11,18), new Time(12,9), new Time(13,31), new Time(14,53)}),
   ;
   
   private final Time[] startTimes, endTimes;
   private final Time labSwitch;
   private final String site;
   
   private DayTypeBackup(Time[] startTimes, Time[] endTimes, Time labSwitch) {
      this.startTimes = startTimes; this.endTimes = endTimes; this.labSwitch = labSwitch;
      site = resources.Addresses.DAY_TYPE_HOME + name().toLowerCase() + ".txt";
   }
   
   private DayTypeBackup(Time[] startTimes, Time[] endTimes) {
      this(startTimes, endTimes, null);
   }
   
   public boolean hasLab() {
      return labSwitch != null;
   }

   public boolean equals(DayTypeBackup otherDT) {
      for (int i = 0; i < startTimes.length; i++) {
         if(!startTimes[i].equals(otherDT.getStartTimes()[i]) || !endTimes[i].equals(otherDT.getEndTimes()[i]))
            return false;
      }
      return true;
   }
   
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
   public String getSite() {
      return site;
   }
}
