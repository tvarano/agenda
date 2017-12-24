package tools;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;

import constants.Rotation;
import display.DisplayMain;
import input.GPAInput;
import input.InputManager;
import managers.PanelManager;
import managers.UIHandler;

//Thomas Varano
//[Program Description]
//Sep 19, 2017

public class ToolBar extends JToolBar implements ActionListener
{
   private static final long serialVersionUID = 1L;
   public static final int ZERO_BUTTON = 0, EIGHT_BUTTON = 1;
   private boolean delayed, half;
   private Rotation rotation;
   private int parentType;
   private JPanel parentPanel;

   public ToolBar(int parentType, JPanel parentPanel) {
      setParentPanel(parentPanel);
      setParentType(parentType);
      setBorderPainted(false);
      setName("ToolBar");
      setBackground(UIHandler.tertiary);
      setFloatable(false);
      setOpaque(true);
      setMargin(new Insets(7,5,0,0));
   }
   
   private ToolBar create(int type) {
      setBackground(UIHandler.background);
      if (type == PanelManager.INPUT)
         return createToolBarDataIn();
      else if (type == PanelManager.GPA)
         return createToolBarGPA();
      return createToolBarDisplay();
   }
   
   public void repaint() {
      if (parentType == PanelManager.DISPLAY) {
         DisplayMain dm = (DisplayMain)parentPanel;
         if (dm != null && dm.getTodayR() != null) {
            setHalf(dm.getTodayR().isHalf());
            setDelayed(dm.getTodayR().isDelay());
         }
      }
      for (Component c : getComponents()) {
         c.repaint();
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
      input.addActionListener(((DisplayMain) parentPanel).changeView(PanelManager.INPUT));
      add(input);
      setHighlights();
      return this;
   }
   
   private ToolBar createToolBarGPA() {
      removeAll();
      
      JButton b = (JButton) add(new JButton("Show Display"));
      b.setForeground(UIHandler.foreground);
      b.setFocusable(false);
      b.setBorderPainted(false);
      b.setCursor(new Cursor(Cursor.HAND_CURSOR));
      b.setOpaque(false);
      b.setFont(UIHandler.getButtonFont());
      b.addActionListener(((GPAInput) parentPanel).changeView(PanelManager.DISPLAY));
      b.addMouseListener(UIHandler.buttonPaintListener(b));
      
      b = (JButton) add(new JButton("Input Schedule"));
      b.setForeground(UIHandler.foreground);
      b.setFocusable(false);
      b.setBorderPainted(false);
      b.setCursor(new Cursor(Cursor.HAND_CURSOR));
      b.setOpaque(false);
      b.setFont(UIHandler.getButtonFont());
      b.addActionListener(((GPAInput) parentPanel).changeView(PanelManager.INPUT));
      b.addMouseListener(UIHandler.buttonPaintListener(b));
      
      ButtonGroup bg = new ButtonGroup();
      JRadioButton rb = new JRadioButton("Use Numbers");
      bg.add(rb);
      rb.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            ((GPAInput) parentPanel).setMethod(true);
         }
      });
      add(rb);
      rb = new JRadioButton("Use Letter");
      bg.add(rb);
      rb.setSelected(true);
      rb.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            ((GPAInput) parentPanel).setMethod(false);
         }
      });
      add(rb);
      return this;
   }

   
   //make a button for adding a class
   private ToolBar createToolBarDataIn() {
      removeAll();
      add(new AddButton(0, (InputManager)parentPanel));
      add(new AddButton(8, (InputManager)parentPanel));
      return this;
   }

   public void setHighlights() {
      for (Component c : getComponents()) {
         if (c instanceof RotationButton) {
            RotationButton r = (RotationButton) c;
            r.setHighlight(r.equals(rotation));
         }
      }
   }
   
   public int getParentType() {
      return parentType;
   }
   public void setParentType(int parentType) {
      this.parentType = parentType;
      create(parentType);
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

   public Rotation getRotation() {
      return rotation;
   }
   
   public void setRotation(Rotation r) {
      this.rotation = r;
      setHighlights();
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      System.out.println(parentPanel.getWidth()+","+parentPanel.getHeight());
   }
}
