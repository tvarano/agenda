//Thomas Varano
//Dec 7, 2017

package ioFunctions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;

import constants.Rotation;
import constants.RotationConstants;
import managers.Agenda;

public class WebReader
{
   public static URL rotationDataSite;
   private ArrayList<String> events, dates;

   public WebReader() {
      init();
   }
   
   public void init() {
      if (Agenda.statusU) Agenda.log("website reader initialized");
      try {
         rotationDataSite = new URL("https://agendapascack.weebly.com/");
      } catch (MalformedURLException e) {
         if (Agenda.statusU) Agenda.logError("URL not traced", e);
      }
      String total = "";
      long start = System.currentTimeMillis();
      try {
         total = retrieveHtml();
      } catch (IOException e) {
         e.printStackTrace();
         if (Agenda.statusU) Agenda.logError("Internet Connection Error", e);
      }
      long readTime = System.currentTimeMillis()-start;
      start = System.currentTimeMillis();
      events = extractEvents(total);
      long eventTime = System.currentTimeMillis()- start;
      start = System.currentTimeMillis();
      dates = extractDates(total);
      long dateTime = System.currentTimeMillis()- start;
      if (Agenda.statusU) Agenda.log("internet read in "+readTime + ". events ordered in "+eventTime + ", dates in "+dateTime);
   }

   private static String retrieveHtml() throws IOException {
      BufferedReader in;
      in = new BufferedReader(
            new InputStreamReader(rotationDataSite.openStream()));
      StringBuilder b = new StringBuilder();
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
         b.append(inputLine);
      }
      in.close();
      return b.toString();
   }

   public static ArrayList<String> extractEvents(String totalHtml) throws StringIndexOutOfBoundsException {
      return findTypes(0, "&#8203;", "<br />", "<br />", "</div>", totalHtml);
   }
   
   public static ArrayList<String> extractDates(String totalHtml) throws StringIndexOutOfBoundsException {
      return findTypes(0, "&#8203;&#8203;", "<br />", "<br />", "</div>", totalHtml);
   }
   
   private Integer[] getTodayDateStringIndexes() {
      if (dates == null)
         return null;
      LocalDate today = LocalDate.now();
      ArrayList<Integer> indexes = new ArrayList<Integer>();
      for (int i = 0; i < dates.size(); i++) {
         if (orderDateString(dates.get(i)).equals(today.toString()))
            indexes.add(i);
      }
      return indexes.toArray(new Integer[indexes.size()]);
   }
   
   public Rotation readTodayRotation() {
      if (events != null && dates != null) {
         Integer[] indexes = getTodayDateStringIndexes();
         if (indexes.length == 0)
            return Rotation.getRotation(LocalDate.now().getDayOfWeek());
         for (Integer i : indexes) {
            if (RotationConstants.getRotation(events.get(i)) != null) {
               if (Agenda.statusU) Agenda.log("ROTATION: "+events.get(i) + " read from internet");
               return RotationConstants.getRotation(events.get(i));
            }
            if (events.get(i).contains("No School"))
               return Rotation.NO_SCHOOL;
            if (events.get(i).contains("Half Day")) {
               if (Agenda.statusU) Agenda.log("ROTATION: half "+events.get(i) + " read from internet");
               return RotationConstants.toHalf(
                     RotationConstants.getRotation(events.get(i).substring(0, events.get(i).indexOf('(')-1)));
            }
            if (events.get(i).contains("Delayed Open")) {
               if (Agenda.statusU) Agenda.log("ROTATION: delayed "+events.get(i) + " read from internet");
               return RotationConstants.toDelay(
                     RotationConstants.getRotation(events.get(i).substring(0, events.get(i).indexOf('(')-1)));
            }
         }
      }
      if (Agenda.statusU) Agenda.log("ROTATION: read from day, not internet");
      return Rotation.getRotation(LocalDate.now().getDayOfWeek());
   }
  
   private static String orderDateString(String str) {
      try {
         String day = str.substring(0, str.indexOf("/"));
         day = (Integer.parseInt(day) < 10) ? "0" + day : day;
         String month = str.substring(str.indexOf("/")+1, str.indexOf("/", str.indexOf("/")+1));
         month = (Integer.parseInt(month) < 10) ? "0" + month : month;
         String year = str.substring(str.indexOf("/", str.indexOf("/")+1)+1);
         return year + "-" + day + "-" + month;
      } catch (StringIndexOutOfBoundsException e) {
         if (Agenda.statusU) Agenda.logError("unable to order dateString: "+str, e);
         return null;
      }
   }
   
  private static ArrayList<String> findTypes(int startIndex, String firstKey, String beginKey,
        String endKey, String breakKey, String totalHtml) throws StringIndexOutOfBoundsException {
     if (totalHtml.equals(""))
        return null;
     ArrayList<String> retval = new ArrayList<String>();
     int index = startIndex;
     int first = 0;
     do {
        String startKey = beginKey;
        boolean isFirst = index == startIndex;
         if (isFirst) 
            startKey = firstKey;
         index = totalHtml.indexOf(startKey, index);
         if (isFirst)
            first = index;
         int endIndex = (Math.min(totalHtml.indexOf(endKey, index + 1),
               totalHtml.indexOf(breakKey, index + 1)) == -1)
                     ? Math.max(totalHtml.indexOf(endKey, index + 1),
                           totalHtml.indexOf(breakKey, index + 1))
                     : Math.min(totalHtml.indexOf(endKey, index + 1),
                           totalHtml.indexOf(breakKey, index + 1));
         // TODO returning negative one, check for endIdnex
         String addition = (totalHtml.substring(index + startKey.length(),
               endIndex));
         index += startKey.length() + addition.length() - 1;
         retval.add(addition);
      } while (totalHtml.indexOf(breakKey, first) - index >= 3 && index != -1);
      return retval;
   }

   public ArrayList<String> getEvents() {
      return events;
   }

   public ArrayList<String> getDates() {
      return dates;
   }
   
   public static void main(String[] args) {
      long start = System.currentTimeMillis();
      WebReader wr = new WebReader();
      System.out.println(System.currentTimeMillis()-start);
      wr.readTodayRotation();
      System.out.println(System.currentTimeMillis()-start);
   }
}
