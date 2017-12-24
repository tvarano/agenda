//Thomas Varano
//[Program Descripion]
//Dec 18, 2017

package input;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import constants.ErrorID;
import information.ClassPeriod;
import information.Schedule;
import ioFunctions.SchedReader;
import ioFunctions.SchedWriter;
import managers.Agenda;
import managers.PanelManager;
import managers.UIHandler;
import tools.ToolBar;

public class GPAInput extends JPanel implements InputManager
{
   private static final long serialVersionUID = 1L;
   private Schedule sched;
   private JPanel center;
   private int nameLength;
   private boolean hasZero;
   private ArrayList<GPAInputSlot> slots;
   private PanelManager manager;
   private JLabel dispLabel;
   public static final String[] letterGrades = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "F"}; 
   public static final double[] gradePoints = {4.33, 4, 3.67, 3.33, 3, 2.67, 2.33, 2, 1.67}; 
   private boolean debug;
   
   public GPAInput(Schedule sched, PanelManager manager) {
      super();
      this.manager = manager;
      this.sched = sched;
      setFont(UIHandler.font);
      if (sched != null)
         init(sched);
      else 
         init(0);
   }
   
   public GPAInput(int amtClasses, PanelManager manager) {
      this.manager = manager;
      setFont(UIHandler.font);
      init(amtClasses);
   }
   
   public GPAInput(PanelManager manager) {
      this(0, manager);
   }
   
   private void init0() {
      removeAll();
      debug = true;
      center = new JPanel();
      center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
      setLayout(new BorderLayout());
      slots = new ArrayList<GPAInputSlot>();
   }
   
   private void initComponents() {
      ToolBar bar = new ToolBar(PanelManager.GPA, this);
      add(bar, BorderLayout.NORTH);
      add(center, BorderLayout.CENTER);
      add(averageDisplay(), BorderLayout.EAST);
      add(createBottomPanel(), BorderLayout.SOUTH);
   }
   
   private void init(Schedule s) {
      init0();
      initComponents();
      initAndAddSlots(s);
   }
   
   private void init(int amtClasses) {
      init0();
      initComponents();
      initAndAddSlots(amtClasses);
   }
   
   private void initAndAddSlots(Schedule sched) {
      //first check length
      hasZero = sched.hasZeroPeriod();
      for (ClassPeriod c : sched.getGpaClasses()) {
         if (c.nameWidth(getFont()) > nameLength)
            nameLength = c.nameWidth(getFont());
      }
      for (int i = 0; i < sched.getGpaClasses().size(); i++) {
         addSlot(sched.getGpaClasses().get(i), sched.hasZeroPeriod());
      }
   }
   
   private void initAndAddSlots(int amtClasses) {
      hasZero = false;
      for (int i = 1; i <= amtClasses; i++) 
         addSlot(new ClassPeriod(i), hasZero);
   }
   
   private void calculateNames() {
      for (GPAInputSlot is : slots) {
         is.setNameLength(nameLength);
      }
   }
   
   @Override
   public void addClass(int index) {
      addClass(new ClassPeriod(index));
   }

   @Override
   public void addClass(ClassPeriod c) {
      if (c.nameWidth(getFont()) > nameLength) {
         System.out.println("width changed "+c);
         nameLength = c.nameWidth(getFont());
         calculateNames();
      }
      addSlot(c, hasZero);
   }
   
   private void addSlot(ClassPeriod c, boolean hasZero) {
      GPAInputSlot add = new GPAInputSlot(c, nameLength, this);
      int addIndex = (hasZero) ? c.getSlot() : c.getSlot()-1;
      if (addIndex > center.getComponentCount() || addIndex > slots.size())
         addIndex = -1;
      center.add(add, addIndex);
      slots.add(add);
   }
   
   private JPanel averageDisplay() {
      JPanel p = new JPanel();
      p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
      p.setBorder(UIHandler.getTitledBorder("GPA"));
      int gap = 10;
      JLabel l = new JLabel("Your 5.0 GPA");
      l.setBorder(BorderFactory.createEmptyBorder(gap,gap,gap,gap));
      p.add(l);
      dispLabel = new JLabel("0.0");
      dispLabel.setBorder(BorderFactory.createEmptyBorder(gap,gap,gap,gap));
      dispLabel.setFont(UIHandler.getInputLabelFont());
      l.setBorder(BorderFactory.createEmptyBorder(gap,gap,gap,gap));
      p.add(dispLabel);
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
            refresh();
         }
      };
   }
   
   public void refresh() {
      save();
      double gpa = calculateGPA(5);
      if (gpa == -1)
         return;
      dispLabel.setText(gpa + "");
      ((BorderLayout) getLayout()).getLayoutComponent(this, BorderLayout.EAST).revalidate();
   }
   
   public ActionListener changeView(int type) {
//      TODO not correct action listener
      if (manager != null)
         return manager.changeView(type);
      
       return new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            if (debug) System.out.println("CHANGED TO "+ type);
         }  
      };
   }
   
   public void save() {
      for (GPAInputSlot s : slots) {
         if (!s.canCreate())
            ErrorID.showUserError(ErrorID.INPUT_ERROR);
         s.save();
      }
      new SchedWriter().write(sched);
      if (Agenda.statusU) Agenda.log("schedule successfully saved");
   }
   
   public void closeToDisp() {
      save();
      
   }

   public double calculateGPA(double outOf) {
      double sumWeights = 0;
      double sumCredits = 0;
      for (GPAInputSlot g : slots) {
         if (!g.canCreate()) {
            ErrorID.showUserError(ErrorID.INPUT_ERROR);
            return -1;
         }
         sumWeights += g.getWeight(outOf);
         sumCredits += g.finish(outOf);
      }
      return (int) (sumCredits / sumWeights * 10_000) / 10_000.0;
   }
   
   public void setMethod(boolean numbers) {
      for (GPAInputSlot in : slots) {
         if (numbers == false)
            in.save();
         in.setUseNumbers(numbers);
      }
      revalidate();
   }
   
   public void setSchedule(Schedule s) {
      this.sched = s;
      init(s);
   }
   
   public static void main(String[] args) {
      Agenda.initialFileWork();
      UIHandler.init();
      JFrame f = new JFrame(Agenda.APP_NAME + " " + Agenda.BUILD + " GPA TEST");
      final long start = System.currentTimeMillis();
      System.out.println("started");
      f.getContentPane().add(new GPAInput(new SchedReader().readSched(), null));
      System.out.println(System.currentTimeMillis() - start);
      f.setVisible(true);
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setSize(new Dimension(Agenda.PREF_W, Agenda.PREF_H + 22));
      
   }
}
