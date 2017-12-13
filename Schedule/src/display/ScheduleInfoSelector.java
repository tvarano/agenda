package display;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import constants.ErrorID;
import constants.RotationConstants;
import information.ClassPeriod;
import information.Schedule;
import managers.Agenda;
import managers.UIHandler;

//Thomas Varano
//[Program Descripion]
//Sep 11, 2017

public class ScheduleInfoSelector extends JPanel
{
   private static final long serialVersionUID = 1L;
   private Schedule todaySched, mainSched;
   private static final int PREF_H = 240;
   private ScheduleList todayList, mainList, todayNameless;
   private MemoPad memo;
   private ClassInfoPane info;
   private boolean debug;
   private JTabbedPane scheduleTabs;
   private DisplayMain parentPane;
   
   public ScheduleInfoSelector(Schedule todaySched, Schedule mainSched, DisplayMain parent) {
      debug = false;
      setBackground(UIHandler.background);
      
      setPreferredSize(new Dimension(parent.getWidth(),PREF_H));
      if (debug) System.out.println("CLASSES\n"+todaySched.classString(true));
      setParentPane(parent);
      setTodaySched(todaySched); setMainSched(mainSched);
      initComponents();
      if (debug) System.out.println("AFTER "+todaySched.classString(true));
      
      setLayout(new GridLayout(1,3));
      addComponents();
      
      if (debug) System.out.println(getName()+" initialized");
   }
   
   private void initComponents() {
      todayList = new ScheduleList(todaySched, true); todayList.setParentPane(this); todayList.setName("Today's Classes");
      todayList.setAutoscrolls(true);
      mainList = new ScheduleList(mainSched, true);
      mainList.setParentPane(this); mainList.setName("Default Rotation");
      mainList.setAutoscrolls(true);
      todayNameless = new ScheduleList(RotationConstants.getNamelessRotation(todaySched, parentPane.getTodayR()), false);
      todayNameless.setParentPane(this); todayNameless.setName("Today's Rotation");
      todayNameless.setAutoscrolls(true);
      
      memo = new MemoPad(new ClassPeriod(), this);
      memo.setEnabled(false);
      memo.setToolTipText("Save Notes, Homework, or Reminders");
      
      scheduleTabs = createTabbedPane();
      scheduleTabs.setOpaque(false);
      if (Agenda.statusU) Agenda.log("scheduleInfo initializer mk 1");
      info = new ClassInfoPane(null);
      add(scheduleTabs);
   }
   
   private void addComponents() {
      scheduleTabs.setBorder(UIHandler.getTitledBorder("Select Class For Info", TitledBorder.LEADING, TitledBorder.TOP));
      if (Agenda.statusU) Agenda.log("scheduleInfo initializer mk 2");
      JScrollPane scroll = new JScrollPane(info);
      scroll.setBorder(UIHandler.getTitledBorder("Class Not Chosen"));
      if (Agenda.statusU) Agenda.log("scheduleInfo initializer mk 3");
      scroll.setOpaque(false);
      add(scroll);
      scroll = new JScrollPane(memo);
      scroll.setBorder(UIHandler.getTitledBorder("Select Class For Memo"));
      scroll.setToolTipText(memo.getToolTipText());
      scroll.setOpaque(false);
      add(scroll);
      setName("Info Selector");
   }
   
   public void updatePeriod() {
      if (debug) System.out.println(getName()+":update");
      ScheduleList selected = null;
      if (scheduleTabs.getSelectedComponent() instanceof ScheduleList) 
         selected = (ScheduleList) scheduleTabs.getSelectedComponent();
         
      else if (scheduleTabs.getSelectedComponent() instanceof JScrollPane)
         selected = (ScheduleList) ((JScrollPane)scheduleTabs.getSelectedComponent())
               .getViewport().getView();
      else {
         ErrorID.showError(new NullPointerException("Cast incorrect for update in ScheduleInfo"), true);
         if (debug) System.err.println(getName()+" failed to cast "+scheduleTabs.getSelectedComponent());
         return;
      }
      information.ClassPeriod selectVal = selected.getSelectedValue();
      if (Agenda.statusU) Agenda.log("class selection changed to: "+selectVal);
      info.setShowNames(selected.isShowNames());
      info.setClassPeriod(selectVal);
      if (selectVal == null)
         setMemoClass(-1);
      else
         setMemoClass(selected.getSelectedValue().getSlot());

      String infoTitle = (info.getClassPeriod() == null) ? "Select Class For Info"
            : info.getClassPeriod().getTrimmedName() + " Info";
      ((JComponent) info.getParent().getParent()).setBorder(UIHandler.getTitledBorder(infoTitle));
      parentPane.revalidate();
   }
   
   public void setMemoClass(int slot) {
      ClassPeriod c = parentPane.classForMemo(slot);
      setMemoBorderTitle(c);
      memo.setParentClass(c);
      memo.repaint();
   }
   
   public MemoPad getMemo() {
      return memo;
   }
   
   public void setMemoBorderTitle(Object o) {
      ((JScrollPane)memo.getParent().getParent()).setBorder(UIHandler.getTitledBorder(o + " Memo"));
   }
   
   public void pushTodaySchedule(Schedule s) {
      setTodaySched(s);
   }
   
   private JTabbedPane createTabbedPane() {
      JTabbedPane retval = new JTabbedPane();
      
      JScrollPane scroll = new JScrollPane(todayList); scroll.setName(todayList.getName());
      scroll.setBackground(todayList.getBackground());
      retval.addTab(scroll.getName(), null, scroll, "Today's Rotation of Classes");
      retval.setMnemonicAt(0, KeyEvent.VK_1);
      
      scroll = new JScrollPane(mainList); scroll.setName(mainList.getName());
      scroll.setBackground(mainList.getBackground());
      retval.addTab(scroll.getName(), null, scroll, "Standard R1 Schedule");
      retval.setMnemonicAt(1, KeyEvent.VK_2);
      
      scroll = new JScrollPane(todayNameless); scroll.setName(todayNameless.getName());
      scroll.setBackground(todayNameless.getBackground());
      retval.addTab(scroll.getName(), null, scroll, "Rotation for Today");
      retval.setMnemonicAt(2, KeyEvent.VK_3);
      
      ScheduleList allC = new ScheduleList(RotationConstants.getAllClasses(mainSched), false);
      allC.setParentPane(this);
      scroll = new JScrollPane(allC);
      scroll.setName("All Classes");
      scroll.setBackground(todayNameless.getBackground());
      retval.addTab(scroll.getName(), null, scroll, "All Classes");
      retval.setMnemonicAt(3, KeyEvent.VK_4);
      
      
      retval.setBackground(UIHandler.background);
      retval.setFont(UIHandler.getTabFont());
      retval.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent e) {
            updatePeriod();
         }  
      });
      return retval;
   }
   
   public Schedule getTodaySched() {
      return todaySched;
   }
   public void setTodaySched(Schedule todaySched) {
      this.todaySched = todaySched;
      if (todayList != null)
         todayList.setSchedule(todaySched);
      if (todayNameless != null)
      todayNameless.setSchedule(RotationConstants.getNamelessRotation(todaySched, parentPane.getTodayR()));
   }
   public Schedule getMainSched() {
      return mainSched;
   }
   public void setMainSched(Schedule mainSched) {
      this.mainSched = mainSched;
      if (mainList != null)
         mainList.setSchedule(mainSched);
   }

   public DisplayMain getParentPane() {
      return parentPane;
   }

   public void setParentPane(DisplayMain parentPane) {
      this.parentPane = parentPane;
   }

}
