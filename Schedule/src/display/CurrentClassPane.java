package display;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.LocalTime;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import information.ClassPeriod;
import information.Schedule;
import information.Time;
import managers.UIHandler;

//Thomas Varano
//[Program Descripion]
//Sep 14, 2017

public class CurrentClassPane extends JPanel
{
   private static final long serialVersionUID = 1L;
   private ClassInfoPane info;
   private DisplayMain parentPane;
   private Time currentTime;
   private PrimaryCurrentClassPane northPane;
//   private SecondaryCurrentClassPane southPane;
   private ScheduleList westList;
   private ClassPeriod classPeriod;
   private Schedule sched;
   private boolean inSchool; 
   private boolean debug = false;
   
   public CurrentClassPane(ClassPeriod c, Schedule s, DisplayMain parent) {
      setName("currentClassPane");
      currentTime = new Time(LocalTime.now().getHour(), LocalTime.now().getMinute());
      inSchool = (c == null);
      setClassPeriod(c); setSched(s); setParentPane(parent);
      setBackground(UIHandler.background);
      sched.setShowName(true);
      if (debug) {
         System.out.println("classPane class:"+c);
         System.out.println("classPane sched:"+getSched());
      }
//      setLayout(new GridLayout(1,2));
      setLayout(new BorderLayout());
      
      
      westList = new ScheduleList(s, true);
      westList.setName("southPane todayList");
      westList.setParentPane(this);
      westList.setSelectedValue(s.get(c.getSlot()), true); 
      westList.setToolTipText("Today's Schedule With Your Current Class");
      westList.setSelectable(false);
      
      JScrollPane scroll = new JScrollPane(westList);
      scroll.setBorder(UIHandler.getTitledBorder("Today's Schedule"));
      scroll.setToolTipText(westList.getToolTipText());
      scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      scroll.setOpaque(false);
      scroll.setSize(320, getHeight());
      
      
      
      northPane = new PrimaryCurrentClassPane(c, this);
//      southPane = new SecondaryCurrentClassPane(c, s, this);
      add(northPane);
//      add(southPane, BorderLayout.EAST);
      add(eastSide(scroll), BorderLayout.EAST);
   }
   
   public boolean checkInSchool() {
      inSchool = ((DisplayMain)parentPane).checkInSchool();
      return inSchool;
   }
   
   public JPanel eastSide(JScrollPane scroll) {
      JPanel retval = new JPanel();
      retval.setLayout(new BorderLayout());
      retval.add(scroll);
      JPanel sizeFix = new JPanel();
      sizeFix.setPreferredSize(new Dimension(200, 0));
      retval.add(sizeFix, BorderLayout.SOUTH);
      return retval;
   }
   
   /**
    * assumes the user is in between classes
    * @return
    */
   public ClassPeriod findNextClass() {
      return ((DisplayMain)parentPane).findNextClass();
   }
   
   public Time timeUntilNextClass() {
      return ((DisplayMain)parentPane).timeUntilNextClass();
   }
   
   public Time timeUntilSchool() {
      return ((DisplayMain)parentPane).timeUntilSchool();
   }
   
   public ClassPeriod findPreviousClass() {
      if (inSchool)
         return sched.classAt(new Time(currentTime.getTotalMins()-5));
      return null;
   }
   

   public void update() {
      westList.autoSetSelection();
      northPane.update();
      revalidate();
   }
   
   public Dimension getMinimumSize() {
      return new Dimension(100, 200);
   }
   public Dimension getPreferredSize() {
      return new Dimension(550, (int) parentPane.getPreferredSize().getHeight());
   }
   public void pushCurrentTime(Time t) {
      setCurrentTime(t);
      northPane.pushCurrentTime(t);
   }
   public void pushClassPeriod(ClassPeriod c) {
      if (debug) System.out.println(getName() + " pushed " +c);
      setClassPeriod(c);
      checkInSchool();
      northPane.pushClassPeriod(c);
//      southPane.pushClassPeriod(c);
         
   }
   
   public void pushCurrentSlot(int slot) {
      setClassPeriod(sched.get(slot));
      checkInSchool();
      northPane.pushCurrentSlot(slot);
//      southPane.pushCurrentSlot(slot);
   }
   public void pushTodaySchedule(Schedule s) {
      setSched(s);
//      southPane.pushTodaySchedule(s);
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
   public ClassInfoPane getInfo() {
      return info;
   }
   public PrimaryCurrentClassPane getNorthPane() {
      return northPane;
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
//   public SecondaryCurrentClassPane getSouthPane() {
//      return southPane;
//   }
}
