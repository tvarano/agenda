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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import constants.Rotation;
import constants.RotationConstants;
import managers.Agenda;

/**
 * Has been replaced by CalReader
 * @see ioFunctions.calendar.CalReader
 * @author varanoth
 */
@Deprecated(since="1.7.1")
public class WebReader
{
   public URL rotationDataSite;
   private ArrayList<String> events, dates;

   public WebReader() {
      init();
   }

   public void init() {
      Agenda.log("website reader initialized");
      try {
         rotationDataSite = new URL("https://agendapascack.weebly.com/");
      } catch (MalformedURLException e) {
         Agenda.logError("URL not traced", e);
      }
      String total = "";
      long start = System.currentTimeMillis();
      try {
         total = retrieveHtml();
      } catch (Exception e) {
         Agenda.log("Internet Connection Error");
      }
      long readTime = System.currentTimeMillis() - start;
      start = System.currentTimeMillis();
      events = extractEvents(total);
      long eventTime = System.currentTimeMillis() - start;
      start = System.currentTimeMillis();
      dates = extractDates(total);
      long dateTime = System.currentTimeMillis() - start;
      Agenda.log("internet read in " + readTime + ". events ordered in "
               + eventTime + ", dates in " + dateTime);
   }

   private static final long MILLIS_TO_WAIT = 4000L;
   public String retrieveHtml() throws ExecutionException, TimeoutException, InterruptedException {
      return OrderUtility.futureCall(MILLIS_TO_WAIT, this::readHtml, "internet reading");
   }
   
   /*
    // dummy method to test timeout
   private static String requestDataFromWebsite() {
      try {
         Thread.sleep(14_000L);
      } catch (InterruptedException e) {}
      return "dummy";
   }
   */
   
   private String readHtml() throws IOException {
      BufferedReader in = null;
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
            String e = events.get(i);
            if (RotationConstants.getRotation(e) != null) {
               Agenda.log("ROTATION: "+e + " read from internet");
               return RotationConstants.getRotation(events.get(i));
            }
            if (e.contains("No School"))
               return Rotation.NO_SCHOOL;
            if (e.contains("Half Day")) {
               Agenda.log("ROTATION: half " + e + " read from internet");
               return RotationConstants.toHalf(
                     RotationConstants.getRotation(e.substring(0, e.indexOf('(')-1)));
            }
            if (e.contains("Delayed Open")) {
               Agenda.log("ROTATION: delayed "+e + " read from internet");
               return RotationConstants.toDelay(
                     RotationConstants.getRotation(e.substring(0, e.indexOf('(')-1)));
            }
         }
      }
      Agenda.log("ROTATION: read from day, not internet");
      return Rotation.getRotation(LocalDate.now().getDayOfWeek());
   }
  
   private static String orderDateString(String str) {
      try {
         String day = str.substring(0, str.indexOf("/"));
         day = (Integer.parseInt(day) < 10) ? "0" + day : day;
         String month = str.substring(str.indexOf("/") + 1,
               str.indexOf("/", str.indexOf("/") + 1));
         month = (Integer.parseInt(month) < 10) ? "0" + month : month;
         String year = str
               .substring(str.indexOf("/", str.indexOf("/") + 1) + 1);
         return year + "-" + day + "-" + month;
      } catch (StringIndexOutOfBoundsException e) {
         Agenda.logError("unable to order dateString: " + str, e);
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
      WebReader wr = new WebReader();
      System.out.println(wr.readTodayRotation());
   }
}
