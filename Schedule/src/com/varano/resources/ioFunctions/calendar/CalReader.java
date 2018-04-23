//Thomas Varano
//[Program Descripion]
//Jan 26, 2018

package com.varano.resources.ioFunctions.calendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import com.varano.information.constants.Rotation;
import com.varano.information.constants.RotationConstants;
import com.varano.managers.Agenda;
import com.varano.managers.OrderUtility;
import com.varano.resources.ioFunctions.AlertReader;

public class CalReader {
   private static URL rotationDataSite;
   private static boolean urlClear, calClear;
   private AlertReader alerts;
   private VCalendar cal;
   private boolean debug;

   public CalReader(AlertReader alert) {
      alerts = alert;
      init();
   }
   
   public void init() {
      debug = false;
      long start = System.currentTimeMillis();
      try {
         rotationDataSite = new URL(com.varano.resources.Addresses.ICS_URL);
         urlClear = true;
      } catch (MalformedURLException e) {
         urlClear = false;
         Agenda.logError("error with .ics url", e);
      }
      if (urlClear) {
         try {
            cal = readAndExtractEvents();
            calClear = true;
            Agenda.log("cal reader successfully initialized");
         } catch (Exception e) {
            calClear = false;
            Agenda.log("exception in cal reading");
         }
      } else
         calClear = false;
      if (debug) System.out.println("calendar import and format took " + (System.currentTimeMillis() - start));
   }

   public Rotation readTodayRotation() {
      if (calClear) {
         for (VEvent e : cal.eventsToday()) {
            String s = e.getSummary();
            String alert;
            if ((alert = alerts.checkAlerts()) != null) {
               Agenda.log("alert found : " + alert);
               if (alert.equals(AlertReader.delayKey))
                  return RotationConstants.toDelay(checkPerfectSyntax(s), true, 
                        RotationConstants.toDelay(RotationConstants.todayRotation(), true, Rotation.DELAY_R1));
               if (alert.equals(AlertReader.halfKey))
                  return RotationConstants.toHalf(checkPerfectSyntax(s), true, 
                        RotationConstants.toHalf(RotationConstants.todayRotation(), true, Rotation.HALF_R1));
               if (alert.equals(AlertReader.noSchoolKey))
                  return Rotation.NO_SCHOOL;
            }
            if (s.contains("10:00") || s.contains("Arrival")) {
               Agenda.log("ROTATION: 10:00 open read from internet");
               return Rotation.DELAY_ARRIVAL;
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
            if (RotationConstants.getRotation(s) != null) {
               Agenda.log("ROTATION: " + s + " read from internet");
               return RotationConstants.getRotation(s);
            }
         }
      }
      Agenda.log("no valid events found. rotation read from day.");
      return RotationConstants.todayRotation();
   }
   
   private static Rotation checkPerfectSyntax(String s) {
      if (RotationConstants.getRotation(s) != null)
         return RotationConstants.getRotation(s);
      return RotationConstants.todayRotation();      
   }
   
   public VCalendar readAndExtractEvents() throws Exception {
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
      events.remove(events.size() - 1);
      Agenda.log("calendar format took "+(System.currentTimeMillis() - start));
      return VCalendar.build(events);
   }
   
   private static final long MILLIS_TO_WAIT = 8000L;
   public String retrieveRfc() throws Exception {
     return OrderUtility.futureCall(MILLIS_TO_WAIT, this::readRfc, "ics reading");
   }

   /**
    * reads the rfc of the day. 
    * has one more 
    * @return
    * @throws IOException
    */
   private String readRfc() throws IOException {
      BufferedReader in = null;
      if (debug) System.out.println("begun reading");
      in = new BufferedReader(
            new InputStreamReader(rotationDataSite.openStream()));
      StringBuilder b = new StringBuilder();
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
         if (inputLine.contains(DTSTART_PREFIX + (LocalDate.now().getYear() - 1)))
               break;
         b.append(inputLine);
         b.append("\n");
      }
      if (debug) System.out.println("done reading");
      in.close();
      return b.toString();
   }
}
