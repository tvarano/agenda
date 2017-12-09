package tools;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import constants.Rotation;
import display.DisplayMain;
import managers.UIHandler;

//Thomas Varano
//[Program Description]
//Sep 19, 2017

public class ToolBar extends JToolBar implements ActionListener
{
   private static final long serialVersionUID = 1L;
   public static final int ZERO_BUTTON = 0, EIGHT_BUTTON = 1;
   private boolean inputting, delayed, half;
   private JPanel parentPanel;

   public ToolBar(boolean inputting, JPanel parentPanel) {
      setParentPanel(parentPanel);
      setInputting(inputting);
      setBorderPainted(false);
      setName("ToolBar");
      setBackground(UIHandler.tertiary);
      setFloatable(false);
      setOpaque(true);
      setMargin(new Insets(7,5,0,0));
   }
   
   private ToolBar create(boolean inputting) {
      setBackground(UIHandler.background);
      if (inputting)
         return createToolBarInput();
      return createToolBarDisplay();
   }
   
   public void repaint() {
      if (!inputting) {
         DisplayMain dm = (DisplayMain)parentPanel;
         if (dm != null && dm.getTodayR() != null) {
            setHalf(dm.getTodayR().isHalf());
            setDelayed(dm.getTodayR().isDelay());
         }
      }
      for (Component b : getComponents()) {
         b.repaint();
      }
      super.repaint();
   }
   
   private ToolBar createToolBarDisplay() {
      removeAll();
      add(new RotationButton(RotationButton.TODAY_R, parentPanel));
      add(new RotationButton(Rotation.R1, parentPanel));
      add(new RotationButton(Rotation.R2, parentPanel));
      add(new RotationButton(Rotation.R3, parentPanel));
      add(new RotationButton(Rotation.R4, parentPanel));
      add(new RotationButton(Rotation.ODD_BLOCK, parentPanel));
      add(new RotationButton(Rotation.EVEN_BLOCK, parentPanel));
      InstanceButton b = new InstanceButton(InstanceButton.DELAY);
      b.setParentBar(this);
      add(b);
      b = new InstanceButton(InstanceButton.HALF);
      b.setParentBar(this);
      add(b);
      JButton input = new JButton("Input Schedule");
      input.setForeground(UIHandler.foreground);
      input.setFocusable(false);
      input.setBorderPainted(false);
      input.setCursor(new Cursor(Cursor.HAND_CURSOR));
      input.setOpaque(false);
      input.setFont(UIHandler.getButtonFont());
      input.addMouseListener(UIHandler.buttonPaintListener(input));
      input.addActionListener(((DisplayMain) parentPanel).changeView());
      add(input);
      return this;
   }
   
   //make a button for adding a class
   private ToolBar createToolBarInput() {
      removeAll();
      add(new AddButton(0, parentPanel));
      add(new AddButton(8, parentPanel));
      return this;
   }

   public boolean isInputting() {
      return inputting;
   }
   public void setInputting(boolean inputting) {
      this.inputting = inputting;
      create(inputting);
   }
   public boolean isDelayed() {
      return delayed;
   }
   public void setDelayed(boolean delayed) {
      this.delayed = delayed;
   }
   public boolean isHalf() {
      return half;
   }
   public void setHalf(boolean half) {
      this.half = half;
   }
   public JPanel getParentPanel() {
      return parentPanel;
   }
   public void setParentPanel(JPanel parentPanel) {
      this.parentPanel = parentPanel;
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      System.out.println(parentPanel.getWidth()+","+parentPanel.getHeight());
   }
}
