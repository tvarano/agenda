package tools;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import input.InputMain;
import managers.UIHandler;

//Thomas Varano
//[Program Descripion]
//Sep 25, 2017

public class AddButton extends JButton implements ActionListener
{
   private static final long serialVersionUID = 1L;
   private int slot;
   private InputMain parentPanel;
   private boolean parentIsInput;

   public AddButton(int slot, JComponent parentPanel) {
      super("Add "+slot+" Period");
      setBorderPainted(false);
      setFocusable(false);
      setOpaque(false);
      setFont(UIHandler.getButtonFont());
      setForeground(UIHandler.foreground);
      setCursor(new Cursor(Cursor.HAND_CURSOR));
      setSlot(slot);
      addActionListener(this);
      addMouseListener(UIHandler.buttonPaintListener(this));
      parentIsInput = parentPanel instanceof InputMain;
      if (parentIsInput)
         setParentPanel((InputMain)parentPanel);
   }
   
   public int getSlot() {
      return slot;
   }
   public void setSlot(int slot) {
      this.slot = slot;
   }
   public InputMain getParentPanel() {
      return parentPanel;
   }
   public void setParentPanel(InputMain parentPanel) {
      this.parentPanel = parentPanel;
   }
   @Override
   public void actionPerformed(ActionEvent e) {
      if (parentIsInput) {
         parentPanel.addClass(slot);
      }
   }
   
   //If you want a menu...
   public static class Menu extends JComboBox<String> implements ActionListener
   { 
      private static final long serialVersionUID = 1L;
      private InputMain parentPanel;
      private boolean parentIsInput;
      
      public Menu(JPanel parentPanel) {
         super();
         addActionListener(this);
         setCursor(new Cursor(Cursor.HAND_CURSOR));
         setEditable(false);
         String[] classes = new String[3];
         classes[0] = "Add Class At:";
         classes[1] = ""+0; classes[2] = ""+8;
         setModel(new DefaultComboBoxModel<String>(classes));
         
         parentIsInput = (parentPanel instanceof InputMain);
         if (parentIsInput)
            setParentPanel((InputMain) parentPanel);
         
      }

      public InputMain getParentPanel() {
         return parentPanel;
      }
      public void setParentPanel(InputMain parentPanel) {
         this.parentPanel = parentPanel;
      }
      
      @Override
      public void actionPerformed(ActionEvent e) {
         @SuppressWarnings("unchecked")
         JComboBox<String> c = (JComboBox<String>) e.getSource();
         if (parentIsInput) {
            if (c.getSelectedIndex() != 0) {
               parentPanel.addClass(Integer.parseInt((String) c.getSelectedItem()));
            }
         }
      }
   }
   
   public static void main(String[] args) {
      JFrame test = new JFrame("AddButtonTest");
      JPanel pane = new JPanel();
      pane.add(new AddButton.Menu(pane));
      test.add(pane);
      test.pack();
      test.setVisible(true);
      test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      test.setLocationRelativeTo(null);
   }
}