package display;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.time.LocalTime;

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
      scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      scroll.setOpaque(false);
      scroll.setSize(320, getHeight());
      
      info = new CurrentInfo(classPeriod, this);
      info.setVisible(true);
      int gap = 10;
      info.setBorder(BorderFactory.createEmptyBorder(gap, gap, gap, gap));
      
      add(listPanel(scroll), BorderLayout.EAST);
      add(info, BorderLayout.CENTER);
   }
   
   public boolean checkInSchool() {
      inSchool = ((DisplayMain)parentPane).checkInSchool();
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
      return ((DisplayMain)parentPane).findNextClass();
   }
   
   public Time getTimeLeft() {
      return currentTime.getTimeUntil(classPeriod.getEndTime());
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
      list.autoSetSelection();
      info.repaintText();
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
}
