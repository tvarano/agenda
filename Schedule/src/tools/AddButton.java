package tools;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import input.DataInput;
import input.GPAInput;
import input.InputManager;
import managers.UIHandler;

//Thomas Varano
//[Program Descripion]
//Sep 25, 2017

public class AddButton extends JButton implements ActionListener
{
   private static final long serialVersionUID = 1L;
   private int slot;
   private InputManager parentPanel;

   public AddButton(int slot, InputManager parentPanel) {
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
      if (parentPanel instanceof InputManager) {
         setParentPanel(parentPanel);
      }
   }
   
   public int getSlot() {
      return slot;
   }
   public void setSlot(int slot) {
      this.slot = slot;
   }
   public InputManager getParentPanel() {
      return parentPanel;
   }
   public void setParentPanel(InputManager parentPanel) {
      this.parentPanel = parentPanel;
   }
   @Override
   public void actionPerformed(ActionEvent e) {
      parentPanel.addClass(slot);
   }
   
   //If you want a menu...
   public static class Menu extends JComboBox<String> implements ActionListener
   { 
      private static final long serialVersionUID = 1L;
      private InputManager parentPanel;
      
      public Menu(InputManager parentPanel, int amtClasses) {
         super();
         addActionListener(this);
         setCursor(new Cursor(Cursor.HAND_CURSOR));
         setEditable(false);
         String[] classes = new String[3];
         classes[0] = "Add Class At:";
         classes[1] = ""+0; classes[2] = ""+8;
         setModel(new DefaultComboBoxModel<String>(classes));
         setParentPanel( parentPanel); 
      }
      public InputManager getParentPanel() {
         return parentPanel;
      }
      public void setParentPanel(InputManager parentPanel) {
         this.parentPanel = parentPanel;
      }
      
      @Override
      public void actionPerformed(ActionEvent e) {
         @SuppressWarnings("unchecked")
         JComboBox<String> c = (JComboBox<String>) e.getSource();
         if (c.getSelectedIndex() != 0) 
            parentPanel.addClass(Integer.parseInt((String) c.getSelectedItem()));
      }
   }
}