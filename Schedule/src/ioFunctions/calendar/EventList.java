//Thomas Varano
//[Program Descripion]
//Jan 29, 2018

package ioFunctions.calendar;

import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;

import managers.UIHandler;

/**
 * was to show the user the calendar items, but now will be replaced by a link to the calendar.
 * @author varanoth
 *
 */
@Deprecated
public class EventList extends JPanel{
   private static final long serialVersionUID = 1L;
//   private VCalendar cal;
   private JList<String> dates, events;
   public EventList(VCalendar cal) {
//      this.cal = cal;
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      create(cal);
      addComponents();
   }
   
   private void create(VCalendar cal) {
      DefaultListModel<String> dm = new DefaultListModel<String>(); 
      dates = new JList<String>(dm);
      dates.setFont(UIHandler.font);
      dates.setBackground(UIHandler.background);
      dates.setForeground(UIHandler.foreground);
      DefaultListModel<String> em = new DefaultListModel<String>(); 
      events = new JList<String>(em);
      events.setFont(UIHandler.font);
      events.setBackground(UIHandler.background);
      events.setForeground(UIHandler.foreground);
      events.addListSelectionListener(selectionListener(dates));
      dates.addListSelectionListener(selectionListener(events));
      ArrayList<VEvent> evs = cal.events();
      dm.removeAllElements();
      em.removeAllElements();
      for (int i = evs.size() - 1; i >= 0; i--) {
         dm.addElement(evs.get(i).getStart().toString());
         em.addElement(evs.get(i).getSummary());
      }
   }
   
   private javax.swing.event.ListSelectionListener selectionListener(JList<?> other) {
      return new javax.swing.event.ListSelectionListener() {
         @SuppressWarnings("unchecked")
         @Override
         public void valueChanged(ListSelectionEvent arg0) {
            other.setSelectedIndex(((JList<String>) arg0.getSource()).getSelectedIndex());
         }
      };
   }
   
   private void addComponents() {
      add(dates);
      add(events);
   }
   
   public JScrollPane scrollable() {
      return new JScrollPane(this);
   }
   
   public static final int PREF_W = 200, ENTRY_H = 25;
   public java.awt.Dimension getPreferredSize() {
      return new java.awt.Dimension(PREF_W/2, ENTRY_H * events.getModel().getSize());
   }
   
   public static void show(VCalendar v) {
      long start = System.currentTimeMillis();
      javax.swing.JFrame f = new javax.swing.JFrame(managers.Agenda.APP_NAME + " Rotations");
      f.getContentPane().add(new EventList(v).scrollable());
      f.pack();
      f.setResizable(false);
      f.setLocationRelativeTo(null);
      f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      f.setVisible(true);
      
      managers.Agenda.log("showing list took "+ (System.currentTimeMillis() - start));
   }
}
