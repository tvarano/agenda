package com.varano.ui.display.current;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.time.LocalTime;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.varano.information.ClassPeriod;
import com.varano.information.Schedule;
import com.varano.information.Time;
import com.varano.information.constants.RotationConstants;
import com.varano.managers.Agenda;
import com.varano.managers.UIHandler;
import com.varano.resources.ResourceAccess;
import com.varano.ui.Notif;
import com.varano.ui.display.DisplayMain;
import com.varano.ui.display.selection.ScheduleList;

//Thomas Varano
//[Program Descripion]
//Sep 14, 2017

public class CurrentClassPane extends JPanel
{
   private static final long serialVersionUID = 1L;
   private DisplayMain parentPane;
   private Time currentTime;
   private CurrentInfo info;
   private ScheduleList list;
   private ClassPeriod classPeriod;
   private Schedule sched;
   private final int LIST_W = 200;
   private boolean inSchool, notifUp; 
   private Notif notif;
   private boolean debug = false;
   
   public CurrentClassPane(ClassPeriod c, Schedule s, DisplayMain parent) {
      setName("currentClassPane");
      currentTime = new Time(LocalTime.now().getHour(), LocalTime.now().getMinute());
      inSchool =  parent.checkInSchool();
      setClassPeriod(c); setSched(s); setParentPane(parent);
      setBackground(UIHandler.background);
      sched.setShowName(true);
      if (debug) {
         System.out.println("classPane class:"+c);
         System.out.println("classPane sched:"+getSched());
      }
      setLayout(new BorderLayout());
      
      initAndAddComponents();
   }
   
   private void initAndAddComponents() {
      list = new ScheduleList(sched, true);
      list.setName("southPane todayList");
      list.setParentPane(this);
      list.setSelectedValue(sched.get(classPeriod.getSlot()), true); 
      list.setToolTipText("Today's Schedule With Your Current Class");
      list.setSelectable(false);
      
      JScrollPane scroll = new JScrollPane(list);
      scroll.setBorder(UIHandler.getTitledBorder("Today's Schedule"));
      scroll.setToolTipText(list.getToolTipText());
      scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      scroll.setBackground(UIHandler.background);
      list.setBackground(UIHandler.secondary);
      scroll.setSize(320, getHeight());
            
      info = new CurrentInfo(classPeriod, this);
      info.setVisible(true);
      int gap = 10;
      info.setBorder(BorderFactory.createEmptyBorder(gap, gap, gap, gap));
      
      add(listPanel(scroll), BorderLayout.EAST);
      add(info, BorderLayout.CENTER);
   }
   
   public boolean checkInSchool() {
      inSchool = parentPane.checkInSchool();
      return inSchool;
   }
   
   private JPanel listPanel(JScrollPane scroll) {
      JPanel retval = new JPanel();
      retval.setName("list panel");
      retval.setLayout(new BorderLayout());
      retval.add(scroll);
      JPanel sizeFix = new JPanel();
      sizeFix.setPreferredSize(new Dimension(LIST_W, 0));
      retval.add(sizeFix, BorderLayout.SOUTH);
      return retval;
   }
   
   /**
    * assumes the user is in between classes
    * @return
    */
   public ClassPeriod findNextClass() {
      return parentPane.findNextClass();
   }

   public Time getTimeLeft() {
      if (classPeriod == null) return Time.NO_TIME;
      if (classPeriod.getSlot() == RotationConstants.LUNCH
            && parentPane.labToday() != null
            && parentPane.labToday().getTimeAtLab().getStartTime().compareTo(getCurrentTime()) > 0) {
         return currentTime.getTimeUntil(parentPane.labToday().getTimeAtLab().getStartTime());
      }
      return currentTime.getTimeUntil(classPeriod.getEndTime());
   }
   
   public Time timeUntilNextClass() {
      return parentPane.timeUntilNextClass();
   }
   
   public Time timeUntilSchool() {
      return parentPane.timeUntilSchool();
   }
   
   public ClassPeriod findPreviousClass() {
      if (inSchool)
         return sched.classAt(new Time(currentTime.getTotalMins()-5));
      return null;
   }
   
   public void update() {
      list.autoSetSelection();
      info.repaintText();
      checkAndShowNotification();
      revalidate();
   }

   public void checkAndShowNotification() {
      if (!parentPane.getParentManager().getParent().shouldShowNotif()) return;
      if (inSchool) {
         if (getTimeLeft().getTotalMins() == 5) {
            if (classPeriod.equals(RotationConstants.getNoSchoolClass())) return;
            if (!notifUp) {
               try {
                  Agenda.log("showing notification");
                  ClassPeriod after = parentPane.findClassAfter();
                  String nameText = (after == null)
                        ? ""
                        : after.getTrimmedName() + " is next";
                  String roomText = (after == null || after.getRoomNumber().equals(ClassPeriod.NO_ROOM)) 
                        ? "" : "\nIn " + after.getTrimmedRoomNumber();
                  parentPane.findClassAfter().getRoomNumber();
                  notif = new com.varano.ui.Notif(parentPane.getParentManager().getParent().getFrame(), 
                        "There are 5 Minutes Left.\n"+nameText + roomText,
                        ImageIO.read(ResourceAccess
                              .getResourceStream("Agenda Logo.png")));
                  if (roomText.equals("")) notif.setPreferredSize(Notif.TWO_LINE_SIZE);
                  notifUp = true;
                  parentPane.getParentManager().getParent().revalidate();
//                  parentPane.getParentManager().getParent().requestFocus();
               } catch (IOException e) {
                  Agenda.logError("error in showing notification", e);
               }
            }
         } else {
            collapseNotif();
         }
      }
   }
   
   public void collapseNotif() {      
      notifUp = false;
      if (notif != null) notif.disappear();
      notif = null;
   }
   
//   public Dimension getMinimumSize() {
//      return new Dimension(100, 200);
//   }
   public Dimension getContentSize() {
      return info.getPreferredScrollableViewportSize();
   }
   public double getContentHeight() {
      if (debug) System.out.println("content height " +getContentSize().getHeight()); 
      return getContentSize().getHeight() <= 0 ? getPreferredSize().getHeight() : getContentSize().getHeight();
   }
   public Dimension getPreferredSize() {
      return new Dimension((int) parentPane.getPreferredSize().getWidth(), 250);
   }
   public void pushCurrentTime(Time t) {
      setCurrentTime(t);
      update();
   }
   public void pushClassPeriod(ClassPeriod c) {
      if (debug) System.out.println(getName() + " pushed " +c);
      setClassPeriod(c);
      checkInSchool();
      info.pushClassPeriod(c);
      list.setSelectedValue(classPeriod, true);
         
   }
   
   public void pushCurrentSlot(int slot) {
      pushClassPeriod(sched.get(slot));
      checkInSchool();
   }
   public void pushTodaySchedule(Schedule s) {
      setSched(s);
      list.setSchedule(s);
   }   
   public ScheduleList getList() {
      return list;
   }
   public boolean isInSchool() {
      return inSchool;
   }
   public void setInSchool(boolean inSchool) {
      this.inSchool = inSchool;
   }
   public void setSched(Schedule sched) {
      this.sched = sched;
   }
   public Schedule getSched() {
      return sched;
   }
   public ClassPeriod getClassPeriod() {
      return classPeriod;
   }
   public void setClassPeriod(ClassPeriod classPeriod) {
      this.classPeriod = classPeriod;
   }
   public CurrentInfo getInfo() {
      return info;
   }
   public DisplayMain getParentPane() {
      return parentPane;
   }
   public void setParentPane(DisplayMain parent) {
      this.parentPane = parent;
   }
   public Time getCurrentTime() {
      return currentTime;
   }
   public void setCurrentTime(Time currentTime) {
      this.currentTime = currentTime;
   }
   public int getCurrentSlot() {
      return classPeriod.getSlot();
   }
   public boolean isNotifUp() {
      return notifUp;
   }
   public void setNotifUp(boolean notifUp) {
      this.notifUp = notifUp;
   }
   public Notif getNotif() {
      return notif;
   }
}
