//Thomas Varano
//[Program Descripion]
//Dec 18, 2017

package com.varano.ui.input;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.varano.information.ClassPeriod;
import com.varano.information.Schedule;
import com.varano.ui.UIHandler;

public class GPAInputSlot extends JPanel
{
   private static final long serialVersionUID = 1L;
   private ClassPeriod cp;
   private GPAInput parentPanel;
   private int courseWeight;
   
   private boolean useNumbers, honors, debug;
   
   public GPAInputSlot(ClassPeriod in, GPAInput parentPanel) {
      super();
      this.cp = in;
      setParentPanel(parentPanel);
      setBackground(UIHandler.background);
      setFont(UIHandler.getInputLabelFont());
      init();
   }

   public GPAInputSlot(int slot, Schedule sched, GPAInput parentPanel) {
      cp = sched.get(slot);
      setParentPanel(parentPanel);
      setFont(UIHandler.getInputLabelFont());
      init();
   }
   
   public void init() {
      debug = false;
      create();
   }
   
   private ActionListener checkModification() {
      return new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            parentPanel.setSaved(false);
         }
      };
   }
   
   public void create() {
      removeAll();
      ((FlowLayout) getLayout()).setAlignment(FlowLayout.LEFT);
      JTextField nameField = new JTextField(cp.getTrimmedName());                   //displays the name
      nameField.setPreferredSize(new Dimension(ClassPeriod.DEF_STRING_WIDTH, 25));
      nameField.setEditable(false);
      nameField.setFont(UIHandler.getInputFieldFont());
      nameField.setBackground(UIHandler.background);
      nameField.setForeground(UIHandler.foreground);
      nameField.setToolTipText(cp.getName());
      add(nameField);
      JLabel l = new JLabel("Grade: ");                                             //prompts the grade of the class
      l.setForeground(UIHandler.foreground);
      l.setFont(UIHandler.getButtonFont());
      add(l);
      honors = cp.isHonors();
      if (useNumbers) {
         JTextField field = (JTextField) add(new JTextField(cp.getGrade() + ""));   //field for number grades
         field.setFont(UIHandler.getInputFieldFont());
         field.setPreferredSize(new Dimension(60, 25));
         field.addActionListener(checkModification());
      } else {
         JComboBox<String> chooser = new JComboBox<String>();                       //JCombo for letter grades
         chooser.setFont(UIHandler.getButtonFont());
         DefaultComboBoxModel<String> m = new DefaultComboBoxModel<String>();
         chooser.setModel(m);
         chooser.addActionListener(checkModification());
         for (String gr : GPAInput.letterGrades)
            m.addElement(gr);
         m.setSelectedItem(cp.getLetterGrade());
         add(chooser);
      }
      JCheckBox hon = new JCheckBox("Honors/AP");                                   //checkbox for honors / ap credit
      hon.setForeground(UIHandler.foreground);
      hon.setOpaque(false);
      hon.setFont(UIHandler.getButtonFont());
      hon.setSelected(honors);
      hon.addActionListener(checkModification());
      hon.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            honors = !honors;
            cp.setHonors(honors);
         }
      });
      add(hon);
      JComboBox<String> courseLen = new JComboBox<String>();                        //determines the weight of the course
      courseLen.setFont(UIHandler.getButtonFont());
      DefaultComboBoxModel<String> clm = new DefaultComboBoxModel<String>();
      courseLen.setModel(clm);
      clm.addElement("Course Weight");
      clm.addElement("Half Year");
      clm.addElement("Full Year");
      clm.addElement("Full w/ Lab");
      courseLen.addActionListener(checkModification());
      courseLen.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            courseWeight = courseLen.getSelectedIndex();
            cp.setCourseWeight(courseLen.getSelectedIndex());
            if (debug) System.out.println("weight " + cp.getCourseWeight());
         }
      });
      courseLen.setSelectedIndex(cp.getCourseWeight());
      add(courseLen);
      JButton removal = (JButton) add(new JButton("Remove From GPA"));
      removal.setFont(UIHandler.getButtonFont());
      removal.addActionListener(checkModification());
      removal.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            remove();
         }
      });
      JButton move = (JButton) add(new JButton("Move"));
      move.setFont(UIHandler.getButtonFont());
      move.addActionListener(checkModification());
      move.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            move();
         }
      });
   }

   public void move() {
      save();
      parentPanel.moveSlot(this);
   }
   
   public void remove() {
      save();
      parentPanel.removeSlot(this);
   }
   
   private double getGradePoint() {
      double gradePoint = 0;
      for (int i = 0; i < GPAInput.letterGrades.length; i++)
         if (cp.getLetterGrade().equals(GPAInput.letterGrades[i])) {
            gradePoint = (i < GPAInput.gradePoints.length)
                  ? GPAInput.gradePoints[i]
                  : GPAInput.gradePoints[GPAInput.gradePoints.length - 1];
         }

      if (honors)
         gradePoint++;
      if (debug) System.out.println(cp.getGrade());
      return gradePoint;
   }
   
   public double getUnweightedGradePoint() {
      for (int i = 0; i < GPAInput.letterGrades.length; i++)
         if (cp.getLetterGrade().equals(GPAInput.letterGrades[i])) {
            return (i < GPAInput.unWeightedGradePoints.length)
                  ? GPAInput.unWeightedGradePoints[i]
                  : GPAInput.unWeightedGradePoints[GPAInput.unWeightedGradePoints.length - 1];
         }
      return -1;
   }
   
   public double getCredits(double gradePoint) {
      return gradePoint * getWeight();
   }
   
   public double getWeight() {
      double weight = 5;
      if (cp.getCourseWeight() == ClassPeriod.FULL_LAB)
         weight++;
      else if (cp.getCourseWeight() == ClassPeriod.HALF_YEAR)
         weight/=2;
      return weight;
   }
   
   public boolean canCreate() {
      if (courseWeight == 0)
         return false;
      if (useNumbers) {
         try {
            double grade = Double.parseDouble(((JTextField) getComponents()[2]).getText());
            return grade <= 100 && grade >= 0; 
         } catch (Exception e) {
            return false;
         }
      }
      return true;
   }

   public void setUseNumbers(boolean useNums) {
      useNumbers = useNums;
      init();
      revalidate();
      repaint();
   }
   
   public GPAInput getParentPanel() {
      return parentPanel;
   }

   public void setParentPanel(GPAInput parentPanel) {
      this.parentPanel = parentPanel;
   }
   
   public int getSlot() {
      return cp.getSlot();
   }
   
   public boolean isHonors() {
      return honors;
   }

   @SuppressWarnings("unchecked")
   public void save() {
      if (useNumbers)
         cp.setGrade(Double.parseDouble(((JTextField) getComponents()[2]).getText()));
      else
         cp.setGrade(((JComboBox<String>) getComponents()[2]).getSelectedItem() + "");
      saveWeight();
   }
   
   public void saveWeight() {
      cp.setCourseWeight(courseWeight);      
   }
   
   public ClassPeriod getClassPeriod() {
      return cp;
   }
   
   public double finish() {
      save();
      return getCredits(getGradePoint());
   }
   
   public String toString() {
      return cp.toString();
   }
}
