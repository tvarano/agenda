package managers;
import java.awt.CardLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import display.DisplayMain;
import information.Schedule;
import input.InputMain;

//Thomas Varano
//[Program Descripion]
//Sep 20, 2017

public class PanelManager
{
   private Main parent;
   private DisplayMain display;
   private InputMain input;
   private MenuBar bar;
   public PanelManager(Main parent, MenuBar bar) { 
      setParent(parent);
      this.bar = bar;
      display = new DisplayMain(this);  display.setName("display");
      input = new InputMain(this);  input.setName("input");
      parent.setLayout(new CardLayout());
      parent.add(display, display.getName());
      parent.add(input, input.getName());
   }
   
   public void setCurrentPane(boolean inputting) {
      if (inputting)
          ((CardLayout) parent.getLayout()).show(parent, input.getName());
      else 
         ((CardLayout) parent.getLayout()).show(parent, display.getName());
   }
   
   public MenuBar getBar() {
      return bar;
   }
   
   public Menu getTimeMenu() {
      System.out.println("BAR"+bar);
      return bar.getMenu(0);
   }
   
   public void startInput(Schedule s) {
      display.stop();
      input.setBeginningSchedule(s);
      setCurrentPane(true);
   }
   
   public ActionListener changeView(boolean inputting) {
      return new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (inputting)
               startInput(display.getMainSched());
            else
               finishInputting();
         }
      };
   }
   
   public void finishInputting() {
      display.reinitialize();
      setCurrentPane(false);
   }
   
   public void closeInput() {
      display.resume();
      setCurrentPane(false);
   }
   public Main getParent() {
      return parent;
   }
   public void setParent(Main parent) {
      this.parent = parent;
   }
}