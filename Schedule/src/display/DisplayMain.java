package display;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import constants.*;
import information.*;
import ioFunctions.OrderUtility;
import ioFunctions.Reader;
import managers.PanelManager;
import managers.UIHandler;
import tools.ToolBar;

//Thomas Varano
//Aug 31, 2017

public class DisplayMain extends JPanel implements ActionListener
{
   private static final long serialVersionUID = 1L;
   private static final int PREF_W = 800, PREF_H = 600;
   private static final int MIN_W = 400, MIN_H = 175;
   private PanelManager parentManager;
   private Schedule mainSched, todaySched;
   private Rotation todayR;
   private DayOfWeek today;
   private Time currentTime;
   private CurrentClassPane westPane;
   private ToolBar toolbar;
   private ScheduleInfoSelector eastPane;
   private boolean updating;
   private boolean debug, testSituation;
   private Timer timer;
   
   public DisplayMain(PanelManager parentManager) {
      debug = false;
      testSituation = false;
      setBackground(UIHandler.tertiary);
      setParentManager(parentManager);
      initTime();
      setTimeBar();
      
      setLayout(new BorderLayout());
      initComponents();
     
      addComponents();
      update();
      requestFocus();
      setTimeBar();
      timer = new Timer(5000, this);
      timer.start();
   }
   
   private void initTime() {
      try {
         if (testSituation) {
            currentTime = new Time(10,00);
            today = DayOfWeek.WEDNESDAY;
            todayR = Rotation.getRotation(today);         
         } else {
            currentTime = new Time(LocalTime.now().getHour(), LocalTime.now().getMinute());
            today = LocalDate.now().getDayOfWeek();
            todayR = Rotation.getRotation(today);
         }
      } catch (Throwable e) { 
         e.printStackTrace();
         ErrorID.showError(e, false);
      }
   }
   
   private void initComponents() {
      Reader r = new Reader();
      mainSched = r.readSched(); mainSched.setName("mainSched");
      todaySched = r.readAndOrderSchedule(todayR); todaySched.setName("todaySched");
      if (debug) System.out.println("today lunch" + todaySched.get(RotationConstants.LUNCH).getInfo());
      todaySched.setLunchLab(todayR); 
      if (debug) System.out.println("today lunchAFTER" + todaySched.get(RotationConstants.LUNCH).getInfo());
      eastPane = new ScheduleInfoSelector(todaySched, mainSched, this);
      westPane = new CurrentClassPane(new ClassPeriod(), todaySched, this);
      toolbar = new ToolBar(false, this);
      checkAndUpdateTime();
      westPane.setClassPeriod(findCurrentClass());
   }
   
   private void addComponents() {
      add(eastPane, BorderLayout.EAST);
      add(westPane, BorderLayout.CENTER);
      add(toolbar, BorderLayout.NORTH);
   }
   
   public void stop() {
      timer.stop();
   }
   
   public void resume() {
      timer.start();
   }
   
   public void reinitialize() {
      removeAll();
      initComponents();
      addComponents();
      resume();
   }
   
   private void setTimeBar() {
      //TODO setBar extend a menuBar gonna be annoying but if you want to set the time theres a lot to do
   }
   
   public void checkAndUpdateTime() {
      if (testSituation) 
         currentTime = currentTime.plus(1);
      else 
         currentTime = new Time(LocalTime.now().getHour(), LocalTime.now().getMinute());
      if (currentTime.getHour24() == 23 && currentTime.getMinute() > 55)
            checkAndUpdateDate();
      westPane.pushCurrentTime(currentTime);
   }
   
   public void checkAndUpdateDate() {
      today = LocalDate.now().getDayOfWeek();
   }
   
   public void pushTodaySchedule() {
      westPane.pushTodaySchedule(todaySched);
      eastPane.pushTodaySchedule(todaySched);
   }
   
   public ClassPeriod findCurrentClass(){
      // searching labs
      for (Lab l : todaySched.getLabs())
         if (todayR.equals(l.getRotation())) {
            if (debug) System.out.println("today's Lab info: "+l.getTimeAtLab().getInfo());
            if (l.getTimeAtLab().contains(currentTime)) {
               if (debug) System.out.println("DISPLAY SHOULD BE PRINTING LAB");
               westPane.pushClassPeriod(l.getTimeAtLab());
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
            if (!c.equals(westPane.getClassPeriod()))
               westPane.pushClassPeriod(c);
            return c;
         }
      }
      westPane.pushClassPeriod(null);
      return null;
   }
      
   public void update() {
      setUpdating(true);
      checkAndUpdateTime();
      findCurrentClass();
      westPane.update();
      repaint();
      setUpdating(false);
   }
   
   public ActionListener changeView() {
      return parentManager.changeView(true);
   }
   
   public Dimension getMinimumSize() {
      return new Dimension(MIN_W,MIN_H);
   }
   public Dimension getPreferredSize() {
      return new Dimension(PREF_W, PREF_H);
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
      if (updating)
         return;
      if (debug) System.out.println("DISPLAY SETTING ROTATION TO "+ todayR);
      todaySched.setData(OrderUtility.reorderAndClone(todayR, mainSched, mainSched.getClasses()));
      this.todayR = todayR;
      todaySched.setLunchLab(todayR);
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
}