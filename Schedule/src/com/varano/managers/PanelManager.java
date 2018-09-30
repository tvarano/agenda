package com.varano.managers;
import java.awt.CardLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import com.varano.information.Schedule;
import com.varano.resources.ioFunctions.SchedReader;
import com.varano.resources.ioFunctions.SchedWriter;
import com.varano.ui.PanelView;
import com.varano.ui.UIHandler;
import com.varano.ui.display.DisplayMain;
import com.varano.ui.input.DataInput;
import com.varano.ui.input.GPAInput;

//Thomas Varano
//[Program Descripion]
//Sep 20, 2017

/**
 * Manages the state of the program. Fires the switching of panels and the
 * transfer of data between them.
 * 
 * @param parent
 *           the {@link Agenda} class responsible for instantiating this object.
 *           There is no scenario for making this class without a parent, so the
 *           parent should never be null.
 * @param bar
 *           The <code>MenuBar</code> created by {@link UIHandler} which holds
 *           many tools for the program
 */
public class PanelManager {
   private Agenda parent;
   private DisplayMain display;
   private DataInput input;
   private GPAInput gpa;
   private MenuBar bar;
   private int currentType;
   private PanelView currentView;
   public static final int DISPLAY = 0, INPUT = 1, GPA = 2;

   public PanelManager(Agenda parent, MenuBar bar) {
      setParent(parent);
      this.bar = bar;
      display = new DisplayMain(this);
      display.setName("display");
      input = new DataInput(this);
      input.setName("input");
      gpa = new GPAInput(this);
      gpa.setName("gpa");
      parent.setLayout(new CardLayout());
      parent.add(display, display.getName());
      parent.add(input, input.getName());
      parent.add(gpa, gpa.getName());
      currentView = display;
   }

   public Agenda getMain() {
      return parent;
   }
   
   public void setCurrentPane(int type) {
      if (currentType == type)
         return;
      currentView.save();
      if (type == INPUT) {
         currentView = input;
      } else if (type == GPA) {
         currentView = gpa;
      } else
         currentView = display;
      currentView.open();
      parent.show(currentView.getName());
      this.currentType = type;
   }

   public void reset() {
      if (!(currentView instanceof DisplayMain))
         if (!UIHandler.checkIntentions("Refresh the program.\nYou might lose unsaved data"))
            return;
      Agenda.log(currentView.getClass().getName() + " refreshed");
      currentView.refresh();
      currentView.revalidate();
   }
   
   public void repaint() {
      if (display != null) display.repaint();
      if (input != null) input.repaint();
      if (gpa != null) gpa.repaint();
   }

   public void update() {
      display.update();
   }
   
   public MenuBar getBar() {
      return bar;
   }
   public DisplayMain getDisplay() {
      return display;
   }
   /**
    * No longer using the time menu
    * @return the Menu on the bar that holds the time left in classes.
    */
   @Deprecated(since = "1.8")
   public Menu getTimeMenu() {
      return bar.getMenu(UIHandler.timeBarIndex());
   }
   public void beforeClose() throws java.util.concurrent.CancellationException {
      if (currentView instanceof com.varano.ui.input.InputManager) {
         if (!((com.varano.ui.input.InputManager) currentView).isSaved()) {
         		int save = askSave();
         		if (save == -1) 
         			throw new java.util.concurrent.CancellationException("Save Cancelled");
         		if (save == 1) {
	            Agenda.log("DO NOT SAVE");
	            return;
         		}
         }
      }
      currentView.save();
   }
   
   /**
    * @return 0 for yes, 1 for no, -1 for no decision
    */
   public static int askSave() {
      return JOptionPane.showOptionDialog(null, "Do you want to save?",
            Agenda.APP_NAME, JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE, null,
            null, null);
   }

   public com.varano.information.Schedule getMainSched() {
      return new SchedReader().readSched();
   }

   public com.varano.information.constants.Rotation getTodayR() {
      return display.getTodayR();
   }

   public void setRotation(com.varano.information.constants.Rotation r) {
      display.setTodayR(r);
   }

   public ActionListener changeViewListener(int parentType) {
      return new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            Agenda.log("view changed. type = " + parentType);
            setCurrentPane(parentType);
         }
      };
   }
   
   public void dispose() {
      display.hardStop();
      display = null;
      input = null;
   }

   public Agenda getParent() {
      return parent;
   }
   public void setParent(Agenda parent) {
      this.parent = parent;
   }
   public void saveSchedule(Schedule s, Class<?> caller) {
      Agenda.log("NEW schedule save called by " + caller.getSimpleName());
      new SchedWriter().write(s);
   }
   public void saveSchedule(Class<?> caller) {
      Agenda.log("**main schedule save called by " + caller.getSimpleName());
      display.writeMain();
   }
}