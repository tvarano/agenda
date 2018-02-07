package constants.test;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import information.Time;
import ioFunctions.OrderUtility;
import managers.Agenda;

public class DayTypeTest {
   private Time[] startTimes, endTimes;
   private Time labSwitch;
   private String name;
   private URL site;
   private DayTypeBackup backup;
   private static boolean error;
   private static ArrayList<DayTypeTest> types;

   public static void init() {
      types = new ArrayList<DayTypeTest>();
      for (DayTypeBackup b : DayTypeBackup.values()) {
         if (!error) { 
            try {
               types.add(new DayTypeTest(b.getSite()));
            } catch (Exception e) {
               types.add(new DayTypeTest(b));
            }
         } else
            types.add(new DayTypeTest(b));
      }
   }

   private DayTypeTest(DayTypeBackup b) {
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

   private DayTypeTest(String fileLocation) throws Exception {
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

   private void formatString(String unf)
         throws NullPointerException, EOFException {
      java.util.Scanner s = new java.util.Scanner(unf);
       name = s.nextLine();
      ArrayList<Time> starts = new ArrayList<Time>();
      ArrayList<Time> ends = new ArrayList<Time>();
      String line = "";
      if (!s.nextLine().equalsIgnoreCase("start")) {
         s.close();
         throw new NullPointerException(
               "format for " + name + " dayType incorrect");
      }
      while (!(line = s.nextLine()).equalsIgnoreCase("end"))
         starts.add(Time.fromString(line));
      while (!(line = s.nextLine()).equalsIgnoreCase("lab"))
         ends.add(Time.fromString(line));
      labSwitch = Time.fromString(s.nextLine());
      startTimes = starts.toArray(new Time[starts.size()]);
      endTimes = ends.toArray(new Time[ends.size()]);
      s.close();
   }
   
   private static final int MILLIS_TO_WAIT = 800;
   private static String retrieveHtml(URL site) throws ExecutionException, TimeoutException, InterruptedException {
      return OrderUtility.futureStringCall(MILLIS_TO_WAIT, new java.util.concurrent.Callable<String>() {
         @Override
         public String call() throws Exception {
            return readHtml0(site);
         }
      }, "retreieve dayType");
   }

   private static String readHtml0(URL site) throws IOException {
      BufferedReader in = null;
      in = new BufferedReader(new InputStreamReader(site.openStream()));
      StringBuilder b = new StringBuilder();
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
   public static void main(String[] args) {
      Agenda.statusU = true;
      init();
      System.out.println(types);
      System.out.println("end");
   }
}