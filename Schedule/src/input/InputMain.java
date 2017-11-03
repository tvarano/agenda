package input;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import constants.ErrorID;
import constants.Lab;
import constants.Rotation;
import constants.RotationConstants;
import information.ClassPeriod;
import information.Schedule;
import ioFunctions.SchedWriter;
import managers.PanelManager;
import managers.UIHandler;
import tools.ToolBar;

//Thomas Varano
//Aug 31, 2017

public class InputMain extends JPanel
{
   private static final long serialVersionUID = 1L;
   public static final int INIT_AMT_CL = 7;
   private ArrayList<Lab> labs;
   private ArrayList<ClassInputSlot> slots;
   private PanelManager parentManager;
   private boolean hasZeroPeriod, hasManager, error, debug;
   private int amtClasses;
   private ClassInputSlot pascack;
   private Schedule beginningSchedule;
   
   public InputMain(PanelManager parentManager) {
      debug = false;
      labs = new ArrayList<Lab>();
      slots = new ArrayList<ClassInputSlot>();
      setBackground(UIHandler.toolBarColor);
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      this.parentManager = parentManager;
      hasManager = (parentManager != null);
      init(INIT_AMT_CL);
      requestFocus();
   }
   
   public void init(int amtSlots) {
      if (debug) System.out.println("INPUTFRAME construted empty");
      add(new ToolBar(true, this));
      initSlots(amtSlots);
      amtClasses = amtSlots;
      addPascack(null);
      add(createBottomPanel());
   }
   
   public void init(Schedule s) {
      if (debug) System.out.println("INPUTFRAME constructed with classes");
      add(new ToolBar(true, this));
      amtClasses = s.getClasses().length;
      if (s.getLabs() != null && s.getLabs().length != 0)
         initSlots(s.getClasses(), s.getLabs());
      else
         initSlots(s.getClasses());
      addPascack(s.getPascackPreferences());
      add(createBottomPanel());
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
      ClassInputSlot s = new ClassInputSlot(slotIndex, this);
      int addIndex = (hasZeroPeriod) ? slotIndex + 1 : slotIndex;
      slots.add(addIndex - 1, s);
      addImpl(s, null, addIndex);
   }
   
   private ClassInputSlot addSlot(ClassPeriod c) {
      if (debug) System.out.println("INPUT added "+c.getInfo());
      slots.add(new ClassInputSlot(c, this));
      return (ClassInputSlot) add(slots.get(slots.size()-1));
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
      p.setLayout(new GridLayout(1,2));
      Cursor hand = new Cursor(Cursor.HAND_CURSOR);
      JButton button = new JButton("Cancel");
      button.setActionCommand("cancel");
      button.setCursor(hand);
      button.setToolTipText("Exit Without Saving");
      button.addActionListener(changeView());
      p.add(button);
      
      button = new JButton("Submit");
      button.setToolTipText("Save Your Schedule");
      button.setSelected(true);
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
         for (ClassInputSlot c : slots) {
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
      ((JButton) ((ToolBar) getComponent(0))
            .getComponent(indexInBar)).setEnabled(enabled);
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
      ErrorID.showRecoverableError(ErrorID.INPUT_ERROR);
   }
   
   private void resolve() {
      error = false;
   }

   public void reWriteSlotsArray() {
      slots.removeAll(slots);
      Component[] c = getComponents();
      
      for (int i = 0; i < c.length; i++) {
         if (c[i] instanceof ClassInputSlot) {
            slots.add((ClassInputSlot) c[i]);
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
      for (Component c : getComponents())
         if (c instanceof ClassInputSlot) 
            if (!((ClassInputSlot) c).checkCanCreate())
               return false;
      return true;
   }
   private void save() {
      if (debug) System.out.println("SAVING SCHED");
      SchedWriter writer = new SchedWriter();
      Component[] c = getComponents();
      ClassPeriod[] classes = new ClassPeriod[amtClasses+1];
      if (!canCreate()) {
         cannotCreate();
         return;
      } else {
         resolve();
      }
      
      int classIndex = 0;
      for (int i = 0; i < c.length; i++) {
         if (c[i] instanceof ClassInputSlot && !c[i].equals(pascack)) {
            classes[classIndex] = ((ClassInputSlot) c[i]).createClass();
            
            if (classes[classIndex].getSlot() == 4) {
               if (debug)
                  System.out.println("filling lunch");
               classIndex++;
               classes[classIndex] = Rotation.R1.get("Lunch");
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
      if (debug) System.out.println(s.getClasses()[0]);
      writer.write(s);
      if (debug) System.out.println("wrote" + s);
   }
   
   public void close() {
      if (hasManager)
         parentManager.finishInputting();
      else 
         ((JFrame)getParent().getParent().getParent().getParent()).dispose();
   }
   
   public void saveAndClose() {
      save();
      if (debug) System.out.println("saved. error = "+error);
      if (!error)
         close();
   }
   
   public void removeAndReOrder(Component c) {
      remove(c);
      slots.remove(c);
      for (Component a : getComponents())
         a.repaint();
      setSize(getSize());
      revalidate();
   }
   
   public Schedule getBeginningSchedule() {
      return beginningSchedule;
   }
   public void setBeginningSchedule(Schedule s) {
      this.beginningSchedule = s;
      removeAll();
      init(s);
   }
   
   private static void createAndShowGUI() {
      JFrame frame = new JFrame("INFO SLOT TEST");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      InputMain s = new InputMain(null);
      s.setBeginningSchedule(new Schedule(Rotation.R1.getTimes(), Lab.LAB1));
      frame.getContentPane().add(s);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);   
   }
   public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            createAndShowGUI();
         }
      });
   }
}
