package display;
import java.awt.Dimension;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import information.ClassPeriod;
import managers.Agenda;
import managers.UIHandler;

//Thomas Varano
//[Program Descripion]
//Sep 10, 2017

public class ClassInfoPane extends JTextPane
{
   private static final long serialVersionUID = 1L;
   private ClassPeriod c;
   private boolean thinConstraints, debug, showNames;
   
   public ClassInfoPane(ClassPeriod c) {
      super();
      debug = false;
      setBackground(UIHandler.quaternary);
      setForeground(UIHandler.foreground);
      setName("unNamedInfoPane");
      this.setEditable(false);
      this.setClassPeriod(c);
      this.setMinimumSize(new Dimension(60,60));
      initStyles(getStyledDocument());
   }
   
   private void createClassDetailPane() {
      if (debug) System.out.println(getName() + "Parent"+getParent());
      if (c == null) {
         if (getParent() instanceof CurrentClassPane) {
            if (((CurrentClassPane) getParent()).isInSchool())
               setText("In Between Classes. \nNext Class is:\n"+((CurrentClassPane) getParent()).findNextClass());
            else
               setText("Not in school");
         }
         else 
            setText("Class not selected");
         return;
      }
      this.setText("");
      String newLine = "\n";
      String tab = (thinConstraints) ?"     ":"";
      if (debug) System.out.println(c);
      String hour = (c.getDuration().getHour24() > 0) ? c.getDuration().getHour24()+" hour, " : "";
      String teacher = (thinConstraints) ? "Teacher: " + c.getTrimmedTeacher() + newLine 
            : "Teacher: " + c.getTrimmedTeacher() + newLine;
      String classLength = (thinConstraints) ? 
            "Class Length:"+ newLine +hour+c.getDuration().getMinute()+" minutes" : 
               "Class Length: "+hour+c.getDuration().getMinute()+" minutes";
      String times = c.getStartTime() + " - " + c.getEndTime();
      
      if (showNames) {
         String[] uneditedText = {
               tab + "Rm. " + c.getTrimmedRoomNumber()+newLine, 
               tab + teacher,
               tab + times +newLine,
               tab + classLength
         };
         String[] styles = {
               "regular", 
               "regular",
               "bold",
               "italic"
         };
         putStyles(uneditedText, styles);
      } else {
         String[] uneditedText = {
               tab + times + newLine,
               tab + classLength
         };
         String[] styles = {
            "bold", 
            "italic"
         };
         putStyles(uneditedText, styles);
      }
   }
   
   private void putStyles(String[] uneditedText, String[] styles) {
      StyledDocument styleDoc = getStyledDocument();
      try {
         for (int i=0; i < uneditedText.length; i++) {
            styleDoc.insertString(styleDoc.getLength(), uneditedText[i],
                  styleDoc.getStyle(styles[i]));
         }
     } catch (BadLocationException e) {
         Agenda.logError("cannot insert styles in infoPane", e);
     }
   }
   
   private void initStyles(StyledDocument doc) {
      Style def = StyleContext.getDefaultStyleContext().
            getStyle(StyleContext.DEFAULT_STYLE);
      
      Style regular = doc.addStyle("regular", def);
      StyleConstants.setBold(regular, false);
      StyleConstants.setFontFamily(regular, UIHandler.font.getFamily());
      StyleConstants.setFontSize(regular, 14);
      
      Style s = doc.addStyle("italic", regular);
      StyleConstants.setItalic(s, true);
      
      s = doc.addStyle("bold", regular);
      StyleConstants.setBold(s, true);
   }

   public ClassPeriod getClassPeriod() {
      return c;
   }
   
   public void setClassPeriod(ClassPeriod c) {
      this.c = c;
      createClassDetailPane();
   }
   public boolean isThinConstraints() {
      return thinConstraints;
   }
   public void setThinConstraints(boolean thinConstraints) {
      this.thinConstraints = thinConstraints;
   }
   public void setShowNames(boolean showNames) {
      this.showNames = showNames;
   }
   public boolean getShowNames() {
      return showNames;
   }
}
