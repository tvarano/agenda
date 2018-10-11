//Thomas Varano
//Sep 4, 2018

package com.varano.ui;

import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.PreferencesEvent;
import java.awt.desktop.PreferencesHandler;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.varano.information.constants.ErrorID;
import com.varano.information.constants.Rotation;
import com.varano.information.constants.RotationConstants;
import com.varano.managers.Agenda;
import com.varano.managers.EmailHandler;
import com.varano.managers.FileHandler;
import com.varano.managers.PanelManager;
import com.varano.resources.Addresses;
import com.varano.resources.ioFunctions.ImportExportHandler;
import com.varano.resources.ioFunctions.SchedWriter;
import com.varano.ui.input.GPAInput;

public class MenuBarHandler {
	private static boolean debug = false;
	
	
   public static MenuBar configureMenuBar(JFrame frame, Agenda age) {
   		Agenda.log("CONFIGURING MENU BAR");
      if (Desktop.isDesktopSupported()) {
         Desktop.getDesktop().setAboutHandler(new AboutHandler() {
            @Override
            public void handleAbout(AboutEvent arg0) {
               UIHandler.showAbout();
            }
         });
         Desktop.getDesktop().setPreferencesHandler(new PreferencesHandler() {
            @Override
            public void handlePreferences(PreferencesEvent arg0) {
               showPreferences(age);
            } 
         });
      }
      MenuBar bar = new MenuBar();
      Menu m;
      
      /*//not using the time bar anymore as of 1.8f
      //---------------------------Time Bar--------------------------
      m = new Menu("Time Left In Class: ");
      bar.add(m);
      */
      //---------------------------File Bar--------------------------
      m = new Menu("File");
           
      MenuItem mi = m.add(new MenuItem("Input Schedule"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            Agenda.log("Input Schedule MenuBar Button Clicked");
            age.getManager().setCurrentPane(PanelManager.INPUT);
         }
      }); 
      mi.setShortcut(new MenuShortcut(KeyEvent.VK_I));
      
      if (GPAInput.show) {
	      mi = m.add(new MenuItem("View GPA"));
	      mi.addActionListener(new ActionListener() {
	         @Override
	         public void actionPerformed(ActionEvent arg0) {
	            Agenda.log("GPA MenuBar Button Clicked");
	            age.getManager().setCurrentPane(PanelManager.GPA);
	         }
	      });
	      mi.setShortcut(new MenuShortcut(KeyEvent.VK_G));
      }
      
      //Placebo button. It autosaves
      mi = m.add(new MenuItem("Save"));
      mi.setShortcut(new MenuShortcut(KeyEvent.VK_S));
      
      m.addSeparator();
      
      //to test day checking and rotation updating
      if (debug) {
         mi = m.add(new MenuItem("TEST DAYCHECK"));
         mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
               Agenda.log("Day Check Button Clicked");
               age.getManager().getDisplay().setLastRead(LocalDate.now().minusDays(1));
            }
         });
      }
      Menu rotations = (Menu) m.add(new Menu("Set Rotation"));
      for (int i = 0; i < RotationConstants.categorizedRotations().length; i++) {
         Menu rm = (Menu) rotations.add(new Menu(RotationConstants.categoryNames[i]));
         for (Rotation r : RotationConstants.categorizedRotations()[i]) {
            if (!r.equals(Rotation.INCORRECT_PARSE)) {
               MenuItem ri = rm.add(new MenuItem(RotationConstants.getName(r.getIndex())));
               ri.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent arg0) {
                     Agenda.log("Rotation "+ r + " requested through menu bar");
                     UIHandler.setRotation(age, r);
                  }
               });
            }
         }
      }
      mi = m.add(new MenuItem("Refresh"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            Agenda.log("REFRESH\n");
            age.getManager().reset(false);
         }
      });
      mi.setShortcut(new MenuShortcut(KeyEvent.VK_R));

      mi = m.add(new MenuItem("Hard Refresh"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            Agenda.log("HARD REFRESH\n");
            if (UIHandler.checkIntentions("Reread and refresh data.")) {
               age.getManager().setCurrentPane(PanelManager.DISPLAY);
               com.varano.information.constants.DayType.reread();
               Rotation.reread();
               age.getManager().getDisplay().hardRefresh();
            }
         }
      });
      mi.setShortcut(new MenuShortcut(KeyEvent.VK_R, true));
      
      mi = m.add(new MenuItem("Check for Updates"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            if (!com.varano.managers.UpdateHandler.updateInquiry())
               JOptionPane.showMessageDialog(null, "No Updates Available.", Agenda.APP_NAME,
                     JOptionPane.INFORMATION_MESSAGE, null);
         }
      });
      
      m.addSeparator();
      
      mi = m.add(new MenuItem("Clear Schedule"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (UIHandler.checkIntentions("Reset Your Schedule")) {
               SchedWriter s = new SchedWriter();
               s.write(RotationConstants.defaultSchedule());
               age.getManager().reset(false);
            }
         }
      });
      mi = m.add(new MenuItem("Clear Preferences"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (UIHandler.checkIntentions("Clear Preferences (Look and Theme).")) {
               try {
                  UIHandler.setLAF0(UIManager.getSystemLookAndFeelClassName());
                  FileHandler.write(UIHandler.themes[0], FileHandler.THEME_ROUTE);
                  FileHandler.write(UIManager.getSystemLookAndFeelClassName(), FileHandler.LAF_ROUTE);
                  UIHandler.setColors();
                  FileHandler.writeWelcomeTrue();
               } catch (IOException e1) {
                  ErrorID.showError(e1, true);
               }
            }
         }
      });
      
      m.addSeparator();
      
      // NOTE this will overwrite the actual schedule being used. make sure to warn.
      mi = m.add(new MenuItem("Import..."));
      mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 0 for default, 1 for another
				int choice = JOptionPane.showOptionDialog(null,
						"Would you like to use the default\nschedule location or choose another schedule?",
						Agenda.APP_NAME + ": Choose Schedule", JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE, null, new String[] {"Default", "Another Schedule"}, "Default");
				
				if (choice == 0) 
					ImportExportHandler.reinitializeWith(age, FileHandler.DEFAULT_SCHED_ROUTE);
				else if (choice == 1)
					ImportExportHandler.reinitializeWith(age, ImportExportHandler.requestImportFileLocation());
				
			}	
      });
      
      
      mi = m.add(new MenuItem("Export..."));
      mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ImportExportHandler.export(age.getManager().getMainSched());
			}	
      });
      
      m.addSeparator();
      
      mi = m.add(new MenuItem("View File Location"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (JOptionPane.showOptionDialog(null, "Your files are kept at:\n"+FileHandler.ENVELOPING_FOLDER,
                  Agenda.APP_NAME + " File Location", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                  new String[] {"View In Finder", "Close"}, "Close") == 0) {
               FileHandler.openDesktopFile(FileHandler.ENVELOPING_FOLDER);
            }
         }
      });
      
      mi = m.add(new MenuItem("Open Log"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            FileHandler.openDesktopFile(FileHandler.LOG_ROUTE);
         }
      });

      
      mi = m.add(new MenuItem("View Source Code"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            FileHandler.openURI(Addresses.createURI(Addresses.SOURCE));
         }  
      });
      bar.add(m);
      
      //---------------------------View Bar--------------------------
      m = new Menu("View");
      mi = m.add(new MenuItem("Get Current Theme"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, "Your Current Theme Is:\n"+UIHandler.readDoc("theme.txt", UIHandler.THEME_ID),
                  Agenda.APP_NAME, JOptionPane.INFORMATION_MESSAGE, null);
         }
      });
      mi = m.add(new MenuItem("Get Current Look"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, "Your Current Look Is:\n"+UIManager.getLookAndFeel().getName(),
                  Agenda.APP_NAME, JOptionPane.INFORMATION_MESSAGE, null);
         }
      });
      
      Menu themes = (Menu)m.add(new Menu("Set Theme..."));
      for (String str : UIHandler.themes) {
         themes.add(new ThemeChooser(str, age));
      }
      Menu looks = (Menu)m.add(new Menu("Set Look..."));
      for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels())
         if (!laf.getName().equals("Nimbus"))
            looks.add(new LookChooser(laf, age));
      bar.add(m);
      //---------------------------Link Bar--------------------------
      m = new Menu("Useful Links");
      m.add(new LinkChooser("Canvas", Addresses.createURI(Addresses.CANVAS)));
      m.add(new LinkChooser("Genesis", Addresses.createURI(Addresses.GENESIS)));
      m.add(new LinkChooser("PHHS Home", Addresses.createURI(Addresses.PHHS_HOME)));
      m.add(new LinkChooser("Naviance", Addresses.createURI(Addresses.NAVIANCE)));
      m.add(new LinkChooser("Agenda Source", Addresses.createURI(Addresses.SOURCE)));
      m.add(new LinkChooser("Rotation Calendar", Addresses.createURI(Addresses.CALENDAR_URL)));
      
      bar.add(m);
      // ---------------------------Help Bar--------------------------
      m = new Menu("Help");
      mi = m.add(new MenuItem("Agenda News"));
      mi.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            UIHandler.showNews();
         }
      });
      
      mi = m.add(new MenuItem("Error Help"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            int choice = JOptionPane.showOptionDialog(null,
                  "Error logging helps the efficiency and ease of use for \n"
                        + "this program. Logs are kept at:\n"
                        + FileHandler.LOG_ROUTE + "\n"
                        + "and keep internal information about the program as it runs.\n"
                        + "If an error occurs, its message will be printed in the log.\n"
                        + "The best thing to do is simply send the entire log when this\n"
                        + "occurs. It gives the most information possible and will allow\n"
                        + "for the error to be fixed most quickly.\n"
                        + "Email the log to "+Addresses.CONTACT_EMAIL + "\n"
                        + "or submit the problem using Help > Submit Issue",
                  Agenda.APP_NAME, JOptionPane.DEFAULT_OPTION,
                  JOptionPane.INFORMATION_MESSAGE, null,
                  new String[]{"Close", "Open Log", "Send Email"}, "Close");
            if (choice == 2)
               EmailHandler.showSendPrompt();
            else if (choice == 1)
               FileHandler.openDesktopFile(FileHandler.LOG_ROUTE);
         }
      });
      mi = m.add(new LinkChooser("Submit Issue", Addresses.createURI(Addresses.GITHUB_ISSUES)));
      
      //if the program is a full release, there is no need for sharing protocol, as users will just get the program from self-service
      if (!Agenda.fullRelease) {
         mi = m.add(new MenuItem("Sharing Protocol"));
         mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               JOptionPane.showMessageDialog(null, 
                     "To share this application, please share the entire folder\n"
                     + "this application came in. The program comes with a README\n"
                     + "file, which will help users who do not have all the \n"
                     + "necessary items on their computer for running this program.",
                     Agenda.APP_NAME, JOptionPane.INFORMATION_MESSAGE, null);
            }
         });
         
         m.addSeparator();
         
         mi = m.add(new MenuItem("Installation Instructions"));
         mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               FileHandler.transfer("Installation Instructions.txt", 
                     new File(System.getProperty("user.home") + "/Desktop/README.txt"), 0);
               JOptionPane.showMessageDialog(null, 
                     "Installation instructions (README.txt) have been created on your desktop.",
                           Agenda.APP_NAME, JOptionPane.INFORMATION_MESSAGE, null);
            }
         });
      }
      
      m.addSeparator();
      
      mi = m.add(new MenuItem("Contact"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            EmailHandler.showSendPrompt();
         }
      });
      bar.setHelpMenu(m);
      
      frame.setMenuBar(bar);
      if (debug) System.out.println("BARUI "+ bar);
      System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Agenda"); 
      return bar;
	}
   
   
	private static class LinkChooser extends MenuItem {
      private static final long serialVersionUID = 1L;

      public LinkChooser(String name, URI link) {
	      super(name);
	      addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               Agenda.log("opening uri "+name);
               FileHandler.openURI(link);
            }
	      });    
	   }
	}
	
	private static Border getUnFormattedTitleBorder(String title) {
	   return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2),
            title, TitledBorder.LEADING, TitledBorder.ABOVE_TOP, UIHandler.font, Color.BLACK);
	}
	
	private static void showPreferences(Agenda age) {
	   final int w = 300;
	   final int h = 300;
	   JFrame f = new JFrame("Preferences");
	   JPanel p = new JPanel(new BorderLayout());
	   p.setPreferredSize(new Dimension(w, h));
	   JPanel top = new JPanel();
	   JLabel l = new JLabel("Input your preferences"/*, com.varano.resources.ResourceAccess.getImage("Agenda Logo.png"), 
	         javax.swing.SwingConstants.LEADING*/);
	   l.setFont(UIHandler.font);
	   top.add(l);
	   top.setPreferredSize(new Dimension(w,30));
	   p.add(top, BorderLayout.NORTH);
	   JPanel content = new JPanel(new BorderLayout());
	   
	   JPanel center = new JPanel(new GridLayout(1, 2));
	   JList<LookChooser> lookList = new JList<LookChooser>();
	   lookList.setBorder(getUnFormattedTitleBorder("Look and Feel"));
	   DefaultListModel<LookChooser> lookModel = new DefaultListModel<LookChooser>();
	   lookList.setForeground(Color.BLACK);
	   lookList.setSelectionBackground(Color.DARK_GRAY);
	   lookList.setSelectionForeground(Color.WHITE);
      lookList.setModel(lookModel);
	   for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels())
         if (!laf.getName().equals("Nimbus")) {
            LookChooser lc = new LookChooser(laf, age);
            lookModel.addElement(lc);
            if (laf.getName().equals(UIManager.getLookAndFeel().getName()))
               lookList.setSelectedValue(lc, true);
         }
	   center.add(lookList);
	   
	   JList<ThemeChooser> themeList = new JList<ThemeChooser>();
	   themeList.setForeground(Color.BLACK);
	   themeList.setSelectionBackground(Color.DARK_GRAY);
	   themeList.setSelectionForeground(Color.WHITE);
	   themeList.setBorder(getUnFormattedTitleBorder("Theme"));
	   DefaultListModel<ThemeChooser> themeModel = new DefaultListModel<ThemeChooser>();
	   themeList.setModel(themeModel);
	   for (String s : UIHandler.themes) {
	      ThemeChooser tc = new ThemeChooser(s, age);
	      themeModel.addElement(tc);
	      if (UIHandler.readDoc("theme.txt", UIHandler.THEME_ID).equals(s))
	         themeList.setSelectedValue(tc, true);
	   }
	   center.add(themeList);
	   content.add(center, BorderLayout.CENTER);
	   
	   JCheckBox notifCheck = new JCheckBox("Show Notifications");
	   notifCheck.setSelected(age.shouldShowNotif());
	   notifCheck.setFont(UIHandler.getButtonFont());
	   notifCheck.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	         age.setShowNotif(notifCheck.isSelected());
	         Agenda.log("show notifications toggled to " + notifCheck.isSelected());
	      }
	   });
	   content.add(notifCheck, BorderLayout.SOUTH);
	   p.add(content, BorderLayout.CENTER);
	   
	   JPanel bottom = new JPanel(new GridLayout(1,2));
	   JButton b = new JButton("Close");
	   b.setCursor(new Cursor(Cursor.HAND_CURSOR));
	   b.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            f.dispose();
         }
	   });
	   bottom.add(b);
	   b = new JButton("Apply");
	   b.setCursor(new Cursor(Cursor.HAND_CURSOR));
	   b.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            lookList.getSelectedValue().performAction(age);;
            themeList.getSelectedValue().performAction(age);;
         }
      });
	   b.setSelected(true);
	   bottom.add(b);
	   
	   p.add(bottom, BorderLayout.SOUTH);
	   
	   f.getContentPane().add(p);
	   f.pack();
	   f.setLocationRelativeTo(age);
	   f.setResizable(false);
	   f.setVisible(true);
	}
	
	
	private static class LookChooser extends CheckboxMenuItem {
      private static final long serialVersionUID = 1L;
      private UIManager.LookAndFeelInfo look;

      public LookChooser(UIManager.LookAndFeelInfo look, Agenda a) {
         super(look.getName());
         this.look = look;
         addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					Agenda.log("look chooser "+look.getName() + " state changed.");
					performAction(a);
				}
         });
      }
      
      public void writeData() {
         try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(FileHandler.RESOURCE_ROUTE+"look.txt"));
            bw.write(look.getClassName());
            bw.close();
         } catch (IOException e1) {
            ErrorID.showError(e1, true);
         }
      }
      
      public void performAction(Agenda a) {
            writeData();
            UIHandler.setLAF0(look.getClassName());
            javax.swing.SwingUtilities.updateComponentTreeUI(a.getParent());
            a.validateLookChecks();
      }
      
      public String toString() {
         return look.getName();
      }
   }
	
	
	private static class ThemeChooser extends CheckboxMenuItem {
      private static final long serialVersionUID = 1L;
      private String themeName;

      public ThemeChooser(String themeName, Agenda a) {
	      super(themeName);
         this.themeName = themeName;
         addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent arg0) {
					performAction(a);
				}
         	
         });
      }
      public void performAction(Agenda a) {
         writeData();
         UIHandler.setColors();
         a.validateThemeChecks();
         a.repaint();         
      }
      public void writeData() {
         try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(FileHandler.THEME_ROUTE));
            bw.write(themeName);
            bw.close();
            
         } catch (IOException e1) {
            ErrorID.showError(e1, true);
         }
      }
      public String toString() {
         return themeName;
      }
	}
}
