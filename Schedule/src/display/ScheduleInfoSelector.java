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
import constants.Rotation;
import constants.RotationConstants;
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
   private static final int PREF_W = 228;
   private ScheduleList todayList, mainList, todayNameless;
   private ClassInfoPane info;
   private boolean debug;
   private JTabbedPane scheduleTabs;
   private DisplayMain parentPane;
   
   public ScheduleInfoSelector(Schedule todaySched, Schedule mainSched, DisplayMain parent) {
      debug = false;
      setBackground(UIHandler.background);
      Rotation todayR = parent.getTodayR();
      
      final Dimension minS = new Dimension(PREF_W, parent.getHeight());
      setMinimumSize(minS);
      if (debug) System.out.println("CLASSES\n"+todaySched.classString(true));
      if (debug) System.out.println("TODAYR = "+todayR);
      setParentPane(parent);
      todayList = new ScheduleList(todaySched, true); todayList.setParentPane(this); todayList.setName("Today's Classes");
      todayList.setAutoscrolls(true);
      mainList = new ScheduleList(mainSched, true);
      mainList.setParentPane(this); mainList.setName("Default Rotation");
      mainList.setAutoscrolls(true);
      todayNameless = new ScheduleList(RotationConstants.getNamelessRotation(todaySched, todayR), false);
      todayNameless.setParentPane(this); todayNameless.setName("Today's Rotation");
      todayNameless.setAutoscrolls(true);
      setTodaySched(todaySched); setMainSched(mainSched);
      if (debug) System.out.println("AFTER "+todaySched.classString(true));
      
      setLayout(new GridLayout(2,1));
      scheduleTabs = createTabbedPane();
      scheduleTabs.setOpaque(false);
      if (Agenda.statusU) Agenda.log("before classInfo select 47");
      info = new ClassInfoPane(todaySched.getClasses()[0]);
      add(scheduleTabs);
      scheduleTabs.setBorder(UIHandler.getTitledBorder("Select Class For Info", TitledBorder.LEADING, TitledBorder.TOP));
      if (Agenda.statusU) Agenda.log("after scroll select 52");
      JScrollPane infoScroll = new JScrollPane(info);
      if (Agenda.statusU) Agenda.log("after scroll select 52");
      //TODO next heavy method
      infoScroll.setBorder(UIHandler.getTitledBorder("Class Not Chosen"));
      if (Agenda.statusU) Agenda.log("after add select 55");
      infoScroll.setOpaque(false);
      add(infoScroll);
      setName("eastPane");
      if (debug) System.out.println(getName()+" initialized");
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
      info.setShowNames(selected.isShowNames());
      info.setClassPeriod(selectVal);
      if (selectVal == null)
         parentPane.setMemoClass(-1);
      else
         parentPane.setMemoClass(selected.getSelectedValue().getSlot());

      String infoTitle = (info.getClassPeriod() == null) ? "Select Class For Info"
            : info.getClassPeriod().getTrimmedName() + " Info";
      ((JComponent) info.getParent().getParent()).setBorder(UIHandler.getTitledBorder(infoTitle));
      parentPane.revalidate();
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
      todayList.setSchedule(todaySched);
      todayNameless.setSchedule(RotationConstants.getNamelessRotation(todaySched, parentPane.getTodayR()));
   }
   public Schedule getMainSched() {
      return mainSched;
   }
   public void setMainSched(Schedule mainSched) {
      this.mainSched = mainSched;
      mainList.setSchedule(mainSched);
   }

   public DisplayMain getParentPane() {
      return parentPane;
   }

   public void setParentPane(DisplayMain parentPane) {
      this.parentPane = parentPane;
   }

}
