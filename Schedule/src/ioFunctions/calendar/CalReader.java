//Thomas Varano
//[Program Descripion]
//Jan 26, 2018

package ioFunctions.calendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import constants.Rotation;
import constants.RotationConstants;
import managers.Agenda;

public class CalReader {
   private static URL rotationDataSite;
   private static boolean urlClear, calClear;
   private VCalendar cal;
   private boolean debug;

   public CalReader() {
      init();
   }
   
   public void init() {
      debug = true;
      try {
         rotationDataSite = new URL("https://calendar.google.com/calendar/ical/8368c5a91jog3s32oc6k22f4e8%40group.calendar.google.com/public/basic.ics");
         urlClear = false;
      } catch (MalformedURLException e) {
         urlClear = false;
         Agenda.logError("error with .ics url", e);
      }
      if (urlClear) {
         try {
            cal = readAndExtractEvents();
            calClear = true;
         } catch (Exception e) {
            calClear = false;
            Agenda.logError("exception in cal reading", e);
         }
      }
   }
   
   /*
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
   */
   
   public VCalendar readAndExtractEvents() throws ExecutionException, TimeoutException, InterruptedException {
      return extractEvents(retrieveRfc());
   }
   
   //commands 
   public static final String BEGIN = "BEGIN:VEVENT";
   
   public static final String END = "END:VEVENT";
   
   public static final String DTSTAMP_PREFIX = "DTSTAMP:";
   
   public static final String SUMMARY_PREFIX = "SUMMARY:";
   
   public VCalendar extractEvents(String rfc) {
      Scanner s = new Scanner(rfc);
      ArrayList<VEvent> events = new ArrayList<VEvent>();
      while (s.hasNextLine()) {
         String line = s.nextLine();
         if (line.equals(BEGIN)) {
            events.add(new VEvent());
            continue;
         } else if (line.contains(DTSTAMP_PREFIX)) {
            events.get(events.size() - 1).setStart(VEvent.translateDate(line.substring(DTSTAMP_PREFIX.length()-1)));
            events.get(events.size() - 1).setEnd(events.get(events.size() - 1).getStart().plusDays(1));
            continue;
         } else if (line.contains(SUMMARY_PREFIX)) {
            events.get(events.size() - 1).setSummary(line.substring(SUMMARY_PREFIX.length()-1));
         }
      }
      s.close();
      return VCalendar.build(events);
   }
   
   private static final long MILLIS_TO_WAIT = 4000L;
   public String retrieveRfc() throws ExecutionException, TimeoutException, InterruptedException {
      final ExecutorService executor = Executors.newSingleThreadExecutor();

      // schedule the work
      final Future<String> future = executor
            .submit(this::readRfc);
      try {
         // wait for task to complete
         final String result = future.get(MILLIS_TO_WAIT,
               TimeUnit.MILLISECONDS);
         return result;
      }

      catch (TimeoutException e) {
         Agenda.logError("ics reading timed out", e);
         future.cancel(true);
         throw e;
      }

      catch (InterruptedException e) {
         Agenda.logError("ics reading interrupted", e);
         throw e;
      }
      catch (ExecutionException e) {
         Agenda.logError("ics reading execution error", e);
         throw e;
      }
   }

   private String readRfc() throws IOException {
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
   
   public static void main(String[] args) {
      CalReader c = new CalReader();
      try {
         System.out.println(c.readAndExtractEvents().eventsString());
      } catch (ExecutionException | TimeoutException | InterruptedException e) {
         e.printStackTrace();
      }
   }
}
