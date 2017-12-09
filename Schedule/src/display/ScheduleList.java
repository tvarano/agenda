package display;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import constants.ErrorID;
import information.ClassPeriod;
import information.Schedule;
import managers.Agenda;
import managers.UIHandler;

//Thomas Varano
//[Program Descripion]
//Sep 9, 2017

public class ScheduleList extends JList<ClassPeriod> implements ListSelectionListener
{
   private static final long serialVersionUID = 1L;
   private JPanel parentPane;
   private boolean selectable;
   private boolean thickConstraints, showNames;
   private boolean debug = false, debugNames = false;
   
   private Schedule schedule;
   public ScheduleList(Schedule schedule, boolean showNames) {
      super();
      selectable = true;
      setName(schedule.getName() + " list"); setShowNames(showNames);
      setBackground(UIHandler.secondary);
      setModel(new DefaultListModel<ClassPeriod>());
      setSchedule(schedule);
      setFont(UIHandler.font.deriveFont(getFont().getSize()));
      addListSelectionListener(this);
      if (debugNames) System.out.println(getName()+ " SHOWNAMES="+showNames);
      createList();
      if (debug) System.out.println(getName()+"size"+getModel().getSize());
   }
   
   private void createList() {
      try {
      ((DefaultListModel<ClassPeriod>) getModel()).removeAllElements();
      } catch (ClassCastException e) {
         ErrorID.showError(e, false);
      }
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
      if (parentPane instanceof CurrentClassPane) {
         CurrentClassPane parent = (CurrentClassPane) parentPane;
         if (parent.checkInSchool()) {
            if (parent.getClassPeriod() == null)
               setSelectedValue(schedule.get(parent.findNextClass().getSlot()), true);
            else
               setSelectedValue(parent.getCurrentSlot(), true);
            if (debug) System.out.println(getName()+"value set to"+parent.getClassPeriod());
         }
         else {
            if (debug) System.out.println(getName()+": out of school");
            clearSelection();
         }
         if (debug) System.out.println("move class back to "+((SecondaryCurrentClassPane) parentPane).getClassPeriod());
      }
      else {
         if (debug) System.out.println(getName()+"error in scheduleListAutoSelect");
         if (Agenda.statusU) Agenda.log("Error in autoSetSelection");
         clearSelection();
      }
   }
   
   public Schedule getSchedule() {
      return schedule;
   }
   public void setSchedule(Schedule schedule) {
      if (debugNames) System.out.println(getName() + "THE NEW SCHEDULE HERE IS "+schedule.getName());
      this.schedule = schedule.clone();
      createList();
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
   public boolean isShowNames() {
      return showNames;
   }
   public void setShowNames(boolean showNames) {
      this.showNames = showNames;
   }
   public boolean getSelectable() {
      return selectable;
   }
   public void setSelectable(boolean selectable) {
      this.selectable = selectable;
      setFocusable(selectable);
   }
   
   public String toString() {
      return getClass().getName() + "[" + getName() + " size = " + getSize() + "]";
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
