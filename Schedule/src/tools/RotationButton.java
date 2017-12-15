package tools;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;


import constants.Rotation;
import constants.RotationConstants;
import display.DisplayMain;
import managers.UIHandler;

//Thomas Varano
//[Program Descripion]
//Sep 19, 2017

public class RotationButton extends JButton implements ActionListener
{
   private static final long serialVersionUID = 1L;
   public static final String TODAY_R = "~currentRotation$";
   private Rotation r;
   private JPanel parentPanel;
   private boolean debug, overridingPower, highlight;
   
   /**
    * @param text
    * @param parentPanel
    */
   public RotationButton(String text, JPanel parentPanel) {
      super(" "+text+" ");
      debug = false;
      setParentPanel(parentPanel);
      setBorderPainted(false);
      setFocusable(false);
      setFont(UIHandler.getButtonFont());
      highlight = false;
      setForeground(UIHandler.foreground);
      setBackground(UIHandler.tertiary.brighter());
      setCursor(new Cursor(Cursor.HAND_CURSOR));
      addMouseListener(UIHandler.buttonPaintListener(this));
      addActionListener(this);
      if (text == TODAY_R) {
         overridingPower = true;
         if (debug) System.out.println(text+" button parent:"+parentPanel);
         if (parentPanel instanceof DisplayMain) {
            setText(" Today's Rotation ");
            r = ((DisplayMain) parentPanel).getTodayR();
         if (debug)
            System.out.println(getText());
         }
      }
      else 
         this.r = RotationConstants.getRotation(getText().trim());
      setName(text+ " button");
   }
   public void repaint() {
      setOpaque(highlight);
      super.repaint();
   }
   
   public RotationButton(int i, JPanel parentPanel) {
      this(RotationConstants.getName(i), parentPanel);  
   }
   public RotationButton(Rotation r, JPanel parentPanel) {
      this(RotationConstants.getName(r.getIndex()), parentPanel);
   }
   public JPanel getParentPanel() {
      return parentPanel;
   }
   public void setParentPanel(JPanel parentPanel) {
      this.parentPanel = parentPanel;
   }
   public boolean isHighlighted() {
      return highlight;
   }
   public void setHighlight(boolean highlight) {
      this.highlight = highlight;
   }
   public boolean equals(Rotation o) {
      return RotationConstants.equalsAllTypes(r, o);
   }
   public boolean equals(RotationButton o) {
      if (o == null)
         return false;
      return (RotationConstants.equalsAllTypes(r, o.r));
//      return r.equals(o.r);
   }
   
   @Override
   public void actionPerformed(ActionEvent e) {
      if (debug) System.out.println(getName()+"clicked");
      DisplayMain parentPane;
      ToolBar parentBar;
      if (getParent().getParent() instanceof DisplayMain) {
         parentPane = ((DisplayMain)getParent().getParent());
         if (parentPane.isUpdating())
            return;
         if (getParent() instanceof ToolBar) {
            parentBar = (ToolBar)getParent();
            if (overridingPower) {
               parentBar.setHalf(r.isHalf());
               parentBar.setDelayed(r.isDelay());
            }
            if (parentBar.isDelayed())
               parentPane.setTodayR(RotationConstants.toDelay(r));
            else if (parentBar.isHalf())
               parentPane.setTodayR(RotationConstants.toHalf(r));
            else {
               parentPane.setTodayR(r);
            }
            parentBar.setRotation(r);
            parentBar.repaint();
            if (debug)  System.out.println("set rotation to + "+r);
         }
      }
      else System.err.println(getName()+" parent incorrect");
   }
}
