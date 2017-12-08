package display;
import java.awt.Color;
import java.time.LocalTime;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import information.ClassPeriod;
import information.Time;
import managers.UIHandler;

public class CurrentInfo extends JTextPane{
      private static final long serialVersionUID = 1L;
      private ClassPeriod c;
      private int situation;
      private static final int NO_PARENT = 0, IN_BETWEEN = 1, NOT_IN_SCHOOL = 2, IN_CLASS = 3;
      private NorthernCurrentClassPane parentPanel;
      private boolean hasParent, debug;
      
      public CurrentInfo(ClassPeriod c, JPanel parentPanel) {
         super();
         debug = false;
         setOpaque(false);
         setBackground(UIHandler.background);
         setForeground(UIHandler.foreground);
         setParentPanel(parentPanel);  setClassPeriod(c);
         if (hasParent)
            setName(parentPanel.getName()+" current info");
         else
            setName("Current Info Pane");
         repaintText();
         setEditable(false);
      }
      
      /**
       * CAREFUL method is much heavier than default repaint
       * @see #createText()
       */
      public void repaintText() {
         createText();
         super.repaint();
      }
      
      private int findAndSetSituation() {
         if (hasParent) {
            if (parentPanel.isInSchool()) {
               situation = IN_BETWEEN;
               if (c != null)
                  situation = IN_CLASS;
            }
            else 
               situation = NOT_IN_SCHOOL;
         }
         else 
            situation = NO_PARENT;
         return situation;
      }
      
      public String[] getTextInput() {
         String newLn = "\n";
         if (situation == IN_CLASS) {
            boolean hour = parentPanel.getTimeLeft().getHour24() > 0;
            String durationHour = (hour) ? c.getDuration().getHour12()+ " hour, " : "";
            return new String[] {
                  "You are in"+newLn,
                  c+newLn,
                  "For ",
                  (hour) ? parentPanel.getTimeLeft().getHour24()+"" : "",
                  (hour) ? " hour and " : "",
                  parentPanel.getTimeLeft().getMinute()+"",
                  " minutes"+newLn,
                  "In "+c.getRoomNumber()+newLn,
                  newLn,
                  c.getStartTime() + " - " + c.getEndTime()+"."+newLn,
                  "The class is " + durationHour +c.getDuration().getMinute() + " minutes long." 
            };
         }
         if (situation == IN_BETWEEN) {
            return new String[] {
                  "You are in between classes."+newLn,
                  parentPanel.getParentPane().findNextClass().getName(),
                  " is next" + newLn +"in ",
                  ""+parentPanel.getParentPane().timeUntilNextClass().getMinute(),
                  " minutes."
                  
            };
         }
         if (situation == NOT_IN_SCHOOL) {
            return new String[] {
                  "You are not in school."+newLn,
                  "School starts in ",
                  new Time(LocalTime.now()).getTimeUntil(
                        parentPanel.getParentPane().getSched().getSchoolDay().getStartTime())
                  .durationString()+newLn,
                  "Incorrect? "+newLn+"Make sure your schedule is inputted correctly or"+newLn+
                        "email me at varanoth@pascack.org"
            };
         }
         return new String[]{
               "ERROR"+newLn,
               "email me your situation at:"+newLn+"varanoth@pascack.org"
         };
      }
      
      public String[] getStyles() {
         if (situation == IN_CLASS) {
            return new String[] {
                  "regular",
                  "h1",
                  "regular",
                  "h2",
                  "regular",
                  "h2",
                  "regular",
                  "h3",
                  "formatting",
                  "bold",
                  "regular"
            };
         }
         if (situation == IN_BETWEEN)
            return new String[] {
                  "regular",
                  "h1",
                  "regular",
                  "h2",
                  "regular"
            };
         if (situation == NOT_IN_SCHOOL) {
            return new String[] {
                  "h2",
                  "regular",
                  "h2",
                  "regular"
            };
         }
         return new String[] {
            "error",
            "h3"
         };
      }
      
      private void createText() {
         findAndSetSituation();
         if (debug) System.out.println(getName() + "situation="+situation);
         setText("");
         String[] uneditedText = getTextInput();
         String styles[] = getStyles();
         StyledDocument styleDoc = initStyles();
         try {
            for (int i=0; i < uneditedText.length; i++) {
               styleDoc.insertString(styleDoc.getLength(), uneditedText[i],
                     styleDoc.getStyle(styles[i]));
            }
        } catch (BadLocationException e) {
            System.err.println("Couldn't insert initial text into text pane.");
        }
      }
      
      private StyledDocument initStyles() {
         StyledDocument doc = getStyledDocument();
         Style def = StyleContext.getDefaultStyleContext().
               getStyle(StyleContext.DEFAULT_STYLE);
         if (debug) System.out.println(doc);
         
         StyleConstants.setFontFamily(def, UIHandler.font.getFamily());
         Style regular = doc.addStyle("regular", def);
         StyleConstants.setFontSize(regular, 16);
         
         if (debug) System.out.println("font:"+regular.getAttribute(StyleConstants.FontFamily));
         
         Style s = doc.addStyle("italic", regular);
         StyleConstants.setItalic(s, true);
         
         s = doc.addStyle("h1", regular);
         StyleConstants.setFontSize(s, 36);
         StyleConstants.setBold(s, true);
         
         s = doc.addStyle("h2", regular);
         StyleConstants.setFontSize(s, 24);

         s = doc.addStyle("h3", regular);
         StyleConstants.setFontSize(s, 18);
         
         s = doc.addStyle("bold", regular);
         StyleConstants.setBold(s, true);
         
         s = doc.addStyle("formatting", regular);
         StyleConstants.setFontSize(s, 8);
         
         s = doc.addStyle("error", doc.getStyle("h1"));
         StyleConstants.setForeground(s, Color.RED);
         return doc;
      }

      public void pushClassPeriod(ClassPeriod c) {
         setText("");
         setClassPeriod(c);
         repaintText();
      }
      
      public ClassPeriod getClassPeriod() {
         return c;
      }
      public void setClassPeriod(ClassPeriod c) {
         this.c = c;
      }
      public NorthernCurrentClassPane getParentPanel() {
         return parentPanel;
      }
      public void setParentPanel(JPanel parentPanel) {
         hasParent = (parentPanel instanceof NorthernCurrentClassPane);
         if (hasParent)
            this.parentPanel = (NorthernCurrentClassPane)parentPanel;
      }
      public boolean hasParent() {
         return hasParent;
      }
   }
