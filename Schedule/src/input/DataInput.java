package input;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import constants.ErrorID;
import constants.Lab;
import constants.Rotation;
import constants.RotationConstants;
import information.ClassPeriod;
import information.Schedule;
import ioFunctions.SchedWriter;
import managers.Agenda;
import managers.PanelManager;
import managers.UIHandler;
import tools.ToolBar;

//Thomas Varano
//Aug 31, 2017

public class DataInput extends JPanel
{
   private static final long serialVersionUID = 1L;
   public static final int INIT_AMT_CL = 7;
   private ArrayList<Lab> labs;
   private ArrayList<DataInputSlot> slots;
   private JPanel center;
   private ClassPeriod lunch;
   private PanelManager parentManager;
   private boolean hasZeroPeriod, hasManager, error, debug, saved;
   private int amtClasses;
   private DataInputSlot pascack;
   private Schedule beginningSchedule;
   
   public DataInput(PanelManager parentManager) {
      debug = false;
      center = new JPanel();
      center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
      labs = new ArrayList<Lab>();
      slots = new ArrayList<DataInputSlot>();
      setBackground(UIHandler.tertiary);
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      setLayout(new BorderLayout());
      this.parentManager = parentManager;
      hasManager = (parentManager != null);
      init(INIT_AMT_CL);
      requestFocus();
   }
   
   public void init(int amtSlots) {
      removeAll();
      if (debug) System.out.println("INPUTFRAME construted empty");
      initSlots(amtSlots);
      amtClasses = amtSlots;
      addPascack(null);
      init0();
   }
   
   public void init(Schedule s) {
      removeAll();
      if (Agenda.statusU) Agenda.log("inputMain initialized");
      if (debug) System.out.println("INPUTFRAME constructed with classes");
      amtClasses = s.getClasses().length;
      if (s.getLabs() != null && s.getLabs().length != 0)
         initSlots(s.getClasses(), s.getLabs());
      else
         initSlots(s.getClasses());
      setLunch(s.get(RotationConstants.LUNCH));
      addPascack(s.getPascackPreferences());
      init0();
   }
   
   private void init0() {
      add(new ToolBar(PanelManager.INPUT, this), BorderLayout.NORTH);
      add(center, BorderLayout.CENTER);
      add(createBottomPanel(), BorderLayout.SOUTH);
      
   }
   
   public void addLab(int slot) {
      if (debug) System.out.println("input adding lab " + slot);
      labs.add(Lab.toLabFromClassSlot(slot));
   }
   
   public void removeLab(int slot) {
      if (debug) System.out.println("input removing lab "+slot);
      labs.remove(Lab.toLabFromClassSlot(slot));
   }
   
   private void addSlot(int slotIndex) {
      DataInputSlot s = new DataInputSlot(slotIndex, this);
      int addIndex = (hasZeroPeriod) ? slotIndex : slotIndex-1;
      slots.add(addIndex, s);
      center.add(s, addIndex);
   }
   
   private DataInputSlot addSlot(ClassPeriod c) {
      if (debug) System.out.println("INPUT added "+c.getInfo());
      slots.add(new DataInputSlot(c, this));
      return (DataInputSlot) center.add(slots.get(slots.size()-1));
   }
   
   private void addPascack(ClassPeriod pref) {
      if (pref == null) {
         ClassPeriod anchor = new ClassPeriod(RotationConstants.PASCACK);
         pascack = addSlot(anchor);
      }
      else {
         pref.setSlot(RotationConstants.PASCACK);
         pascack = addSlot(pref);
      }
      pascack.setSlotNumber(RotationConstants.PASCACK);
      pascack.getPromptFields()[0].setText("Pascack Pd");
      pascack.getPromptFields()[0].setEditable(false);
      pascack.setLabFriendly(false);
   }
   
   private JPanel createBottomPanel() {
      JPanel p = new JPanel();
      p.setBackground(UIHandler.secondary);
      p.setLayout(new GridLayout(1,2));
      Cursor hand = new Cursor(Cursor.HAND_CURSOR);
      JButton button = new JButton("Cancel");
      button.setActionCommand("cancel");
      button.setFont(UIHandler.getButtonFont());
      button.setCursor(hand);
      button.setToolTipText("Exit Without Saving");
      button.addActionListener(changeView());
      p.add(button);
      
      button = new JButton("Submit");
      button.setToolTipText("Save Your Schedule");
      button.setSelected(true);
      button.setFont(UIHandler.getButtonFont());
      button.setCursor(hand);
      button.setActionCommand("submit");
      button.addActionListener(saveAndChangeView());
      p.add(button);
      return p;
   }
   
   private void initSlots(ClassPeriod[] cp) {
      for (ClassPeriod c : cp) {
         if (c.getSlot() == 0) {
            hasZeroPeriod = true;
            setButtonEnabled(ToolBar.ZERO_BUTTON, false);
            if (debug) System.out.println("INPUT HAS ZERO PERIOD");
         }
         else if (c.getSlot() == 8) {
            setButtonEnabled(ToolBar.EIGHT_BUTTON, false);
         }
         if (c.getSlot() != RotationConstants.LUNCH)
            addSlot(c);
         else
            amtClasses--;
      }
   }
   
   private void initSlots(ClassPeriod[] cp, Lab[] labs) {
      initSlots(cp);
      for (Lab l : labs) {
         for (DataInputSlot c : slots) {
            if (l.getClassSlot() == c.getSlotNumber()) {
               c.setLab(true);
               if (debug) System.out.println("lab "+l.getClassSlot() + "set to "+c);
            }
         }
      }
   }
   
   private void initSlots(int amtSlots) {
      int i = (hasZeroPeriod) ? 0 : 1;
      for (; i <= amtSlots; i++) {
         addSlot(i);
      }
   }
   
   public void addClass(int slot) {
      if (slot == 0) {
         hasZeroPeriod = true;
         setButtonEnabled(ToolBar.ZERO_BUTTON, false);
      }
      else if (slot == 8)
         setButtonEnabled(ToolBar.EIGHT_BUTTON, false);
      addSlot(slot);
      revalidate();
      amtClasses++;     
   }
   
   public void removeClassAndReOrder(int slot, Component c) {
      removeClassInt(slot);
      removeAndReOrder(c);
   }
   
   private void setButtonEnabled(int indexInBar, boolean enabled) {
      JButton b = ((JButton) ((ToolBar) getComponent(0))
            .getComponent(indexInBar));
      b.setEnabled(enabled);
   }
   
   public void removeClassInt(int slot) {
      amtClasses--;
      if (slot == 0) {
         hasZeroPeriod = false;
         setButtonEnabled(ToolBar.ZERO_BUTTON, true);
      }
      else if (slot == 8)
         setButtonEnabled(ToolBar.EIGHT_BUTTON, true);
   }
   
   private void cannotCreate() {
      error = !canCreate();
      if (debug) System.out.println("cannotCreate");
      ErrorID.showUserError(ErrorID.INPUT_ERROR);
   }
   
   private void resolve() {
      error = false;
   }

   public void reWriteSlotsArray() {
      slots.removeAll(slots);
      Component[] c = center.getComponents();
      
      for (int i = 0; i < c.length; i++) {
         if (c[i] instanceof DataInputSlot) {
            slots.add((DataInputSlot) c[i]);
         }
      }
   }
   
   public ActionListener changeView() {
      return new ActionListener() {
         @Override
          public void actionPerformed(ActionEvent e) {
               close();
          } 
       };
   }
   
   public ActionListener saveAndChangeView() {
      return new ActionListener() {
         @Override
          public void actionPerformed(ActionEvent e) {
               saveAndClose();
          } 
       };
   }
   
   private boolean canCreate() {
      for (Component c : center.getComponents())
         if (c instanceof DataInputSlot) 
            if (!((DataInputSlot) c).checkCanCreate())
               return false;
      return true;
   }
   private void save() {
      if (debug) System.out.println("SAVING SCHED");
      SchedWriter writer = new SchedWriter();
      Component[] c = center.getComponents();
      ClassPeriod[] classes = new ClassPeriod[amtClasses+1];
      if (!canCreate()) {
         cannotCreate();
         return;
      } else {
         resolve();
      }
      
      int classIndex = 0;
      for (int i = 0; i < c.length; i++) {
         if (c[i] instanceof DataInputSlot && !c[i].equals(pascack)) {
            classes[classIndex] = ((DataInputSlot) c[i]).createClass();
            
            if (classes[classIndex].getSlot() == 4) {
               if (debug) System.out.println("filling lunch");
               classIndex++;
               if (lunch == null) {
                  classes[classIndex] = Rotation.R1.get("Lunch");
               }
               else {
                  classes[classIndex] = lunch;
               }
            }
            classIndex++;
         }
      }
      
      // just to print
      if (debug) {
         for (int i = 0; i < classes.length; i++) 
            System.out.println("clInput " +i+":" + classes[i]);
      }
      
      // write
      Schedule s = new Schedule(classes, labs.toArray(new Lab[labs.size()]));
      s.setPascackPreferences(pascack.createClass());
      writer.write(s);
      if (debug) System.out.println("wrote" + s);
      if (Agenda.statusU) Agenda.log("saved input");
      saved = true;
   }
   
   public void close() {
      if (hasManager) {
         if (saved)
            parentManager.reinitDisp();
         else 
            parentManager.resumeDisp();
      }
      else 
         ((JFrame)getParent().getParent().getParent().getParent()).dispose();
      if (Agenda.statusU) Agenda.log("closed input");
   }
   
   public void saveAndClose() {
      save();
      if (debug) System.out.println("saved. error = "+error);
      if (!error)
         close();
   }
   
   public void removeAndReOrder(Component c) {
      center.remove(c);
      slots.remove(c);
      for (Component a : center.getComponents())
         a.repaint();
      setSize(getSize());
      revalidate();
      center.revalidate();
   }
   
   public Schedule getBeginningSchedule() {
      return beginningSchedule;
   }
   public void setBeginningSchedule(Schedule s) {
      this.beginningSchedule = s;
      center.removeAll();
      init(s);
   }

   public ClassPeriod getLunch() {
      return lunch;
   }

   public void setLunch(ClassPeriod lunch) {
      this.lunch = lunch;
   }
}
