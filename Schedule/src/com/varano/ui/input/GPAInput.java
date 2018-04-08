//Thomas Varano
//[Program Descripion]
//Dec 18, 2017

package com.varano.ui.input;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.varano.information.ClassPeriod;
import com.varano.information.Schedule;
import com.varano.information.constants.ErrorID;
import com.varano.information.constants.Lab;
import com.varano.information.constants.RotationConstants;
import com.varano.managers.Agenda;
import com.varano.managers.PanelManager;
import com.varano.managers.UIHandler;
import com.varano.resources.ioFunctions.SchedReader;
import com.varano.ui.PanelView;
import com.varano.ui.display.selection.ScheduleList;
import com.varano.ui.tools.ToolBar;

/*
 * TODO 
 * removing classes        DONE
 * adding back from your regular schedule       DONE
 *    joption pane "add from your schedule or create new class"
 * ensuring the ease for lab switches DONE
 * question icon DONE
 * writing and reordering classes DONE
 * 
 *
 */

/**
 * Central panel for allowing the user to integrate their GPA into the program. 
 * <p>
 * <strong>NOTE this does not affect the classes in the display, just for the gpa. </strong>  
 * 
 * it uses the {@link Schedule} gpa classes arrayList to produce a separate list of classes.
 * 
 * @author Thomas Varano
 *
 */
public class GPAInput extends JPanel implements InputManager, PanelView
{
   private static final long serialVersionUID = 1L;
   private Schedule sched;
   private JPanel center;
   private boolean hasZero, saved;
   private ArrayList<GPAInputSlot> slots;
   private PanelManager manager;
   private JLabel weightLabel, unWeightLabel;
   private static final String weightedPrefix = "Weighted GPA: ";
   private static final String unWeightedPrefix = "UnWeighted GPA: ";
   public static final String[] letterGrades = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "F"}; 
   public static final double[] gradePoints = {4.33, 4, 3.67, 3.33, 3, 2.67, 2.33, 2, 1.67, 1.33, 1};
   public static final double[] unWeightedGradePoints = {4, 4, 3.67, 3.33, 3, 2.67, 2.33, 2, 1.67, 1.33, 1};
   private boolean debug, error;
   
   public GPAInput(Schedule sched, PanelManager manager) {
      super();
      this.manager = manager;
      this.sched = sched;
      setFont(UIHandler.font);
      if (sched != null)
         init(sched);
      else 
         init(0);
      Agenda.log("gpa constructed with schedule");
   }
   
   public GPAInput(int amtClasses, PanelManager manager) {
      this.manager = manager;
      setFont(UIHandler.font);
      init(amtClasses);
      Agenda.log("gpa constructed empty");
   }
   
   public GPAInput(PanelManager manager) {
      this(0, manager);
   }
   
   public Agenda getMain() {
      return manager.getMain();
   }
   
   private void init0() {
      removeAll();
      debug = false;
      center = new JPanel();
      center.setBackground(UIHandler.background);
      center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
      center.add(averageDisplay());
      setLayout(new BorderLayout());
      slots = new ArrayList<GPAInputSlot>();
      initComponents();
   }
   
   private void initComponents() {
      ToolBar bar = new ToolBar(PanelManager.GPA, this);
      add(bar, BorderLayout.NORTH);
      add(new JScrollPane(center), BorderLayout.CENTER);
      add(createBottomPanel(), BorderLayout.SOUTH);
   }
   
   private void init(Schedule s) {
      Agenda.log("gpa init with "+s.getName());
      init0();
      initAndAddSlots(s);
   }
   
   private void init(int amtClasses) {
      init0();
      initAndAddSlots(amtClasses);
   }
   
   private void initAndAddSlots(Schedule sched) {
      hasZero = sched.hasZeroPeriod();
      //ensure classes with lab are marked correctly
      for (Lab l : sched.getLabs())
         sched.get(l.getClassSlot()).setCourseWeight(ClassPeriod.FULL_LAB);
               
      for (int i = 0; i < sched.getGpaClasses().size(); i++) {
         addSlot(sched.getGpaClasses().get(i), sched.hasZeroPeriod());
      }
         
   }
   
   private void initAndAddSlots(int amtClasses) {
      hasZero = false;
      for (int i = 1; i <= amtClasses; i++) 
         addSlot(new ClassPeriod(i), hasZero);
   }
   
   @Override
   public void addClass(int index) {
      if (debug) System.out.println("class added at "+index);
      addClass(new ClassPeriod(index));
   }

   @Override
   public void addClass(ClassPeriod c) {
      if (debug) System.out.println("class added: "+c);
      if (c == null)
         return;
      sched.addGPAClass(c);
      addSlot(c, hasZero);
   }
   
   @Override
   public void addCustomClass() {
      int choice = 0;
      choice = JOptionPane.showOptionDialog(null, "Do you want to\nchoose a class from your schedule\nor create a new class?", 
            "Add a GPA Class", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, 
            new String[] {"Cancel", "Create", "Choose"} , "Cancel");
      if (choice == 0) return;
      if (choice == 1) {
         addClass(DataInputSlot.showInputSlot());
      }
      else 
         addClass(chooseClass());
   }
   
   private ClassPeriod chooseClass() {
      ArrayList<ClassPeriod> unSignedClasses = new ArrayList<ClassPeriod>();
      for (int i = 0; i < sched.getClasses().length; i++) {
         ClassPeriod c = sched.getClasses()[i];
         if (c.getSlot() != RotationConstants.LUNCH && c.getSlot() != RotationConstants.PASCACK)
            if (sched.getGpaClasses().indexOf(sched.getClasses()[i]) < 0)
               unSignedClasses.add(sched.getClasses()[i]);
      }
      if (unSignedClasses.isEmpty()) {
         JOptionPane.showMessageDialog(null, "There are no unused\n"
               + "classes in your schedule.", "Select a Class", JOptionPane.QUESTION_MESSAGE, null);
         return null;
      }
      
      ScheduleList sl = new ScheduleList(
            new Schedule(unSignedClasses.toArray(new ClassPeriod[unSignedClasses.size()])), true);
      sl.setBorder(UIHandler.getTitledBorder("Select..."));
      
      JOptionPane.showMessageDialog(null, sl, "Select a Class", JOptionPane.QUESTION_MESSAGE, null);
      return sl.getSelectedValue();
   }
   
   private void addSlot(ClassPeriod c, boolean hasZero) {
      if (debug) System.out.println("adding"+ c.getInfo());
      GPAInputSlot add = new GPAInputSlot(c, this);
      center.add(add);
      slots.add(add);
      revalidate();
   }
   
   private JPanel averageDisplay() {
      JPanel p = new JPanel();
      p.setBackground(UIHandler.background);
      ((FlowLayout) p.getLayout()).setAlignment(FlowLayout.RIGHT);
      
      final int gap = 5;
      weightLabel = new JLabel(weightedPrefix + "--");
      weightLabel.setForeground(UIHandler.foreground);
      weightLabel.setFont(UIHandler.getInputLabelFont());
      weightLabel.setBorder(BorderFactory.createEmptyBorder(gap,0,0,gap));
      p.add(weightLabel);
      unWeightLabel = new JLabel(unWeightedPrefix + "--");
      unWeightLabel.setForeground(UIHandler.foreground);
      unWeightLabel.setFont(UIHandler.getInputLabelFont());
      unWeightLabel.setBorder(BorderFactory.createEmptyBorder(gap,0,0,gap));
      p.add(unWeightLabel);
      return p;
   }
   
   private JPanel createBottomPanel() {
      JPanel p = new JPanel();
      p.setBackground(UIHandler.secondary);
      p.setLayout(new GridLayout(1,2));
      Cursor hand = new Cursor(Cursor.HAND_CURSOR);
      JButton button = new JButton("Close");
      button.setFont(UIHandler.getButtonFont());
      button.setCursor(hand);
      button.setToolTipText("Exit and Save");
      button.addActionListener(changeView(PanelManager.DISPLAY));
      p.add(button);
      
      button = new JButton("Refresh GPA");
      button.setToolTipText("See your new GPA");
      button.setSelected(true);
      button.setFont(UIHandler.getButtonFont());
      button.setCursor(hand);
      button.addActionListener(refreshListener());
      p.add(button);
      return p;
   }

   private ActionListener refreshListener() {
      return new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            refreshGPA();
         }
      };
   }
   
   public void refreshGPA() {
      save();
      double gpa = calculateWeightedGPA();
      double unw = calculateUnWeightedGPA();
      if (gpa == -1 || unw == -1)
         return;
      weightLabel.setText(weightedPrefix + gpa);
      unWeightLabel.setText(unWeightedPrefix + unw);
   }
   
   public ActionListener changeView(int type) {
      if (manager != null)
         return manager.changeViewListener(type);
      
       return new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            if (debug) System.out.println("CHANGED TO "+ type);
         }  
      };
   }
   
   public boolean checkAndSetError() {
      for (GPAInputSlot s : slots) {
         if (!s.canCreate()) {
            error = true;
            return error;
         }
      }
      error = false;
      return error;
   }
   
   public void save() {
      if (debug) System.out.println("GPA 272 gpa = " + sched.getGpaClasses().toString());
      if (checkAndSetError()) {
         ErrorID.showUserError(ErrorID.INPUT_ERROR);
         return;
      }
      for (GPAInputSlot s : slots) {
         s.save();
      }
      setSaved(true);
      if (manager != null)
         manager.saveSchedule(sched, getClass());
      Agenda.log("gpa successfully saved");
   }
   
   public void closeToDisp() {
      if (manager != null) {
         save();
         manager.setCurrentPane(PanelManager.DISPLAY);
      }
   }

   public double calculateUnWeightedGPA() {
      int sum = 0;
      for (GPAInputSlot g : slots) {
         if (!g.canCreate()) {
            if (debug) System.out.println(g);
            ErrorID.showUserError(ErrorID.INPUT_ERROR);
            return -1;
         }
         sum += g.getUnweightedGradePoint();
      }
      return (int) (sum / slots.size() * 10_000) / 10_000.0; 
   }
   
   public double calculateWeightedGPA() {
      if (error)
         return -1;
      double sumWeights = 0;
      double sumCredits = 0;
      for (GPAInputSlot g : slots) {
         if (!g.canCreate()) {
            if (debug) System.out.println(g);
            ErrorID.showUserError(ErrorID.INPUT_ERROR);
            return -1;
         }
         sumWeights += g.getWeight();
         sumCredits += g.finish();
      }
      return (int) (sumCredits / sumWeights * 10_000) / 10_000.0;
   }
   
   public void setMethod(boolean numbers) {
      for (GPAInputSlot in : slots) {
         if (numbers == false)
            in.save();
         else 
            in.saveWeight();
         in.setUseNumbers(numbers);
      }
      revalidate();
   }
   
   public void removeSlot(GPAInputSlot slot) {
      sched.removeGPAClass(slot.getClassPeriod());
      slots.remove(slot);
      center.remove(slot);
      revalidate();
   }
   
   public void moveSlot(GPAInputSlot slot) {
      int newIndex = -1;
      JList<String> spaces = new JList<String>();
      spaces.setBorder(UIHandler.getTitledBorder("Put "+slot.toString() + " after..."));
      DefaultListModel<String> dlm = new DefaultListModel<String>();
      spaces.setModel(dlm);
      dlm.addElement("<Put First>");
      for (GPAInputSlot in : slots)
         dlm.addElement(in.toString());
      if (JOptionPane.showConfirmDialog(null, spaces, "Move Slot", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null) == 1)
         return;
      newIndex = spaces.getSelectedIndex();
      if (newIndex < 0) return;
      removeSlot(slot);
      if (newIndex >= slots.size()) {
         center.add(slot);
         sched.addGPAClass(slot.getClassPeriod());
         slots.add(slot);
      } else {
         sched.addGPAClass(slot.getClassPeriod(), newIndex);
         slots.add(newIndex, slot);
         center.add(slot, newIndex+1);
      }
      revalidate();
   }
   
   public void setSchedule(Schedule s) {
      this.sched = s;
      init(s);
   }
   
   @Override
   public void open() {
      setSchedule(manager.getMainSched());
   }

   @Override
   public void close() {
      save();
   }
   
   public boolean isSaved() {
      return saved;
   }
   
   public void setSaved(boolean saved) {
      this.saved = saved;
      if (debug) System.out.println("gpa 389 SAVED = "+saved);
      repaint();
   }
   
   @Override
   public void refresh() {
      setSchedule(new SchedReader().readSched());
   }
   
   /*
   public static void main(String[] args) {
      Agenda.initialFileWork();
      UIHandler.init();
      javax.swing.JFrame f = new javax.swing.JFrame(Agenda.APP_NAME + " " + Agenda.BUILD + " GPA TEST");
      final long start = System.currentTimeMillis();
      System.out.println("NOTE gpa run seperately");
      f.getContentPane().add(new GPAInput(new SchedReader().readSched(), null));
      System.out.println(System.currentTimeMillis() - start);
      f.setVisible(true);
      f.setMinimumSize(new java.awt.Dimension(Agenda.MIN_W, Agenda.MIN_H));
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setSize(new Dimension(Agenda.PREF_W, Agenda.PREF_H + 22));
      
   }
   */
}
