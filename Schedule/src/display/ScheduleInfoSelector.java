package display;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import information.Schedule;
import managers.UIHandler;

//Thomas Varano
//[Program Descripion]
//Sep 11, 2017

public class ScheduleInfoSelector extends JPanel
{
   private static final long serialVersionUID = 1L;
   private Schedule todaySched, mainSched;
   private ScheduleList todayList, mainList;
   private ClassInfoPane info;
   private boolean debug;
   private JTabbedPane scheduleTabs;
   private JPanel parentPane;
   
   public ScheduleInfoSelector(Schedule todaySched, Schedule mainSched, JPanel parent) {
      debug = false;
      setBackground(UIHandler.background);
      
      if (debug) System.out.println("CLASSES\n"+todaySched.classString(true));
      setParentPane(parent);
      todayList = new ScheduleList(todaySched, false); todayList.setParentPane(this); todayList.setName("Today's Rotation");
      todayList.setAutoscrolls(true);
      mainList = new ScheduleList(mainSched, false);  mainList.setParentPane(this); mainList.setName("Default Rotation");
      mainList.setAutoscrolls(true);
      setTodaySched(todaySched); setMainSched(mainSched);
      if (debug) System.out.println("AFTER "+todaySched.classString(true));
      
      setLayout(new GridLayout(2,1));
      scheduleTabs = createTabbedPane();
      scheduleTabs.setOpaque(false);
      info = new ClassInfoPane(todaySched.getClasses()[0]);
      add(scheduleTabs);
      info.setBorder(UIHandler.getTitledBorder("Select Class For Info"));
      add(info);
      setName("eastPane");
      if (debug) System.out.println(getName()+" initialized");
   }
   
   public void updatePeriod() {
      if (debug) System.out.println(getName()+":update");
      if (scheduleTabs.getSelectedComponent() instanceof ScheduleList)
         info.setClassPeriod(((ScheduleList) scheduleTabs.getSelectedComponent()).getSelectedValue());
      
      else if (scheduleTabs.getSelectedComponent() instanceof JScrollPane)
         info.setClassPeriod(((ScheduleList) ((JScrollPane)scheduleTabs.getSelectedComponent())
               .getViewport().getView()).getSelectedValue());
      
      else
         System.err.println(getName()+" failed to cast "+scheduleTabs.getSelectedComponent());
      String infoTitle = (info.getClassPeriod() == null) ? "ERROR" : info.getClassPeriod().getTrimmedName() + " Info";
      info.setBorder(BorderFactory.createTitledBorder(infoTitle));
   }
   
   public void pushTodaySchedule(Schedule s) {
      setTodaySched(s);
   }
   
   private JTabbedPane createTabbedPane() {
      JTabbedPane retval = new JTabbedPane();
      
      JScrollPane scroll = new JScrollPane(todayList); scroll.setName(todayList.getName());
      retval.addTab(scroll.getName(), null, scroll, "Today's Order of Classes");
      retval.setMnemonicAt(0, KeyEvent.VK_1);
      
      scroll = new JScrollPane(mainList); scroll.setName(mainList.getName());
      retval.addTab(scroll.getName(), null, scroll, "Your R1 Schedule");
      retval.setMnemonicAt(1, KeyEvent.VK_2);
      
      return retval;
   }
   
   
   
   public Schedule getTodaySched() {
      return todaySched;
   }
   public void setTodaySched(Schedule todaySched) {
      this.todaySched = todaySched;
      todayList.setSchedule(todaySched);
   }
   public Schedule getMainSched() {
      return mainSched;
   }
   public void setMainSched(Schedule mainSched) {
      this.mainSched = mainSched;
      mainList.setSchedule(mainSched);
   }

   public JPanel getParentPane() {
      return parentPane;
   }

   public void setParentPane(JPanel parentPane) {
      this.parentPane = parentPane;
   }

}
