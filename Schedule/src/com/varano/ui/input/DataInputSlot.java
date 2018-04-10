package com.varano.ui.input;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.varano.information.ClassPeriod;
import com.varano.information.constants.RotationConstants;
import com.varano.managers.Agenda;
import com.varano.managers.UIHandler;

//Thomas Varano
//[Program Descripion]
//Sep 20, 2017

public class DataInputSlot extends JPanel implements ActionListener
{
   private static final long serialVersionUID = 1L;
   private static final int WIDTH = 615, F_HEIGHT = 25;

   private static final Dimension NAME_SIZE = new Dimension(115, F_HEIGHT);
   private static final Dimension TEACH_SIZE = new Dimension(135, F_HEIGHT);
   private static final Dimension ROOM_SIZE = new Dimension(53, F_HEIGHT);
   private int slotNumber;
   private String beginName;
   private Container parentPanel;
   private JCheckBox labBox;
   private ClassPeriod dataHolder;
   private boolean hasParent, hasLab, removable, labFriendly;
   private JTextField[] promptFields;
   private static boolean debug;
   
   public DataInputSlot(int slotNumber, Container parentPanel) {
      this (new ClassPeriod(slotNumber), parentPanel);
      if (debug) System.out.println("input slot "+slotNumber+" initialized empty");
   }
   
   public DataInputSlot(ClassPeriod c, Container parentPanel) {
      if (c == null) c = new ClassPeriod();
      debug = false;
      dataHolder = c.clone();
      beginName = c.getName();
      if (debug) System.out.println("input 56 dataholder\n"+dataHolder.getInfo());
      setFont(UIHandler.getInputLabelFont());
      if (parentPanel != null) {
         setBackground(UIHandler.background);
         setForeground(UIHandler.foreground);
      }
      setName(c.getSlot() + "input slot");
      setSlotNumber(c.getSlot());
      hasLab = false; 
      removable = ((slotNumber == 0 || slotNumber == 8) && parentPanel != null);
      if (parentPanel instanceof DataInput) {
         this.parentPanel = (DataInput)parentPanel;
          hasParent = true;
      }
      else
         this.parentPanel = parentPanel;
      labFriendly = true;
      int amtFields = 3;
      promptFields = new JTextField[amtFields];
      addComponents(c);  
   }
   
   public Dimension getPreferredSize() {
      return new Dimension(Agenda.PREF_W-100, 30);
   }
   
   public static ClassPeriod showInputSlot() {
      DataInputSlot in = new DataInputSlot(RotationConstants.NO_SLOT, null);
      in.setLabFriendly(false);
      
      if (JOptionPane.showOptionDialog(null, in, "CREATE", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
            null, null, null) == 0)
         return in.createClass();
      if (debug) System.out.println("returning null");
      return null;
   }
   
   private void addComponents(ClassPeriod c) {
      int index = 0;
      ((FlowLayout)getLayout()).setAlignment(FlowLayout.LEFT);
      //label for the class slot
      JLabel labelLeft = new JLabel((slotNumber == RotationConstants.PASCACK) ? "P-" : slotNumber+"-");
      if (c.getSlot() == RotationConstants.NO_SLOT)
         labelLeft.setText("");
      labelLeft.setFont(getFont());
      labelLeft.setForeground(getForeground());
      add(labelLeft);
      
      JLabel currentLabel = new JLabel("Class Name:");            // class name prompt
      addLabel(currentLabel, labelLeft);
      
      JTextField currentField = new JTextField(c.getName());      //class name field
      currentField.addKeyListener(new KeyListener() {
         @Override
         public void keyPressed(KeyEvent arg0) {}
         @Override
         public void keyReleased(KeyEvent arg0) {
            checkScience();
         }
         @Override
         public void keyTyped(KeyEvent arg0) {}
      });
      addField(currentField, currentLabel, index);
      index++;
      
      currentLabel = new JLabel("Teacher:");                      //teacher prompt
      addLabel(currentLabel, currentField);
      
      currentField = new JTextField(c.getTeacher());              //teacher field
      addField(currentField, currentLabel, index); 
      index++;
      
      currentLabel = new JLabel("Room:");                          // rm number prompt
      addLabel(currentLabel, currentField);
      
      currentField = new JTextField(c.getRoomNumber());                       //rm number field
      addField(currentField, currentLabel, index); 

      labBox = new JCheckBox("Has Lab");              // check box to see if you have lab in that class
      labBox.setActionCommand("lab");
      labBox.addActionListener(this);
      labBox.setFont(getFont());
      labBox.setOpaque(false);
      labBox.setForeground(getForeground());
      labBox.setBackground(getBackground());
      if (labFriendly) {
         add(labBox);
      }
      if (debug) System.out.println("slot "+slotNumber+"componentSize:"+getComponents().length);
      
      if (removable) {
         JButton remove = new JButton("Remove");                     //button to remove
         remove.setFont(UIHandler.getButtonFont());
         remove.setActionCommand("remove");
         remove.addActionListener(this);
         add(remove);
      }
   }
   
   private void addField(JTextField f, JComponent c, int index) {
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
            f.revalidate();
         }

         private void setToolTipField(JTextField f) {
            f.setToolTipText(f.getText());
         }
      });
      promptFields[index] = f;  
   }
   
   private void addLabel(JLabel f, JComponent c) {
      f.setFont(getFont());
      f.setForeground(getForeground());
      add(f);   
   }
   
   public void forceNames() {
      for (JTextField f : promptFields)
         if (f.getText().equals(""))
            f.setText("unspecified");
   }
   
   private void checkScience() {
      String text = promptFields[0].getText().toLowerCase();
      String[] sciences = {"chem", "science", "bio", "physics"};
      for (String s : sciences)
         if (text.contains(s))
            labBox.setSelected(true);      
   }
   
   private void checkHonorsText() {
      String text = promptFields[0].getText().toLowerCase();
      if (text.equalsIgnoreCase(beginName))
         return;
      System.out.println(text);
      if (text.contains("honors") || text.contains("ap"))
         dataHolder.setHonors(true);
   }
   
   public ClassPeriod createClass() {
      forceNames();
      checkHonorsText();
      if (hasParent && hasLab) {
         ((DataInput) parentPanel).addLab(slotNumber);
         if (debug) System.out.println("\tslot" + slotNumber + "Added lab");
      }
      ClassPeriod retval = new ClassPeriod(slotNumber,
            promptFields[0].getText(), promptFields[1].getText(),
            promptFields[2].getText());
      retval.setBackgroundData(dataHolder);
      if (debug) System.out.println("created:" + retval.getInfo());
      if (dataHolder.isHonors())
         System.out.println(retval.getName() + " honors");
      return retval;
   }
   
   public void setLab(boolean hasLab) {
      labBox.setSelected(hasLab);
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
