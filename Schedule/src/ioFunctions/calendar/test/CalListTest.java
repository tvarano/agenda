//Thomas Varano
//[Program Descripion]
//Jan 29, 2018

package ioFunctions.calendar.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.swing.JFrame;

import ioFunctions.calendar.CalReader;
import ioFunctions.calendar.EventList;
import ioFunctions.calendar.VCalendar;

public class CalListTest {
   public static void main(String[] args) {
      System.out.println("1");
      VCalendar test = VCalendar.test(); /*
      VCalendar v = null;
      try {
        v = new CalReader().readAndExtractEvents();
      } catch (ExecutionException | TimeoutException | InterruptedException e) {
         e.printStackTrace();
      } */
      System.out.println("a");
      JFrame f = new JFrame("cal list test");
      f.getContentPane().add(new EventList(test).scrollable());
      f.setVisible(true);
      System.out.println("b");
   }
}
