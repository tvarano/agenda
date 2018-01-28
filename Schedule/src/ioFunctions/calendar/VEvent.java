//Thomas Varano
//[Program Descripion]
//Jan 26, 2018

package ioFunctions.calendar;

import java.time.LocalDate;

public class VEvent {
   private LocalDate start, end;
   private String summary, detail;
   
   public VEvent(String summary, LocalDate start, LocalDate end) {
      setSummary(summary); setStart(start); setEnd(end);
   }
   
   public VEvent(String summary) {
      this(summary, LocalDate.MIN, LocalDate.MIN);
   }
   
   public VEvent() {
      this("");
   }
   
   public static LocalDate translateDate(String dateString) {
      return LocalDate.of(Integer.parseInt(dateString.substring(0,4)), 
            Integer.parseInt(dateString.substring(4,6)), 
            Integer.parseInt(dateString.substring(6,8)));
   }
   
   //TODO should it include end?
   public boolean contains(LocalDate d) {
      return (d.isAfter(start) && d.isBefore(end)) || d.isEqual(start);
   }

   public LocalDate getStart() {
      return start;
   }
   public void setStart(LocalDate start) {
      this.start = start;
   }
   public LocalDate getEnd() {
      return end;
   }
   public void setEnd(LocalDate end) {
      this.end = end;
   }
   public String getSummary() {
      return summary;
   }
   public void setSummary(String summary) {
      this.summary = summary;
   }
   public String getDetail() {
      return detail;
   }
   public void setDetail(String detail) {
      this.detail = detail;
   }
}
