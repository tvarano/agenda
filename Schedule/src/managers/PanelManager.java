package managers;
import java.awt.CardLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import display.DisplayMain;
import input.InputMain;

//Thomas Varano
//[Program Descripion]
//Sep 20, 2017

public class PanelManager
{
   private Agenda parent;
   private DisplayMain display;
   private InputMain input;
   private MenuBar bar;
   private boolean inputting;
   public PanelManager(Agenda parent, MenuBar bar) { 
      setParent(parent);
      this.bar = bar;
      display = new DisplayMain(this);  display.setName("display");
      input = new InputMain(this);  input.setName("input");
      parent.setLayout(new CardLayout());
      parent.add(display, display.getName());
      parent.add(input, input.getName());
   }
   
   public void setCurrentPane(boolean inputting) {
      if (this.inputting == inputting)
         return;
      if (inputting)
          ((CardLayout) parent.getLayout()).show(parent, input.getName());
      else 
         ((CardLayout) parent.getLayout()).show(parent, display.getName());
      this.inputting = inputting;
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
      if (inputting == true)
         return;
      display.stop();
      input.setBeginningSchedule(display.getMainSched());
      setCurrentPane(true);
   }
   
   public ActionListener changeView(boolean inputting) {
      return new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (Agenda.statusU) Agenda.log("view changed. inputting = "+inputting);
            if (inputting) 
               startInput();
            else
               finishInputting();
         }
      };
   }
   
   public void finishInputting() {
      display.reinitialize();
      setCurrentPane(false);
   }
   
   public void dispose() {
	   display.hardStop();
	   display = null;
	   input = null;
   }
   
   public void closeInput() {
      display.resume();
      setCurrentPane(false);
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