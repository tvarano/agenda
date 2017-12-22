package managers;
import java.awt.CardLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import display.DisplayMain;
import input.DataInput;
import input.GPAInput;

//Thomas Varano
//[Program Descripion]
//Sep 20, 2017

public class PanelManager
{
   private Agenda parent;
   private DisplayMain display;
   private DataInput input;
   private GPAInput gpa;
   private MenuBar bar;
//   private boolean inputting;
   private int currentPane;
   public static final int DISPLAY = 0, INPUT = 1, GPA = 2;
   
   
   public PanelManager(Agenda parent, MenuBar bar) { 
      setParent(parent);
      this.bar = bar;
      display = new DisplayMain(this);  display.setName("display");
      input = new DataInput(this);  input.setName("input");
      parent.setLayout(new CardLayout());
      parent.add(display, display.getName());
      parent.add(input, input.getName());
   }
   
   public void setCurrentPane(int type) {
      if (currentPane == type)
         return;
      if (type == INPUT)
          ((CardLayout) parent.getLayout()).show(parent, input.getName());
      else 
         ((CardLayout) parent.getLayout()).show(parent, display.getName());
      this.currentPane = type;
   }
   
   public MenuBar getBar() {
      return bar;
   }
   
   public DisplayMain getDisplay() {
      return display;
   }
   
   public Menu getTimeMenu() {
      System.out.println("BAR"+bar);
      return bar.getMenu(0);
   }
   
   public void startInput() {
      if (currentPane == INPUT)
         return;
      display.stop();
//      gpa.save();
      input.setBeginningSchedule(display.getMainSched());
      setCurrentPane(INPUT);
   }
   
   public ActionListener changeView(int parentType) {
      return new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (Agenda.statusU) Agenda.log("view changed. type = "+parentType);
            if (parentType == INPUT) 
               startInput();
            else if (parentType == GPA)
               startGPA();
            else
               reinitDisp();
         }
      };
   }
   
   public void startGPA() {
      
   }
   
   public void reinitDisp() {
      display.reinitialize();
      setCurrentPane(DISPLAY);
   }
   
   public void dispose() {
	   display.hardStop();
	   display = null;
	   input = null;
   }
   
   public void resumeDisp() {
      display.resume();
      setCurrentPane(DISPLAY);
   }
   public Agenda getParent() {
      return parent;
   }
   
   public void setParent(Agenda parent) {
      this.parent = parent;
   }
   
   protected void finalize() {
      display = null;
      input = null;
      bar = null;
   }
}