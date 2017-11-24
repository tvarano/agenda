package tools;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;

import constants.RotationConstants;
import display.DisplayMain;
import managers.UIHandler;

//Thomas Varano
//[Program Descripion]
//Sep 22, 2017

public class InstanceButton extends JButton implements ActionListener
{
   private static final long serialVersionUID = 1L;
   public static final String DELAY = "Delayed Opening", HALF = "Half Day";
   private boolean enacted;
   private Border enactedBorder, unEnactedBorder;
   private  ToolBar parentBar;
   
   public InstanceButton(String key) {
      super(key);
      enacted = false;
      setFocusable(false);
      setName("instanceButton "+key);
      setFont(UIHandler.getButtonFont());
      setToolTipText("Alter Next Selection to "+key);
      setForeground(UIHandler.foreground);
      setOpaque(false);
      unEnactedBorder = BorderFactory.createRaisedSoftBevelBorder(); 
      enactedBorder = BorderFactory.createLoweredSoftBevelBorder();
      addActionListener(this);
      setCursor(new Cursor(Cursor.HAND_CURSOR));
      setBorder(unEnactedBorder);
      if (getParent() instanceof ToolBar) 
         parentBar = (ToolBar) getParent();
   }

   public void repaint() {
      super.repaint();
      if (parentBar != null) {
         if (getText().equalsIgnoreCase(HALF) &&
               parentBar.isDelayed())
            setEnabled(false);
         else if (getText().equalsIgnoreCase(DELAY) && parentBar.isHalf())
            setEnabled(false);
         else
            setEnabled(true);
         }
   }
   private Border calcBorder() {
      if (enacted)
         return enactedBorder;
      return unEnactedBorder;
   }
   
   public Border getEnactedBorder() {
      return enactedBorder;
   }
   public void setEnactedBorder(Border enactedBorder) {
      this.enactedBorder = enactedBorder;
   }
   public Border getUnEnactedBorder() {
      return unEnactedBorder;
   }
   public void setUnEnactedBorder(Border unEnactedBorder) {
      this.unEnactedBorder = unEnactedBorder;
   }
   public ToolBar getParentBar() {
      return parentBar;
   }
   public void setParentBar(ToolBar bar) {
      this.parentBar = bar;
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      enacted = !enacted;
      setBorder(calcBorder());
      if (parentBar != null) {
         DisplayMain mainParent = null;
         if (parentBar.getParentPanel() instanceof DisplayMain)
            mainParent = (DisplayMain) parentBar.getParentPanel();
         if (getText().equalsIgnoreCase(DELAY)) {
            parentBar.setDelayed(enacted);
            mainParent.setTodayR((enacted) ? RotationConstants.toDelay(mainParent.getTodayR())
                  : RotationConstants.toNormal(mainParent.getTodayR()));
         }
         else if (getText().equalsIgnoreCase(HALF)) {
            parentBar.setHalf(enacted);
            mainParent.setTodayR((enacted) ? RotationConstants.toHalf(mainParent.getTodayR())
                  : RotationConstants.toNormal(mainParent.getTodayR()));
            }
      }
      for (Component b : parentBar
            .getComponents()) {
         b.repaint();
      }
      repaint();
   }

}
