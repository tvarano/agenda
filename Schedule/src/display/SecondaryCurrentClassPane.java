package display;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import information.ClassPeriod;
import information.Schedule;
import information.Time;
import managers.UIHandler;

//Thomas Varano
//[Program Descripion]
//Sep 14, 2017

/**
 *@deprecated not needed at all. 
 * @author varanoth
 *
 */
public class SecondaryCurrentClassPane extends JPanel
{
   private static final long serialVersionUID = 1L;
   private final int LIST_W = 230;
   private ClassPeriod classPeriod;
   private int currentSlot;
   private Schedule s;
   private ScheduleList westList;
   private Time ct;
   private CurrentClassPane parentPanel;
   
   public SecondaryCurrentClassPane(ClassPeriod c, Schedule s, CurrentClassPane parent) {
      setClassPeriod(c); setSchedule(s); setParentPanel(parent); setCurrentSlot(c.getSlot());
      setBackground(UIHandler.background);
            
      westList = new ScheduleList(s, true);
      westList.setName("southPane todayList");
      westList.setParentPane(this);
      westList.setSelectedValue(s.get(currentSlot), true); 
      westList.setToolTipText("Today's Schedule With Your Current Class");
      
      JScrollPane scroll = new JScrollPane(westList);
      scroll.setBorder(UIHandler.getTitledBorder("Today's Schedule"));
      scroll.setToolTipText(westList.getToolTipText());
      scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      scroll.setOpaque(false);
      add(scroll);
      
      westList.setSelectable(false);
//      westList.setPreferredSize(new Dimension(LIST_W, getHeight()));
   }
   
//   public Dimension getPreferredSize() {
//      return new Dimension(LIST_W+30, getHeight());
//   }
   
   public void update() {
      westList.autoSetSelection();
   }
   
   public void pushTodaySchedule(Schedule s) {
      setSchedule(s);
      westList.setSchedule(s);
   }
   
   public boolean checkInSchool() {
      if (parentPanel == null)  return false;
      return parentPanel.checkInSchool();
   }
   
   public ClassPeriod findNextClass() {
      if (parentPanel == null) return null;
      return parentPanel.findNextClass();
   }
   
   public void updateListAndInfo() {
      westList.setSelectedValue(classPeriod, true);
   }
   
   public void pushCurrentTime(Time t) {
      setCt(t);
   }
   
   public Schedule getS() {
      return s;
   }
   public void setS(Schedule s) {
      this.s = s;
   }
   public CurrentClassPane getParentPanel() {
      return parentPanel;
   }
    
   public void setParentPanel(CurrentClassPane parent) {
      this.parentPanel = parent;
   }
   
   public ScheduleList getWestList() {
      return westList;
   }
   
   public void pushClassPeriod(ClassPeriod c) {
      setClassPeriod(c);
      updateListAndInfo();
   }
   
   public void pushCurrentSlot(int slot) {
      setCurrentSlot(slot);
      pushClassPeriod(s.get(slot));
   }

   private void setCurrentSlot(int slot) {
      this.currentSlot = slot;
   }
   public int getCurrentSlot() {
      return currentSlot;
   }
   public ClassPeriod getClassPeriod() {
      return classPeriod;
   }
   public void setClassPeriod(ClassPeriod classPeriod) {
      setCurrentSlot((classPeriod == null) ? -1 : classPeriod.getSlot());
      this.classPeriod = classPeriod;
   }
   public Schedule getSchedule() {
      return s;
   }
   public void setSchedule(Schedule s) {
      this.s = s;
   }
   public Time getCt() {
      return ct;
   }
   public void setCt(Time ct) {
      this.ct = ct;
   }
}
