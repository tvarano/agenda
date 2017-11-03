package display;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import information.ClassPeriod;
import information.Schedule;
import information.Time;
import managers.UIHandler;

//Thomas Varano
//[Program Descripion]
//Sep 14, 2017

public class SouthernCurrentClassPane extends JPanel
{
   private final int LIST_W = 100;
   private ClassPeriod classPeriod;
   private int currentSlot;
   private Schedule s;
   private ScheduleList westList;
   private ClassInfoPane eastInfo;
   private Time ct;
   private CurrentClassPane parentPanel;
   
   public SouthernCurrentClassPane(ClassPeriod c, Schedule s, CurrentClassPane parent) {
      setClassPeriod(c); setSchedule(s); setParentPanel(parent); setCurrentSlot(c.getSlot());
      setBackground(UIHandler.background);
      
      setLayout(new GridLayout(1,2));
      eastInfo = new ClassInfoPane(c);
      eastInfo.setThinConstraints(true);
      westList = new ScheduleList(s, true);
      westList.setName("southPane todayList");
      westList.setParentPane(this);
      westList.setSelectedValue(s.get(currentSlot), true); 
      JScrollPane scroll = new JScrollPane(westList);
//      scroll.setBorder(BorderFactory.createTitledBorder("Today's Schedule"));
      scroll.setBorder(UIHandler.getTitledBorder("Today's Schedule"));
      scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      add(scroll);
      
      scroll = new JScrollPane(eastInfo);
//      scroll.setBorder(BorderFactory.createTitledBorder("Current Class Info"));
      scroll.setBorder(UIHandler.getTitledBorder("Current Class Info"));
      add(scroll);
      
      westList.setSelectable(false);
      westList.setSize(LIST_W, westList.getHeight());
   }
   
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
      eastInfo.setClassPeriod(classPeriod);
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
   
   public ClassInfoPane getEastInfo() {
      return eastInfo;
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
