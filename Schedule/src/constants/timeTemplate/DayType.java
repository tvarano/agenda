package constants.timeTemplate;

import java.io.EOFException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import information.Time;
import ioFunctions.OrderUtility;
import managers.Agenda;

public class DayType {
   private Time[] startTimes, endTimes;
   private Time labSwitch;
   private String name;
   private URL site;
   private DayTypeBackup backup;
   private static boolean error;
   private static ArrayList<DayType> types;

   public static void init() {
      types = new ArrayList<DayType>();
      for (DayTypeBackup b : DayTypeBackup.values()) {
         if (!error) { 
            try {
               types.add(new DayType(b.getSite()));
            } catch (Exception e) {
               if (e instanceof IOException || e instanceof ExecutionException)
                  error = true;
               e.printStackTrace();
               Agenda.log(b.name() + " INITIALIZED OFFLINE");
               types.add(new DayType(b));
            }
         } else
            types.add(new DayType(b));
      }
      Agenda.log("day types successfully initialized");
   }

   private DayType(DayTypeBackup b) {
      backup = b;
      setStartTimes(b.getStartTimes());
      setEndTimes(b.getEndTimes());
      name = b.name();
      setLabSwitch(b.getLabSwitch());
   }
   
   private void offlineInit0() {
      setStartTimes(backup.getStartTimes());
      setEndTimes(backup.getEndTimes());
      name = backup.name();
      setLabSwitch(backup.getLabSwitch());
   }
   
   private void onlineInit0() throws Exception {
      readAndFormat(site);
   }

   private DayType(String fileLocation) throws Exception {
      URL fileURL = new URL(fileLocation);
      site = fileURL;
      readAndFormat(fileURL);
      backup = null;
   }

   private void readAndFormat(URL in) throws Exception {
      formatString(retrieveHtml(in));
   }
   
   public void reinitialize() {
      try {
         onlineInit0();
      } catch (Exception e) {
         if (backup == null)
            offlineInit0();
         Agenda.logError("Error in reinitializing daytype", e);
      }
   }

   private static final String START = "start ", END = "end", LAB = "lab";
   private void formatString(String unf)
         throws NullPointerException, EOFException {
      java.util.Scanner s = new java.util.Scanner(unf);
      name = s.nextLine();
      ArrayList<Time> starts = new ArrayList<Time>();
      ArrayList<Time> ends = new ArrayList<Time>();
      String line = "";
      if (!s.nextLine().equalsIgnoreCase(START)) {
         s.close();
         throw new NullPointerException(
               "format for " + name + " dayType incorrect");
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
   
   private static final int MILLIS_TO_WAIT = 200;
   private static String retrieveHtml(URL site) throws ExecutionException, TimeoutException, InterruptedException {
      return OrderUtility.futureCall(MILLIS_TO_WAIT, new java.util.concurrent.Callable<String>() {
         @Override
         public String call() throws Exception {
            return readHtml0(site);
         }
      }, "retreieve dayType");
   }

   private static String readHtml0(URL site) throws IOException {
      java.io.BufferedReader in = null;
      in = new java.io.BufferedReader(new java.io.InputStreamReader(site.openStream()));
      java.lang.StringBuilder b = new java.lang.StringBuilder();
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
         b.append(inputLine);
         b.append("\n");
      }
      in.close();
      return b.toString();
   }

   public Time[] getStartTimes() {
      return startTimes;
   }
   public void setStartTimes(Time[] startTimes) {
      this.startTimes = startTimes;
   }
   public Time[] getEndTimes() {
      return endTimes;
   }
   public void setEndTimes(Time[] endTimes) {
      this.endTimes = endTimes;
   }
   public Time getLabSwitch() {
      return labSwitch;
   }
   public void setLabSwitch(Time labSwitch) {
      this.labSwitch = labSwitch;
   }
   public DayTypeBackup getBackup() {
      return backup;
   }
   public void setBackup(DayTypeBackup backup) {
      this.backup = backup;
   }
   public String toString() {
      return getClass().getName() + "[" + name + "]";
   }
   
   /**
    * The list of daytypes
    * @return an {@code ArrayList} of DayTypes that are used in the program.
    */
   public static final ArrayList<DayType> types() {
      return types;
   }
   
   public static final DayType get(DayTypeBackup backup) {
      return types.get(backup.ordinal()-1);
   }
   /**
    * Convenience method to return the normal DayType
    * @return the DayType corresponding with {@linkplain DayTypeBackup} normal
    */
   public static final DayType normal() {
      return get(DayTypeBackup.NORMAL);
   }
   /**
    * Convenience method to return the block DayType
    * @return the DayType corresponding with {@linkplain DayTypeBackup} block
    */
   public static final DayType block() {
      return get(DayTypeBackup.BLOCK);
   }
   /**
    * Convenience method to return the delayed opening DayType
    * @return the DayType corresponding with {@linkplain DayTypeBackup} delayed open
    */
   public static final DayType delayedOpen() {
      return get(DayTypeBackup.DELAYED_OPEN);
   }
   /**
    * Convenience method to return the delayed odd block DayType
    * @return the DayType corresponding with {@linkplain DayTypeBackup} delay odd
    */
   public static final DayType delayOdd() {
      return get(DayTypeBackup.DELAY_ODD);
   }
   /**
    * Convenience method to return the delayed even block DayType
    * @return the DayType corresponding with {@linkplain DayTypeBackup} delay even
    */
   public static final DayType delayEven() {
      return get(DayTypeBackup.DELAY_EVEN);
   }
   /**
    * Convenience method to return the half day DayType
    * @return the DayType corresponding with {@linkplain DayTypeBackup} half day
    */
   public static final DayType halfDay() {
      return get(DayTypeBackup.HALF_DAY);
   }
   /**
    * Convenience method to return the test day DayType
    * @return the DayType corresponding with {@linkplain DayTypeBackup} test day
    */
   public static final DayType testDay() {
      return get(DayTypeBackup.TEST_DAY);
   }
   /**
    * Convenience method to return the no school DayType
    * @return the DayType corresponding with {@linkplain DayTypeBackup} no school
    */
   public static final DayType noSchool() {
      return get(DayTypeBackup.NO_SCHOOL);
   }
   /**
    * Convenience method to return the 10:00 opening DayType
    * @return the DayType corresponding with {@linkplain DayTypeBackup} delay arr
    */
   public static final DayType delayArrival() {
      return get(DayTypeBackup.DELAY_ARR);
   }
   
   public static void main(String[] args) {
      Agenda.statusU = true;
      init();
      System.out.println(types);
      System.out.println("end");
   }
}