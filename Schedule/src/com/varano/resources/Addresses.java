//Thomas Varano
//[Program Descripion]
//Jan 28, 2018

package com.varano.resources;

/**
 * Holds addresses for different folders, websites, and locations. 
 * 
 * @author Thomas Varano
 *
 */
public final class Addresses {
   
   public static final String CONTACT_EMAIL = "pascackagendamailer@gmail.com";
   
   public static final String ICS_URL = "https://calendar.google.com/calendar/ical/8368c5a91jog3s32oc6k22f4e8%40group.calendar.google.com/public/basic.ics";
   
   public static final String CALENDAR_URL = "https://calendar.google.com/calendar/embed?src=8368c5a91jog3s32oc6k22f4e8%40group.calendar.google.com&ctz=America%2FNew_York";
   
   public static final String SOURCE = "https://github.com/tvarano54/schedule-new";
   
   public static final java.net.URI sourceURI() {
      try {
         return new java.net.URI(SOURCE);
      } catch (java.net.URISyntaxException e) {
         com.varano.managers.Agenda.logError("source URI failed to load", e);
         return null;
      }
   }
   
   public static final String NEWS = "http://agendapascack.x10host.com/updates/news.html";
   
   public static final String GITHUB_ISSUES = SOURCE + "/issues?q=is%3Aopen+is%3Aissue";
   
   public static final String CANVAS = "https://pascack.instructure.com/";
   
   public static final String GENESIS = "https://students.pascack.k12.nj.us/genesis/";
   
   public static final String PHHS_HOME = "http://hills.pascack.org/";
   
   public static final String NAVIANCE = "http://connection.naviance.com/phhs";
      
   public static final String getExec() {
      if (com.varano.managers.Agenda.isApp)
         return System.getProperty("java.class.path").substring(0, System.getProperty("java.class.path").indexOf(".app")) + ".app";
      return System.getProperty("java.class.path");
   }
   
   public static final String getDir() {
      return getExec().substring(0, getExec().indexOf("Agenda.app"));
   }
   
   public static final String getHome() {
      if (com.varano.managers.Agenda.isApp)
         return getExec() + "/Contents/Resources/Internal/";
      return System.getProperty("user.home") + "/Applications/Agenda/";
   }
   
   public static final String DATABASE = "https://tvarano54.github.io/agenda/data/";
   public static final String ROTATION_HOME = DATABASE + "rotations/";
   public static final String DAY_TYPE_HOME = DATABASE + "daytypes/";
   public static final String UPDATES_HOME = DATABASE + "updates/";
   
   public static java.net.URI createURI(String path) {
      try {
         return new java.net.URI(path);
      } catch (java.net.URISyntaxException e) {
         com.varano.information.constants.ErrorID.showError(e, true);
         return null;
      }
   }
   
   public static java.net.URL createURL(String path) {
      try {
         return new java.net.URL(path);
      } catch (java.net.MalformedURLException e) {
         com.varano.information.constants.ErrorID.showError(e, true);
         return null;
      }
   }
}
