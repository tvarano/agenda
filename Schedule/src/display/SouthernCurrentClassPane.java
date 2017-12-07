package display;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import constants.RotationConstants;
import information.ClassPeriod;
import information.Schedule;
import information.Time;
import managers.UIHandler;

//Thomas Varano
//[Program Descripion]
//Sep 14, 2017

public class SouthernCurrentClassPane extends JPanel
{
   private static final long serialVersionUID = 1L;
   private final int LIST_W = 100;
   private ClassPeriod classPeriod;
   private int currentSlot;
   private Schedule s;
   private MemoPad memo;
   private ScheduleList westList;
   private Time ct;
   private CurrentClassPane parentPanel;
   
   public SouthernCurrentClassPane(ClassPeriod c, Schedule s, CurrentClassPane parent) {
      setClassPeriod(c); setSchedule(s); setParentPanel(parent); setCurrentSlot(c.getSlot());
      setBackground(UIHandler.background);
      
      setLayout(new GridLayout(1,2));
      
      memo = new MemoPad(c, this);
      memo.setEnabled(false);
      
      westList = new ScheduleList(s, true);
      westList.setName("southPane todayList");
      westList.setParentPane(this);
      westList.setSelectedValue(s.get(currentSlot), true); 
      westList.setToolTipText("Today's Schedule With Your Current Class");
      
      JScrollPane scroll = new JScrollPane(westList);
      scroll.setBorder(UIHandler.getTitledBorder("Today's Schedule"));
      scroll.setToolTipText(westList.getToolTipText());
      scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      scroll.setOpaque(false);
      add(scroll);
      
      scroll = new JScrollPane(memo);
      scroll.setBorder(UIHandler.getTitledBorder("Select Class For Memo"));
      scroll.setToolTipText(westList.getToolTipText());
      scroll.setOpaque(false);
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
   public void setMemoClass(ClassPeriod c) {
      setMemoBorderTitle(c);
      memo.setParentClass(c);
      memo.repaint();
   }
   
   public void setMemoBorderTitle(Object o) {
      ((JScrollPane)memo.getParent().getParent()).setBorder(UIHandler.getTitledBorder(o + " Memo"));

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
   public MemoPad getMemo() {
      return memo;
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
