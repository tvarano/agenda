package com.varano.ui.display;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.Timer;

import com.varano.information.ClassPeriod;
import com.varano.information.Schedule;
import com.varano.information.Time;
import com.varano.information.constants.ErrorID;
import com.varano.information.constants.Lab;
import com.varano.information.constants.Rotation;
import com.varano.information.constants.RotationConstants;
import com.varano.managers.Agenda;
import com.varano.managers.PanelManager;
import com.varano.managers.UIHandler;
import com.varano.resources.ioFunctions.AlertReader;
import com.varano.resources.ioFunctions.OrderUtility;
import com.varano.resources.ioFunctions.SchedReader;
import com.varano.resources.ioFunctions.SchedWriter;
import com.varano.resources.ioFunctions.calendar.CalReader;
import com.varano.ui.PanelView;
import com.varano.ui.display.current.CurrentClassPane;
import com.varano.ui.display.selection.ScheduleInfoSelector;
import com.varano.ui.tools.ToolBar;

//Thomas Varano
//Aug 31, 2017

/**
 * Responsible for displaying the home screen of the application. Everything not involving input and GPA is done here, 
 * usually constituting showing data and editing memos.
 * 
 * @param parentManager the {@linkplain PanelManager} responsible for handling the state of the application, and thereby 
 * this object.
 * @author Thomas Varano
 */
public class DisplayMain extends JPanel implements ActionListener, PanelView
{
   private static final long serialVersionUID = 1L;
   private PanelManager parentManager;
   private Schedule mainSched, todaySched;
   private Rotation todayR;
   private int lastRead;
   private CalReader cal;
   private Time currentTime;
   private CurrentClassPane currentClassPane;
   private ToolBar toolbar;
   private ScheduleInfoSelector infoSelector;
   private boolean updating, showDisp;
   private boolean debug, debugSave, testSituation;
   private Timer timer;
   
   public DisplayMain(PanelManager parentManager) {
      debug = false;
      debugSave = false;
      testSituation = true;
      showDisp = true;
      setBackground(UIHandler.tertiary);
      setParentManager(parentManager);
      initCalReader();
      setLayout(new BorderLayout());
      initComponents();
     
      addComponents();
      update();
      requestFocus();
      if (debug) {
         System.out.println("DELAY_EVEN TIMESSSS");
         for (ClassPeriod c : Rotation.DELAY_EVEN.getTimes())
            System.out.println(c.getInfo());
         System.out.println();
      }
      timer = new Timer(5000, this);
      timer.start();
      Agenda.log("display main fully initialized");
   }
   
   public Agenda getMain() {
      return parentManager.getMain();
   }
   
   private void initCalReader() {
      AlertReader alert = new AlertReader();
      java.awt.EventQueue.invokeLater(new Runnable() {
         public void run() {
            alert.init();
         }
      });
      cal = new CalReader(alert);
      initTime();      
   }
   
   public Rotation readRotation() {
      return cal.readTodayRotation();
   }
   
   public void setLastRead(LocalDate d) {
      lastRead = d.getDayOfYear();
      Agenda.log("schedule last read "+d);
   }
   
   private void initTime() {
      try {
         if (testSituation) {
            currentTime = new Time(10,23);
            todayR = Rotation.getRotation(DayOfWeek.MONDAY); 
            setLastRead(LocalDate.now());
         } else {
            currentTime = new Time(LocalTime.now());
            todayR = readRotation();
            setLastRead(LocalDate.now());
         }
      } catch (Throwable e) { 
         e.printStackTrace();
         ErrorID.showError(e, false);
      }
   }
   
   private void initComponents() {
      SchedReader r = new SchedReader();
      mainSched = r.readSched(); mainSched.setName("mainSched");
      if (debug) System.out.println("DISP 104 MAIN IS "+mainSched.classString(false));
      if (debug) System.out.println("\tDISP 96 GPA is " + mainSched.getGpaClasses().toString() );
      todaySched = r.readAndOrderSchedule(todayR); todaySched.setName("todaySched");
      if (debug && todaySched.get(RotationConstants.LUNCH) != null)
         System.out.println("today lunch" + todaySched.get(RotationConstants.LUNCH).getInfo());
      todaySched.setLunchLab(todayR); 

      if (debug) System.out.println("TODAY SCHED = "+todaySched);
      infoSelector = new ScheduleInfoSelector(todaySched, mainSched, this);

      currentClassPane = new CurrentClassPane(new ClassPeriod(), todaySched, this);
      toolbar = new ToolBar(PanelManager.DISPLAY, this);
      checkAndUpdateTime();
      currentClassPane.setClassPeriod(findCurrentClass());
      pushTodaySchedule();
      toolbar.setRotation(todayR);
   }
   
   private void addComponents() {
      add(toolbar, BorderLayout.NORTH);
      JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, currentClassPane, infoSelector);
      
      add(sp, BorderLayout.CENTER);
   }
   
   public void hardStop() {
	   timer.stop();
   }
   
   public synchronized void writeMain() {
      Agenda.log(getClass().getName()  + " wrote main Schedule");
      if (debugSave) System.out.println("MAIN SCHED WRITTEN");
      try {
         SchedWriter w = new SchedWriter();
         w.write(mainSched);
         w.close();
      } catch (Exception e) {
         ErrorID.showUserError(ErrorID.FILE_TAMPER);
         com.varano.managers.FileHandler.initAndCreateFiles();
      }
   }
   
   public void stop() {
      save();
      showDisp = false;
   }
   
   public void resume() {
      showDisp = true;
      update();
   }
   
   public void hardResume() {
	   showDisp = true;
	   timer.start();
   }
   
   public void reinitialize() {
      removeAll();
      initTime();
      initComponents();
      addComponents();
      resume();
   }
   
   public void setBarText(String s) {
      parentManager.getTimeMenu().setLabel(s);
   }
  
   public void setBarTime(Time timeLeft) {
      String prefix = "In "+ findCurrentClass() +" for: ";
      setBarText(prefix + timeLeft.durationString());
   }
   
   public void configureBarTime(ClassPeriod c) {
      if (c != null) {
         setBarTime(currentTime.getTimeUntil(c.getEndTime()));
         if (c.getSlot() == RotationConstants.NO_SCHOOL_TYPE)
            setBarText("No School");
      }
       else if (checkInSchool())
         setBarText(findNextClass() + " Is In: " + timeUntilNextClass().durationString());
       else 
          setBarText("Not In School");
   }
   
   public void checkAndUpdateTime() {
      if (testSituation) 
         currentTime = currentTime.plus(1);
      else 
         currentTime = new Time(LocalTime.now());
      checkAndUpdateDate();
      checkInSchool();
      findCurrentClass();
      currentClassPane.pushCurrentTime(currentTime);
   }
   
   public void checkAndUpdateDate() {
         if (LocalDate.now().getDayOfYear() != lastRead) {
            Agenda.log("day changed. read rotation");
            refresh();
         }
   }
   
   public void pushTodaySchedule() {
      currentClassPane.pushTodaySchedule(todaySched);
      infoSelector.pushTodaySchedule(todaySched);
   }
   
   /**
    * assumes the user is in between classes
    * @return
    */
   public ClassPeriod findNextClass() {
      if (checkInSchool())
         return todaySched.classAt(new Time(currentTime.getTotalMins()+5));
      return null;
   }
   
   /**
    * assumes the user is in between classes
    * @return
    */
   public ClassPeriod findClassAfter() {
      if (checkInSchool())
         return todaySched.classAt(new Time(currentTime.getTotalMins()+20));
      return null;
   }
   
   public ClassPeriod classForMemo(int slot) {
      if (debugSave && mainSched.get(slot) != null) System.out.println("\t should be "+mainSched.get(slot).memoryInfo());
      return (slot == RotationConstants.PASCACK) ? mainSched.getPascackPreferences() : mainSched.get(slot);
   }
   
   public Time timeUntilNextClass() {
      if (checkInSchool())
         return currentTime.getTimeUntil(findNextClass().getStartTime());
      return new Time();
   }
   
   public Time timeUntilSchool() {
      return currentTime.getTimeUntil(todaySched.getSchoolDay().getStartTime());
   }
   
   public boolean checkInSchool() {
      return todaySched.getSchoolDay().contains(currentTime);
   }

   public Lab labToday() {
      for (Lab l : todaySched.getLabs())
         if (todayR.equals(l.getRotation())) 
            return l;
      return null;
   }
   
   public ClassPeriod findCurrentClass(){
      // searching labs
      for (Lab l : todaySched.getLabs())
         if (todayR.equals(l.getRotation())) {
            if (debug) System.out.println("today's Lab info: "+l.getTimeAtLab().getInfo());
            if (l.getTimeAtLab().contains(currentTime)) {
               if (debug) System.out.println("DISPLAY SHOULD BE PRINTING LAB");
               currentClassPane.pushClassPeriod(l.getTimeAtLab());
               return l.getTimeAtLab();
            }
         }
      //then classes
      ClassPeriod[] cp = todaySched.getClasses();
      if (debug) System.out.println("updating"+currentTime);
      for (ClassPeriod c : cp) {
         if (debug) System.out.println("display searching "+ c +"for time");
         if (c.contains(currentTime)) {
            if (debug) System.out.println("display pushing "+c.getInfo());
            if (!c.equals(currentClassPane.getClassPeriod()))
               currentClassPane.pushClassPeriod(c);
            return c;
         }
      }
      currentClassPane.pushClassPeriod(null);
      return null;
   }
      
   public void update() {
      setUpdating(true);
      if (debug) System.out.println("DISP 284 background: "+getBackground() + " and location " + 
            Integer.toHexString(getBackground().hashCode()));
      checkAndUpdateTime();
      ClassPeriod current = findCurrentClass();
      configureBarTime(current);
      if (infoSelector.getMemo().hasChanges()) {
         infoSelector.getMemo().save();
         writeMain();
      }
      
      if (showDisp) {
         currentClassPane.update();
         repaint();
      } else currentClassPane.checkAndShowNotification();
      setUpdating(false);
   }
   
   public void repaint() {
      if (toolbar != null) toolbar.setHighlights();
      super.repaint();
   }
   
   public ActionListener changeView(int type) {
      return parentManager.changeViewListener(type);
   }
   
   public Dimension getMinimumSize() {
      return new Dimension(Agenda.MIN_W,Agenda.MIN_H);
   }
   public Dimension getPreferredSize() {
      return new Dimension(Agenda.PREF_W, Agenda.PREF_H);
   }
   public Schedule getMainSched() {
      return mainSched;
   }
   public void setMainSched(Schedule mainSched) {
      if (debugSave) System.out.println("MAINSCHED CHANGED HERE");
      this.mainSched = mainSched;
   }
   public Schedule getTodaySched() {
      return todaySched;
   }
   public Rotation getTodayR() {
      return todayR;
   }
   public void setTodayR(Rotation todayR) {
      if (Agenda.statusU) System.out.println("-----------------NEW ROTATION-----------------");
      if (updating)
         return;
      if (debug) System.out.println("DISPLAY SETTING ROTATION TO "+ todayR);
      currentClassPane.collapseNotif();
      todaySched.setData(OrderUtility.reorderAndClone(todayR, mainSched, mainSched.getClasses()));
      this.todayR = todayR;
      todaySched.setLunchLab(todayR);
      toolbar.setRotation(todayR);
      toolbar.repaint();
      toolbar.setHighlights();
      pushTodaySchedule();
      
      update();
   }
   public PanelManager getParentManager() {
      return parentManager;
   }
   public void setParentManager(PanelManager parentManager) {
      this.parentManager = parentManager;
   }
   public boolean isUpdating() {
      return updating;
   }
   public void setUpdating(boolean updating) {
      this.updating = updating;
   }
   public CalReader getReader() {
      return cal;
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      update();
   }

   @Override
   public void refresh() {
      hardStop();
      reinitialize();
      hardResume();
      currentClassPane.getList().autoSetSelection();
      revalidate();
      if (debug)
         for (java.awt.Component c : getComponents())
            System.out.println(c);
   }
   
   public void hardRefresh() {
      initCalReader();
      refresh();
   }

   @Override
   public void open() {
      reinitialize();
      resume();
   }

   @Override
   public void close() {
      stop();
   }
   
   @Override
   public void save() {
      infoSelector.getMemo().save();
      writeMain();
   }

}