//Thomas Varano
//[Program Descripion]
//Dec 18, 2017

package input;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import information.ClassPeriod;
import information.Schedule;

public class GPAInputSlot extends JPanel
{
   private static final long serialVersionUID = 1L;
   private ClassPeriod cp;
   private boolean useNumbers, hasLab, honors;

   public GPAInputSlot(ClassPeriod in) {
      this.cp = in;
      init();
   }

   public GPAInputSlot(int slot, Schedule sched) {
      cp = sched.get(slot);
      init();
   }

   public void init() {
      removeAll();
      ((FlowLayout) getLayout()).setAlignment(FlowLayout.LEFT);
      add(new JLabel(cp.getName() + " : "));
      add(new JLabel("Grade: "));
      if (useNumbers) {
         JTextField field = (JTextField) add(new JTextField(cp.getGrade() + ""));
         field.setPreferredSize(new Dimension(50, 25));
      } else {
         JComboBox<String> chooser = new JComboBox<String>();
         DefaultComboBoxModel<String> m = new DefaultComboBoxModel<String>();
         chooser.setModel(m);
         for (String gr : GPAInput.letterGrades)
            m.addElement(gr);
         m.setSelectedItem(cp.getLetterGrade());
         add(chooser);
      }
      add(new JLabel("Honors or AP: "));
      JCheckBox hon = new JCheckBox();
      hon.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            honors = !honors;
            System.out.println("CLICKED. hon = " + honors);
         }
      });
      add(hon);
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
      return gradePoint;
   }

   public double getCredits(double gradePoint, double outOf) {
      return gradePoint * getWeight(outOf);
   }
   
   public double getWeight(double outOf) {
      double weight = outOf;
      if (outOf != 4) {
         if (cp.getCourseLength() == ClassPeriod.HALF_YEAR)
            weight/=2;
         if (hasLab)
            weight++;
      }
      return weight;
   }
   
   public boolean canCreate() {
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
   
   @SuppressWarnings("unchecked")
   public void save() {
      if (useNumbers)
         cp.setGrade(Double.parseDouble(((JTextField) getComponents()[2]).getText()));
      else
         cp.setGrade(((JComboBox<String>) getComponents()[2]).getSelectedItem() + "");
   }
   
   public double finish(double outOf) {
      save();
      return getCredits(getGradePoint(), outOf);
   }
}
