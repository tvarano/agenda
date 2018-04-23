//Thomas Varano
//Mar 12, 2018

package com.varano.resources.ioFunctions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.Callable;

import com.varano.managers.Agenda;
import com.varano.managers.OrderUtility;
import com.varano.resources.Addresses;

public class AlertReader {
   public static final String delayKey = "delay", halfKey = "half day", noSchoolKey = "closed";
   public static final String alertKey = "<div id=\"schoolmessenger-alert\"><div class=\"row\"><div class=\"description\">";
   public static final String endKey = "</div>";
   public static final String stopKey = "<div id=\"homeNewsWrapper\" class=\"row\">";
   private static final boolean debug = false;
   private String html, message;
      
   public void init() {
      try {
         html = retrieveHtml();
//         html = decryptHtml(Addresses.createURL(Addresses.PHHS_HOME));
         if (debug) System.out.println(html);
         message = lookForMessage();
         if (debug) System.out.println("message: "+message);
      } catch (Exception e) {
         Agenda.logError("error in reading alerts", e);
         html = null;
         message = null;
      }
   }
   
   private String lookForMessage() {
      if (!html.contains(alertKey)) 
         return null;
      return html.substring(html.indexOf(alertKey) + alertKey.length(),
            html.indexOf(endKey, html.indexOf(alertKey))).toLowerCase();
   }
   
   /**
    * another way of dealing with reading the html from the site, trying to sidestep encryption (unsuccessfully)
    */
   /*
   private static String decryptHtml(URL url) throws Exception {
      java.net.URLConnection con = url.openConnection();
      java.io.InputStream in = con.getInputStream();
      String encoding = con.getContentType().substring(con.getContentType().indexOf("charset=")+"charset=".length());  // ** WRONG: should use "con.getContentType()" instead but it returns something like "text/html; charset=UTF-8" so this value must be parsed to extract the actual encoding
      System.out.println(encoding);
      encoding = encoding == null ? "UTF-8" : encoding;
      java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
      byte[] buf = new byte[8192];
      int len = 0;
      while ((len = in.read(buf)) != -1) {
          baos.write(buf, 0, len);
      }
      String body = new String(baos.toByteArray(), encoding);      System.out.println(body);
      return body;
   }
   */
   
   
   private static String readHtml(URL site) throws IOException {
      BufferedReader in = null;
      in = new BufferedReader(new InputStreamReader(site.openStream()));
      if (!in.readLine().contains("<!DOCTYPE html>")) {
         Agenda.log(Addresses.PHHS_HOME + " html not read correctly");
         return "";
      }
      StringBuilder b = new StringBuilder();
      String inputLine;
      while ((inputLine = in.readLine()) != null && !inputLine.contains(stopKey)) {
         b.append(inputLine);
         b.append("\n");
      }
      in.close();
      return b.toString();
   }
   
   private static final int MILLIS_TO_WAIT = 2000;
   private static String retrieveHtml() throws Exception {
      return OrderUtility.futureCall(MILLIS_TO_WAIT, new Callable<String>() {
         @Override
         public String call() throws Exception {
            return readHtml(Addresses.createURL(Addresses.PHHS_HOME));
         }
      }, "look for alerts on "+ Addresses.PHHS_HOME);
   }
   
   public String checkAlerts() {
      return extract();
   }
   
   private String extract() {
      if(message == null)
         return null;
      if (LocalDate.now().equals(checkDelay()))
         return delayKey;
      if (LocalDate.now().equals(checkHalf()))
         return halfKey;
      if (LocalDate.now().equals(checkNoSchool()))
         return noSchoolKey;
      return null;
   }
   
   private LocalDate checkDelay() {
      if (message.contains(delayKey) && message.contains("90"))
         return searchForDate();
      return null;
   }
   
   private LocalDate checkHalf() {
      if (message.contains(halfKey) || message.contains("early dismissal"))
         return searchForDate();
      return null;
   }
   
   private LocalDate checkNoSchool() {
      if (message.contains(noSchoolKey))
         return searchForDate();
      return null;
   }
   
   private LocalDate searchForDate() {
      if (message.contains("today"))
         return LocalDate.now();
      if (message.contains("tomorrow"))
         return LocalDate.now().plusDays(1);
      for (DayOfWeek d : DayOfWeek.values())
         if (message.contains(d.name().toLowerCase()))
            return nextOccuranceOf(d);
      
      for (Month m : Month.values()) 
         if (message.contains(m.name().toLowerCase()))
            return LocalDate.of(LocalDate.now().getYear(), m.getValue(), 
                  findContinuousInt(message.substring(message.indexOf(m.name().toLowerCase()) + m.name().length() + 1)));
      return null;
   }
   
   private LocalDate nextOccuranceOf(DayOfWeek d) {
      LocalDate ret = LocalDate.now();
      while (!ret.getDayOfWeek().equals(d))
         ret = ret.plusDays(1);
      return ret;
   }
   
   private int findContinuousInt(String in) {
      int index = 0;
      String retS = ""; //48-57
      while (in.charAt(index) > 47 && in.charAt(index) < 58) {
         retS += in.charAt(index);
         index++;
      }
      return Integer.parseInt(retS);
   }
   
   public static void main(String[] args) {
      AlertReader a = new AlertReader();
      a.init();
      System.out.println(a.checkAlerts());
      System.exit(0);
   }
}
