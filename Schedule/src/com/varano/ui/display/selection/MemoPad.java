//Thomas Varano
//[Program Descripion]
//Dec 6, 2017

package com.varano.ui.display.selection;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextPane;

import com.varano.information.ClassPeriod;
import com.varano.managers.Agenda;
import com.varano.ui.UIHandler;

public class MemoPad extends JTextPane implements FocusListener
{
   private static final long serialVersionUID = 1L;
   private ClassPeriod parentClass;
   private ScheduleInfoSelector parentPanel;
   private boolean debug;

   public MemoPad(ClassPeriod parentClass, ScheduleInfoSelector parentPanel) {
      super();
      debug = false;
      setBackground(UIHandler.quaternary);
      setForeground(UIHandler.foreground);
      setFont(UIHandler.font);
      this.setMinimumSize(new Dimension(100,100));
      addFocusListener(this);
      setParentPanel(parentPanel); setParentClass(parentClass); 
   }

   public void save() {
      if (this.parentClass != null) {
         Agenda.log("memo saved "+parentClass.memoryInfo());
         if (debug) System.out.println("memo saved \"" + getText() + "\" to " + parentClass.memoryInfo());
         parentClass.setMemo(getText());
      } else
         Agenda.log("memo saved null");
   }
   
   private void checkAccessibility() {
      if (parentPanel == null) return;
      if (parentClass == null) {
         setText("Cannot Write Memo");
         parentPanel.setMemoBorderTitle("UnDeclared Class");
         setEnabled(false);
      }
      else {
         setEnabled(true);
         setText(parentClass.getMemo());
      }
   }
   public ClassPeriod getParentClass() {
      return parentClass;
   }
   public void setParentClass(ClassPeriod parentClass) {
      save();
      if (debug && parentClass != null) System.out.println("\tMemo Recieved "+parentClass.memoryInfo());
      this.parentClass = parentClass;
      checkAccessibility();
      repaint();
   }
   public ScheduleInfoSelector getParentPanel() {
      return parentPanel;
   }
   public void setParentPanel(ScheduleInfoSelector parentPanel) {
      this.parentPanel = parentPanel;
   }

   private void callWrite() {
      if (parentPanel != null)
         parentPanel.getParentPane().writeMain();
   }
   
   public boolean hasChanges() {
      if (parentClass == null)
         return false;
      return !parentClass.getMemo().equals(getText());
   }
      
   @Override
   public void focusGained(FocusEvent e) {}

   @Override
   public void focusLost(FocusEvent e) {
      save();
      callWrite();
   }
}
