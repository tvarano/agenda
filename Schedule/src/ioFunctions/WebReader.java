//Thomas Varano
//[Program Descripion]
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
      if (Agenda.statusU) Agenda.log("website reading process begun");
      try {
         rotationDataSite = new URL("https://sites.google.com/pascack.k12.nj.us/agenda/home");
      } catch (MalformedURLException e) {
         if (Agenda.statusU) Agenda.logError("URL not traced", e);
      }
      String total = "";
      try {
         total = retrieveHtml();
      } catch (IOException e) {
         if (Agenda.statusU) Agenda.logError("Internet Connection Error", e);
      }
      events = extractEvents(total);
      dates = extractDates(total);
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
      return findTypes(0, "ltr;\">", "</p>", "</div", totalHtml);
   }
   
   public static ArrayList<String> extractDates(String totalHtml) throws StringIndexOutOfBoundsException {
      return findTypes(0, "right;\">", "</p>", "</div", totalHtml);
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
            if (RotationConstants.getRotation(events.get(i)) != null)
               return RotationConstants.getRotation(events.get(i));
            if (events.get(i).contains("Half Day"))
               return RotationConstants.toHalf(
                     RotationConstants.getRotation(events.get(i).substring(0, events.get(i).indexOf('(')-1)));
         }
      }
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
   
  private static ArrayList<String> findTypes(int startIndex, String beginKey,
        String endKey, String breakKey, String totalHtml) throws StringIndexOutOfBoundsException {
     if (totalHtml.equals(""))
        return null;
     ArrayList<String> retval = new ArrayList<String>();
     int dIndex = startIndex;
     int first = 0;
     do {
        boolean isFirst = dIndex == startIndex;
         dIndex = totalHtml.indexOf(beginKey, dIndex);
         if (isFirst)
            first = dIndex;
         String addition = (totalHtml.substring(dIndex + beginKey.length(),
               totalHtml.indexOf(endKey, dIndex)));
         dIndex += beginKey.length() + addition.length() + 10;
         retval.add(addition);
      } while (dIndex < totalHtml.indexOf(breakKey, first) && dIndex != -1);
      return retval;
   }

   public ArrayList<String> getEvents() {
      return events;
   }

   public ArrayList<String> getDates() {
      return dates;
   }

   
   public static void main(String[] args) {
      WebReader w = new WebReader();
      System.out.println(w.readTodayRotation());
   }
}
