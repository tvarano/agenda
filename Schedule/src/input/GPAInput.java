//Thomas Varano
//[Program Descripion]
//Dec 18, 2017

package input;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import information.ClassPeriod;
import information.Schedule;

public class GPAInput extends JPanel
{
   private static final long serialVersionUID = 1L;
   private Schedule sched;
   private JPanel center;
   private ArrayList<GPAInputSlot> slots;
   
   public GPAInput(Schedule sched) {
      center = new JPanel();
      center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
      setLayout(new BorderLayout());
      slots = new ArrayList<GPAInputSlot>();
      initAndAddComponents();
   }
   
   private void init(Schedule s) {
      
   }
   
   private void init(int amtClasses) {
      
   }
   
   private void initAndAddComponents() {
      for (int i = 0; i < sched.getClasses().length; i++)
         addSlot(sched.getClasses()[i]);
   }
   
   private void addSlot(ClassPeriod c) {
      GPAInputSlot add = new GPAInputSlot(c);
      center.add(add);
      slots.add(add);
   }
   
   private void addSlot(int slot) {
      addSlot(new ClassPeriod(slot));
   }
   
   public void calculateGPA() {
      
   }
}
