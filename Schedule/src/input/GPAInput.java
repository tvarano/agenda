//Thomas Varano
//[Program Descripion]
//Dec 18, 2017

package input;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
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
import managers.Agenda;
import managers.PanelManager;
import managers.UIHandler;
import tools.ToolBar;

public class GPAInput extends JPanel
{
   private static final long serialVersionUID = 1L;
   private Schedule sched;
   private JPanel center;
   private ArrayList<GPAInputSlot> slots;
   private PanelManager manager;
   private JLabel dispLabel;
   public static final String[] letterGrades = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "F"}; 
   public static final double[] gradePoints = {4.33, 4, 3.67, 3.33, 3, 2.67, 2.33, 2, 1.67}; 
   private boolean debug;
   
   public GPAInput(Schedule sched, PanelManager manager) {
      this.manager = manager;
      if (sched != null)
         init(sched);
      else 
         init(0);
      this.sched = sched;
   }
   
   public GPAInput(int amtClasses, PanelManager manager) {
      this.manager = manager;
      init(amtClasses);
   }
   
   private void init0() {
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
      for (int i = 0; i < sched.getClasses().length; i++)
         addSlot(sched.getClasses()[i]);
   }
   
   private void initAndAddSlots(int amtClasses) {
      for (int i = 1; i <= amtClasses; i++) 
         addSlot(new ClassPeriod(i));
   }
   
   private void addSlot(ClassPeriod c) {
      GPAInputSlot add = new GPAInputSlot(c);
      center.add(add);
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
      dispLabel = new JLabel("");
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
      JButton button = new JButton("Cancel");
      button.setFont(UIHandler.getButtonFont());
      button.setCursor(hand);
      button.setToolTipText("Exit Without Saving");
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
      double gpa = calculateGPA(5);
      if (gpa == -1)
         return;
      dispLabel.setText(gpa + "");
   }
   
   public ActionListener changeView(int type) {
      //TODO not correct action listener
//      return manager.changeView(type);
      return new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            if (debug) System.out.println("CHANGED TO "+ type);
         }  
      };
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
   
   public static void main(String[] args) {
      UIHandler.init();
      JFrame f = new JFrame(Agenda.APP_NAME + " " + Agenda.BUILD + " GPA TEST");
      f.getContentPane().add(new GPAInput(7, null));
      f.setVisible(true);
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setSize(new Dimension(Agenda.PREF_W, Agenda.PREF_H + 22));
      
   }
}
