package display;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.JPanel;
import javax.swing.Timer;

import constants.ErrorID;
import constants.Lab;
import constants.Rotation;
import constants.RotationConstants;
import information.ClassPeriod;
import information.Schedule;
import information.Time;
import ioFunctions.OrderUtility;
import ioFunctions.SchedReader;
import ioFunctions.SchedWriter;
import ioFunctions.WebReader;
import managers.Agenda;
import managers.PanelManager;
import managers.PanelView;
import managers.UIHandler;
import tools.ToolBar;

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
   private DayOfWeek today;
   private WebReader web;
   private Time currentTime;
   private CurrentClassPane currentClassPane;
   private ToolBar toolbar;
   private ScheduleInfoSelector infoSelector;
   private boolean updating, showDisp;
   private boolean debug, testSituation;
   private Timer timer;
   
   public DisplayMain(PanelManager parentManager) {
      debug = false;
      testSituation = true;
      showDisp = true;
      setBackground(UIHandler.tertiary);
      setParentManager(parentManager);
      web = new WebReader();
      initTime();
      setLayout(new BorderLayout());
      initComponents();
     
      addComponents();
      update();
      requestFocus();
      timer = new Timer(5000, this);
      timer.start();
      if (Agenda.statusU) Agenda.log("display main fully initialized");
   }
   
   public Rotation readRotation() {
      return web.readTodayRotation();
   }
   
   private void initTime() {
      try {
         if (testSituation) {
            currentTime = new Time(9,20);
            today = DayOfWeek.MONDAY;
            todayR = Rotation.getRotation(today); 
            
         } else {
            currentTime = new Time(LocalTime.now());
            today = LocalDate.now().getDayOfWeek();
            todayR = web.readTodayRotation();
         }
      } catch (Throwable e) { 
         e.printStackTrace();
         ErrorID.showError(e, false);
      }
   }
   
   private void initComponents() {
      SchedReader r = new SchedReader();
      mainSched = r.readSched(); mainSched.setName("mainSched");
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
      add(infoSelector, BorderLayout.SOUTH);
      add(currentClassPane, BorderLayout.CENTER);
   }
   
   public void hardStop() {
	   timer.stop();
   }
   
   public synchronized void writeMain() {
      if (Agenda.statusU) Agenda.log("wrote main Schedule");
      try {
         SchedWriter w = new SchedWriter();
         w.write(mainSched);
         w.close();
      } catch (Exception e) {
         ErrorID.showUserError(ErrorID.FILE_TAMPER);
         Agenda.FileHandler.initAndCreateFiles();
      }
   }
   
   public void stop() {
      save();
      showDisp = false;
   }
   
   public void resume() {
      showDisp = true;
   }
   
   public void hardResume() {
	   showDisp = true;
	   timer.start();
   }
   
   public void reinitialize() {
      removeAll();
      initComponents();
      addComponents();
      resume();
   }
   
   public void setBarText(String s) {
      parentManager.getTimeMenu().setLabel(s);
   }
  
   public void setBarTime(Time timeLeft) {
      String prefix = "Time Left In Class: "; 
      setBarText(prefix + timeLeft.durationString());
   }
   
   public void configureBarTime(ClassPeriod c) {
      if (c != null) {
         setBarTime(currentTime.getTimeUntil(c.getEndTime()));
         if (c.getSlot() == RotationConstants.NO_SCHOOL_TYPE)
            setBarText("No School");
      }
       else if (checkInSchool())
         setBarText("Next Class In: " + timeUntilNextClass().durationString());
       else 
          setBarText("Not In School");
   }
   
   public void checkAndUpdateTime() {
      if (testSituation) 
         currentTime = currentTime.plus(1);
      else 
         currentTime = new Time(LocalTime.now());
      if (currentTime.getHour24() == 0 && currentTime.getMinute() < 2)
            checkAndUpdateDate();
      checkInSchool();
      findCurrentClass();
      currentClassPane.pushCurrentTime(currentTime);
   }
   
   public void checkAndUpdateDate() {
      today = LocalDate.now().getDayOfWeek();
      web.init();
      setTodayR(web.readTodayRotation());
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
   
   public ClassPeriod classForMemo(int slot) {
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
      }
      setUpdating(false);
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
      this.mainSched = mainSched;
   }
   public Schedule getTodaySched() {
      return todaySched;
   }
   public Rotation getTodayR() {
      return todayR;
   }
   public void setTodayR(Rotation todayR) {
      if (Agenda.statusU) System.out.println("-----------------NEW ROTATION---------------------");
      if (updating)
         return;
      if (debug) System.out.println("DISPLAY SETTING ROTATION TO "+ todayR);
      todaySched.setData(OrderUtility.reorderAndClone(todayR, mainSched, mainSched.getClasses()));
      this.todayR = todayR;
      todaySched.setLunchLab(todayR);
      toolbar.setRotation(todayR);
      toolbar.repaint();
      pushTodaySchedule();
      
      update();
   }
   public DayOfWeek getToday() {
      return today;
   }
   public void setToday(DayOfWeek today) {
      this.today = today;
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

   @Override
   public void actionPerformed(ActionEvent e) {
      update();
   }
   
   protected void finalize() {
      hardStop();
   }

   @Override
   public void refresh() {
      reinitialize();
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