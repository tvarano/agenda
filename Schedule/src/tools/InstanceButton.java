package tools;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import constants.RotationConstants;
import display.DisplayMain;
import managers.Agenda;
import managers.UIHandler;

//Thomas Varano
//[Program Descripion]
//Sep 22, 2017

public class InstanceButton extends JCheckBox implements ActionListener
{
   private static final long serialVersionUID = 1L;
   public static final String DELAY = "Delayed Open", HALF = "Half Day";
   private ToolBar parentBar;
   
   public InstanceButton(String key) {
      super(key);
      setName("instanceButton "+key);
      setFont(UIHandler.getButtonFont());
      setToolTipText("Alter Selection to "+key);
      setForeground(UIHandler.foreground);
      setOpaque(false);
      addActionListener(this);
      setCursor(new Cursor(Cursor.HAND_CURSOR));
      if (getParent() instanceof ToolBar) 
         parentBar = (ToolBar) getParent();
   }
   
   public void repaint() {
      calculateSelected();
      super.repaint();
   }
   
   public ToolBar getParentBar() {
      return parentBar;
   }
   public void setParentBar(ToolBar bar) {
      this.parentBar = bar;
   }

   public void calculateSelected() {
      if (parentBar == null)
         return;
      if (parentBar.isHalf()) {
         if (getText().equalsIgnoreCase(DELAY))
            setSelected(false);
         else if (getText().equalsIgnoreCase(HALF))
            setSelected(true);
      }
      else if (parentBar.isDelayed()) {
         if (getText().equalsIgnoreCase(DELAY))
            setSelected(true);
         else if (getText().equalsIgnoreCase(HALF))
            setSelected(false);
      }
   }
   
   @Override
   public void actionPerformed(ActionEvent e) {
      System.out.println("SELECTED" + isSelected());
      if (parentBar != null) {
         DisplayMain mainParent = null;
         if (parentBar.getParentPanel() instanceof DisplayMain)
            mainParent = (DisplayMain) parentBar.getParentPanel();
         else 
            Agenda.logError("INSTANCE ERROR "+ parentBar.getParentPanel(), new ClassCastException("Cannot cast parent panel"));
         if (getText().equalsIgnoreCase(DELAY)) {
            parentBar.setDelayed(isSelected());
            mainParent.setTodayR((isSelected()) ? RotationConstants.toDelay(RotationConstants.toNormal(mainParent.getTodayR()))
                  : RotationConstants.toNormal(mainParent.getTodayR()));
         }
         else if (getText().equalsIgnoreCase(HALF)) {
            parentBar.setHalf(isSelected());
            mainParent.setTodayR((isSelected()) ? RotationConstants.toHalf(RotationConstants.toNormal(mainParent.getTodayR()))
                  : RotationConstants.toNormal(mainParent.getTodayR()));
            }
         parentBar.repaint();
      }
   }
}
