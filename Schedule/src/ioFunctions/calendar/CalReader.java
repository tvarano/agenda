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
      debug = false;
      long start = System.currentTimeMillis();
      try {
         rotationDataSite = new URL(information.Addresses.ICS_URL);
         urlClear = true;
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
      } else
         calClear = false;
      if (debug) System.out.println("calendar import and format took " + (System.currentTimeMillis() - start));
   }

   public Rotation readTodayRotation() {
      if (calClear) {
         for (VEvent e : cal.eventsToday()) {
            String s = e.getSummary();
            if (RotationConstants.getRotation(s) != null) {
               Agenda.log("ROTATION: " + s + " read from internet");
               return RotationConstants.getRotation(s);
            }
            if (s.contains("No School"))
               return Rotation.NO_SCHOOL;
            if (s.contains("Half Day")) {
               Agenda.log("ROTATION: half " + s + " read from internet");
               return RotationConstants.toHalf(RotationConstants
                     .getRotation(s.substring(0, s.indexOf('(') - 1)));
            }
            if (s.contains("Delayed Open")) {
               Agenda.log("ROTATION: delayed " + s + " read from internet");
               return RotationConstants.toDelay(RotationConstants
                     .getRotation(s.substring(0, s.indexOf('(') - 1)));
            }
         }
      }
      return Rotation.getRotation(LocalDate.now().getDayOfWeek());
   }
   
   public VCalendar readAndExtractEvents() throws ExecutionException, TimeoutException, InterruptedException {
      return extractEvents(retrieveRfc());
   }
   
   //commands 
   public static final String BEGIN = "BEGIN:VEVENT";
   
   public static final String END = "END:VEVENT";
   
   public static final String DTSTAMP_PREFIX = "DTSTAMP:";
   public static final String DTSTART_PREFIX = "DTSTART;VALUE=DATE:";
   public static final String DTEND_PREFIX = "DTEND;VALUE=DATE:";
   
   public static final String SUMMARY_PREFIX = "SUMMARY:";
   
   public VCalendar extractEvents(String rfc) {
      long start = System.currentTimeMillis();
      if (debug) System.out.println("begin extract");
      Scanner s = new Scanner(rfc);
      ArrayList<VEvent> events = new ArrayList<VEvent>();
      while (s.hasNextLine()) {
         String line = s.nextLine();
         if (line.equals(BEGIN)) {
            if (debug) System.out.println("begin event");
            events.add(new VEvent());
            continue;
         } else if (line.contains(DTSTART_PREFIX)) {
            if (VEvent.getYear(line.substring(DTSTART_PREFIX.length())) != LocalDate.now().getYear())
               events.remove(events.size() - 1);
            if (debug) System.out.println("dtStart");            
            events.get(events.size() - 1).setStart(VEvent.translateDate(line.substring(DTSTART_PREFIX.length())));
            continue;
         } else if (line.contains(DTEND_PREFIX)) {
            if (debug) System.out.println("dtEnd");            
            events.get(events.size() - 1).setEnd(VEvent.translateDate(line.substring(DTEND_PREFIX.length())));
            continue;
         } else if (line.contains(SUMMARY_PREFIX)) {
            if (debug) System.out.println("summary");            
            events.get(events.size() - 1).setSummary(line.substring(SUMMARY_PREFIX.length()));
         }
      }
      s.close();
      Agenda.log("calendar format took "+(System.currentTimeMillis() - start));
      return VCalendar.build(events);
   }
   
   private static final long MILLIS_TO_WAIT = 8000L;
//   private static final long MILLIS_TO_WAIT = Long.MAX_VALUE;
   public String retrieveRfc() throws ExecutionException, TimeoutException, InterruptedException {
      final ExecutorService executor = Executors.newSingleThreadExecutor();
      long start = System.currentTimeMillis();
      // schedule the work
      final Future<String> future = executor
            .submit(this::readRfc);
      try {
         // wait for task to complete
         final String result = future.get(MILLIS_TO_WAIT,
               TimeUnit.MILLISECONDS);
         Agenda.log("cal read took " + (System.currentTimeMillis() - start));
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
      if (debug) System.out.println("begun reading");
      in = new BufferedReader(
            new InputStreamReader(rotationDataSite.openStream()));
      StringBuilder b = new StringBuilder();
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
         b.append(inputLine);
         b.append("\n");
      }
      if (debug) System.out.println("done reading");
      in.close();
      return b.toString();
   }
   
   public static void main(String[] args) {
      System.out.println("hello");
      CalReader c = new CalReader();
      System.out.println(c.readTodayRotation());
   }
}
