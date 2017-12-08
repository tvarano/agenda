//Thomas Varano
//[Program Descripion]
//Dec 6, 2017

package display;

import java.awt.Dimension;

import javax.swing.JTextPane;

import information.ClassPeriod;
import managers.UIHandler;

public class MemoPad extends JTextPane
{
   private static final long serialVersionUID = 1L;
   private ClassPeriod parentClass;
   private SouthernCurrentClassPane parentPanel;

   public MemoPad(ClassPeriod parentClass, SouthernCurrentClassPane parentPanel) {
      super();
      setBackground(UIHandler.quaternary);
      setForeground(UIHandler.foreground);
      setFont(UIHandler.font);
      setParentClass(parentClass); setParentPanel(parentPanel);
      this.setMinimumSize(new Dimension(100,100));
   }

   public void save() {
      if (this.parentClass != null)
         this.parentClass.setMemo(getText());
   }
   
   private void checkAccessibility() {
      if (parentClass == null) {
         setText("Sorry, cannot do memos for this class");
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
      this.parentClass = parentClass;
      checkAccessibility();
      repaint();
   }
   public SouthernCurrentClassPane getParentPanel() {
      return parentPanel;
   }
   public void setParentPanel(SouthernCurrentClassPane parentPanel) {
      this.parentPanel = parentPanel;
   }
}
