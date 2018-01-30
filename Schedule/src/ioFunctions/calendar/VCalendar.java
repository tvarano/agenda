//Thomas Varano
//[Program Descripion]
//Jan 26, 2018

package ioFunctions.calendar;

import java.time.LocalDate;
import java.util.ArrayList;

public class VCalendar {
   private ArrayList<VEvent> events;
   private VCalendar() {
      events = new ArrayList<VEvent>();
   }
   private VCalendar(ArrayList<VEvent> events) {
      this.events = events;
   }
   
   public static VCalendar build(ArrayList<VEvent> events) {
      return new VCalendar(events);
   }
   
   public static VCalendar test() {
      ArrayList<VEvent> v = new ArrayList<VEvent>();
      for (char i = 'A'; i < 'z'; i++)
         v.add(new VEvent(i + ""));
      return new VCalendar(v);
   }
   
   public ArrayList<VEvent> events() {
      return events;
   }
   
   public ArrayList<VEvent> eventsToday() {
      ArrayList<VEvent> ret = new ArrayList<VEvent>();
      for (VEvent e : events)
         if (e.contains(LocalDate.now()))
            ret.add(e);
      return ret;
   }
   
   public String toString() {
      return getClass().getName() + "["+events.toString() + "]";
   }
}
