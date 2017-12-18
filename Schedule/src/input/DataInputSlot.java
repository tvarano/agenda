package input;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import constants.RotationConstants;
import information.ClassPeriod;
import managers.UIHandler;

//Thomas Varano
//[Program Descripion]
//Sep 20, 2017

public class DataInputSlot extends JPanel implements ActionListener
{
   private static final long serialVersionUID = 1L;
   private static final int gap = 4;
   private static final int WIDTH = 615, F_HEIGHT = 25;

   private static final Dimension NAME_SIZE = new Dimension(115, F_HEIGHT);
   private static final Dimension TEACH_SIZE = new Dimension(135, F_HEIGHT);
   private static final Dimension ROOM_SIZE = new Dimension(40, F_HEIGHT);
   private int slotNumber;
   private Container parentPanel;
   private JCheckBox labBox;
   private String memo;
   private boolean hasParent, hasLab, removable, labFriendly, debug;
   private JTextField[] promptFields;
   
   public DataInputSlot(int slotNumber, Container parentPanel) {
      this (new ClassPeriod(slotNumber), parentPanel);
      if (debug) System.out.println("input slot "+slotNumber+" initialized empty");
   }
   
   public DataInputSlot(ClassPeriod c, Container parentPanel) {
      if (c == null) c = new ClassPeriod();
      debug = false;
      setFont(UIHandler.getInputLabelFont());
      setBackground(UIHandler.background);
      setForeground(UIHandler.foreground);
      setName(c.getSlot() + "input slot");
      setSlotNumber(c.getSlot());
      hasLab = false; 
      removable = (slotNumber == 0 || slotNumber == 8);
      if (parentPanel instanceof DataInput) {
         this.parentPanel = (DataInput)parentPanel;
          hasParent = true;
      }
      else
         this.parentPanel = parentPanel;
      setMemo(c.getMemo());
      if (debug) System.out.println(getName() + "INPUT SLOT CREATED WITH MEMO: "+memo);
      labFriendly = true;
      int amtFields = 3;
      promptFields = new JTextField[amtFields];
      setLayout(new SpringLayout());
      addComponents(c);
      
   }
   
   private void addComponents(ClassPeriod c) {
      int index = 0;
      SpringLayout l = (SpringLayout) getLayout();
      //label for the class slot
      JLabel labelLeft = new JLabel((slotNumber == RotationConstants.PASCACK) ? "P-" : slotNumber+"-"); 
      labelLeft.setFont(getFont());
      labelLeft.setForeground(getForeground());
      add(labelLeft);
      l.putConstraint(SpringLayout.WEST, labelLeft, gap*2, SpringLayout.WEST, this);
      setNorthBound(labelLeft);
      
      JLabel currentLabel = new JLabel("Class Name:");            // class name prompt
      addLabel(currentLabel, labelLeft, l, index);
      
      JTextField currentField = new JTextField(c.getName());      //class name field
      addField(currentField, currentLabel, l, index);
      index++;
      
      currentLabel = new JLabel("Teacher:");                      //teacher prompt
      addLabel(currentLabel, currentField, l, index);
      
      currentField = new JTextField(c.getTeacher());              //teacher field
      addField(currentField, currentLabel, l, index); 
      index++;
      
      currentLabel = new JLabel("Room:");                          // rm number prompt
      addLabel(currentLabel, currentField, l, index);
      
      currentField = new JTextField(c.getRoomNumber());                       //rm number field
      addField(currentField, currentLabel, l, index); 

      labBox = new JCheckBox("Has Lab");              // check box to see if you have lab in that class
      labBox.setActionCommand("lab");
      labBox.addActionListener(this);
      labBox.setFont(getFont());
      labBox.setOpaque(false);
      labBox.setForeground(getForeground());
      labBox.setBackground(getBackground());
      if (labFriendly) {
         add(labBox);
         l.putConstraint(SpringLayout.WEST, labBox, gap*2, SpringLayout.EAST, currentField);
         setNorthBound(labBox);
      }
      if (debug) System.out.println("slot "+slotNumber+"componentSize:"+getComponents().length);
      
      if (removable) {
         JButton remove = new JButton("Remove");                     //button to remove
         remove.setFont(UIHandler.getButtonFont());
         remove.setActionCommand("remove");
         remove.addActionListener(this);
         setNorthBound(remove);
         l.putConstraint(SpringLayout.WEST, remove, gap*2, SpringLayout.EAST, labBox);
         add(remove);
      }
   }
   
   private void addField(JTextField f, JComponent c, SpringLayout l, int index) {
      final int name = 0, teacher = 1;
      f.setMinimumSize((index == name) ? NAME_SIZE : (index == teacher) ? TEACH_SIZE : ROOM_SIZE);
      f.setMaximumSize((index == name) ? NAME_SIZE : (index == teacher) ? TEACH_SIZE : ROOM_SIZE);
      f.setPreferredSize((index == name) ? NAME_SIZE : (index == teacher) ? TEACH_SIZE : ROOM_SIZE);
      f.setFont(UIHandler.getInputFieldFont());
      add(f);   
      f.setToolTipText(f.getText());
      f.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            setToolTipField((JTextField) e.getSource());
         }
         private void setToolTipField(JTextField f) {
            f.setToolTipText(f.getText());
         }
      });
      f.addFocusListener(new FocusListener() {
         @Override
         public void focusGained(FocusEvent e) {}

         @Override
         public void focusLost(FocusEvent e) {
          setToolTipField((JTextField)e.getSource());      
         }

         private void setToolTipField(JTextField f) {
            f.setToolTipText(f.getText());
         }
      });
      promptFields[index] = f;  
      l.putConstraint(SpringLayout.WEST, f, gap, SpringLayout.EAST, c);
      setNorthBound(f);
   }
   
   private void addLabel(JLabel f, JComponent c, SpringLayout l, int index) {
      f.setFont(getFont());
      f.setForeground(getForeground());
      add(f);   
//      promptLabels[index] = f;
      l.putConstraint(SpringLayout.WEST, f, gap, SpringLayout.EAST, c);
      setNorthBound(f);
   }
   
   private void setNorthBound(JComponent c) {
      int vgap = (c instanceof JTextField || c instanceof JCheckBox || c instanceof JButton) ? gap : gap*2;
      ((SpringLayout) getLayout()).putConstraint(SpringLayout.NORTH, c, vgap, SpringLayout.NORTH, this);
   }
//   //TODO
//   public void showError() {
//      error = true;
//      ErrorID.showError(ErrorID.INPUT_ERROR, true);
//      if (debug) System.out.println(getName()+" has error");
//   }
//  //TODO
//   public void resolveError() {
//      if (error) {
//         if (debug) System.out.println(getName()+" resolved error");
//      }
//      error = false;
//   }
  
   public boolean checkCanCreate() {
      for (JTextField f : promptFields)
         if (f.getText().equals(""))
            return false;
      return true;
   }
   
   public ClassPeriod createClass() {
      if (checkCanCreate()) {
//         resolveError();
         if (hasParent && hasLab) {
            ((DataInput) parentPanel).addLab(slotNumber);
            if (debug)
               System.out.println("\tslot" +slotNumber +"Added lab");
         }
         ClassPeriod retval = new ClassPeriod(slotNumber, promptFields[0].getText(), promptFields[1].getText(), 
               promptFields[2].getText());
         retval.setMemo(memo);
         if (debug)
            System.out.println("created:"+retval.getInfo());
         return retval;
      }
      return new ClassPeriod();
   }
   
   public void setLab(boolean hasLab) {
      labBox.setSelected(hasLab);
//      ((JCheckBox)getComponents()[getComponents().length-1]).setSelected(hasLab);
      this.hasLab = hasLab;
   }

   public String toString() {
      return getClass().getName()+"[slot="+slotNumber+", lab="+hasLab+"]";
   }
   public int getSlotNumber() {
      return slotNumber;
   }
   public void setSlotNumber(int slotNumber) {
      this.slotNumber = slotNumber;
   }
   public boolean isRemovable() {
      return removable;
   }
   public void setRemovable(boolean removable) {
      this.removable = removable;
   }
   public boolean needsScroll() {
      return WIDTH > parentPanel.getWidth();
   } 
   public JTextField[] getPromptFields() {
      return promptFields;
   }
   public boolean isLabFriendly() {
      return labFriendly;
   }
   public void setLabFriendly(boolean b) {
      labFriendly = b;
      if (!labFriendly)
         remove(labBox);
   }

   public String getMemo() {
      return memo;
   }

   public void setMemo(String memo) {
      this.memo = memo;
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      if (e.getSource() instanceof AbstractButton) {
         AbstractButton b = (AbstractButton) e.getSource();
         if (b.getActionCommand().equals("remove")) {
            if (hasParent)
               ((DataInput) parentPanel).removeClassAndReOrder(slotNumber, this);
            else
               parentPanel.remove(this);
         }
         else if (b.getActionCommand().equals("lab")) {
            hasLab = !hasLab;
            if (debug) System.out.println(slotNumber+" has lab");
         }
         if (debug) System.out.println("unassigned action for "+e.getSource());
      }
      parentPanel.repaint();
   }
}
