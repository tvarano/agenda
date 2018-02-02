package constants.test;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import information.Addresses;
import information.Time;

public class DayTypeTest {
   private Time[] startTimes, endTimes;
   private Time labSwitch;
   private String name;
   private static ArrayList<DayTypeTest> types;
   
   public static void init() {
      types = new ArrayList<DayTypeTest>();
      for (int i = 0; i < DayTypeBackup.values().length; i++) {
         try {
            types.add(new DayTypeBackup(Addresses.dayTypeSites()[i]))
         }
      }
   }
   
   public DayTypeTest(DayTypeBackup b) {
      setStartTimes(b.getStartTimes());
      setEndTimes(b.getEndTimes());
      setLabSwitch(b.getLabSwitch());
   }
   
   public DayTypeTest(String fileLocation) throws NullPointerException, EOFException, IOException {
      URL fileURL = new URL(fileLocation);
      readAndFormat(fileURL);
   }
   
   private void readAndFormat(URL in) throws NullPointerException, EOFException, IOException {
      formatString(readHtml(in));
   }
   
   private void formatString(String unf) throws NullPointerException, EOFException {
      java.util.Scanner s = new java.util.Scanner(unf);
      name = s.nextLine();
      ArrayList<Time> starts = new ArrayList<Time>();
      ArrayList<Time> ends = new ArrayList<Time>();
      String line = "";
      if (!s.nextLine().equalsIgnoreCase("start"))
         throw new NullPointerException("format for "+name+ " dayType incorrect");
      while (!(line = s.nextLine()).equalsIgnoreCase("end"))
         starts.add(Time.fromString(line));
      while (!(line = s.nextLine()).equalsIgnoreCase("lab"))
         ends.add(Time.fromString(line));
      labSwitch = Time.fromString(s.nextLine());
      startTimes = starts.toArray(new Time[starts.size()]);
      endTimes = ends.toArray(new Time[ends.size()]);
   }
   
   private static String readHtml(URL site) throws IOException {
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
}