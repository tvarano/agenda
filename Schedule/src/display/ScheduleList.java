package display;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import information.ClassPeriod;
import information.Schedule;
import managers.UIHandler;

//Thomas Varano
//[Program Descripion]
//Sep 9, 2017

public class ScheduleList extends JList<ClassPeriod> implements ListSelectionListener
{
   private static final long serialVersionUID = 1L;
   private JPanel parentPane;
   private boolean thickConstraints, selectable, showNames;
   private boolean debug = false, debugNames = false;
   
   private Schedule schedule;
   public ScheduleList(Schedule schedule, boolean showNames) {
      super();
      this.schedule = schedule.clone();
      setName(schedule.getName() + " list"); setShowNames(showNames); setSelectable(true);
      setBackground(UIHandler.background);
      this.setModel(new DefaultListModel<ClassPeriod>());
      this.addListSelectionListener(this);
      if (debug) System.out.println(getName()+ " SHOWNAMES="+showNames);
      cloneAndSetClasses();
      if (debug) System.out.println(getName()+"size"+getModel().getSize());
   }
   
   private void cloneAndSetClasses() {
      if (debugNames) System.out.println(schedule.getName()+" cloned for "+getName());
      schedule.setShowName(showNames);
      createList();
   }
   
   private void createList() {
      ((DefaultListModel<ClassPeriod>) getModel()).removeAllElements();
      for (ClassPeriod c : schedule.getClasses()) {
         if (debug) System.out.println(getName()+" added "+c);
         ((DefaultListModel<ClassPeriod>) getModel()).addElement(c);
      }
      if (debug) System.out.println(getName()+" CREATED size:" +getModel().getSize());
   }
   
   public void setSelectedValue(int slot, boolean scroll) {
      setSelectedValue(schedule.get(slot), scroll);
   }

   public void autoSetSelection() {
      if (parentPane instanceof SouthernCurrentClassPane) {
         SouthernCurrentClassPane parent = (SouthernCurrentClassPane) parentPane;
         if (parent.checkInSchool()) {
            if (parent.getClassPeriod() == null)
               setSelectedValue(schedule.get(parent.findNextClass().getSlot()), true);
            else
               setSelectedValue(parent.getCurrentSlot(), true);
            if (debug) System.out.println(getName()+"value set to"+parent.getClassPeriod());
         }
         else {
            if (debug) System.out.println(getName()+": out of school");
            setSelectedValue(schedule.getClasses()[0], true);
         }
         if (debug) System.out.println("move class back to "+((SouthernCurrentClassPane) parentPane).getClassPeriod());
      }
      else {
         if (debug) System.out.println(getName()+": out of school");
         setSelectedValue(null, false);
      }
   }
   
   public Schedule getSchedule() {
      return schedule;
   }
   public void setSchedule(Schedule schedule) {
      this.schedule = schedule.clone();
      cloneAndSetClasses();
   }
   public JPanel getParentPane() {
      return parentPane;
   }
   public void setParentPane(JPanel parent) {
      this.parentPane = parent;
   }
   public boolean isThickConstraints() {
      return thickConstraints;
   }
   public void setThickConstraints(boolean thickConstraints) {
      this.thickConstraints = thickConstraints;
   }
   public boolean isSelectable() {
      return selectable;
   }
   public void setSelectable(boolean selectable) {
      this.selectable = selectable;
   }
   public boolean isShowNames() {
      return showNames;
   }
   public void setShowNames(boolean showNames) {
      this.showNames = showNames;
   }

   @Override
   public void valueChanged(ListSelectionEvent e) {
      if (debug) {
         if (getSelectedValue()!=null)
            System.out.println("SELECTION:"+getSelectedIndex() + ":"+ ((ClassPeriod)getSelectedValue()).getInfo());
         else
            System.out.println("SELECTION:"+getSelectedIndex() + ":null");
      }
      if (selectable) {
         if (parentPane != null)
            if (parentPane instanceof ScheduleInfoSelector)
               ((ScheduleInfoSelector) parentPane).updatePeriod();
      }
      else {
         autoSetSelection();
      }
   }  
}
