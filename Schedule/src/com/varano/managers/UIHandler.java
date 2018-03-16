package com.varano.managers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
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
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.zip.DataFormatException;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import com.varano.information.constants.ErrorID;
import com.varano.information.constants.Rotation;
import com.varano.information.constants.RotationConstants;
import com.varano.resources.Addresses;
import com.varano.resources.ResourceAccess;
import com.varano.resources.ioFunctions.SchedWriter;
import com.varano.ui.MutableColor;

//Thomas Varano

/**
 * Handles all UI necessities, including LAF and theme. Uninstantiatable and unextendable. Only for static calls.
 * 
 * @author Thomas Varano
 */
public final class UIHandler {

   private static final int THEME_ID = 0, LAF_ID = 1;
   
	public static final Font font = new Font("Futura", Font.PLAIN, 16);
	private static boolean debug;
	
	/**
	 * must be done for any other use of this class. initializes all variables
	 */
	public static void init() { 
	   debug = false;
	   setLAF();
	   initColors();
	   setColors();
	   putValues();
	}
	
	public static void putValues() {
	   UIManager.put("List.selectionBackground", tertiary);
	   UIManager.put("List.selectionForeground", foreground);
	   UIManager.put("List.foreground", foreground);
      UIManager.put("TabbedPane.selected", quaternary);
      UIManager.put("TabbedPane.selectHighlight", quaternary);
      UIManager.put("TabbedPane.foreground", foreground);
      UIManager.put("TabbedPane.insets", secondary);
	   UIManager.put("ToolTip.font", getToolTipFont());
	   UIManager.put("ToolTip.background", background.brighter());
	   UIManager.put("ToolTip.foreground", foreground);
	   UIManager.put("Button.disabledText", secondary);
	   UIManager.put("OptionPane.font", getButtonFont());
	   UIManager.put("OptionPane.errorIcon", ResourceAccess.getImage("ErrorIcon.png"));
	   UIManager.put("OptionPane.warningIcon", ResourceAccess.getImage("WarningIcon.png"));
	   UIManager.put("OptionPane.informationIcon", ResourceAccess.getImage("InfoIcon.png"));
	   UIManager.put("OptionPane.questionIcon", ResourceAccess.getImage("QuestionIcon.png"));
	   
	   final int divThickness = 4;
	   UIManager.put("SplitPane.background", background);
      UIManager.put("SplitPaneDivider.border", BorderFactory.createLineBorder(titleBorderColor, divThickness));
      UIManager.put("SplitPaneDivider.draggingColor", titleBorderColor.darker());
      UIManager.put("SplitPane.dividerSize", divThickness);
	   }
	
	
	private static class ThemeChooser extends MenuItem {
      private static final long serialVersionUID = 1L;
      private String themeName;

      public ThemeChooser(String themeName, Agenda a) {
	      super(themeName);
         this.themeName = themeName;
         addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               performAction(a);
            }
         });
      }
      public void performAction(Agenda a) {
         writeData();
         setColors();
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

	private static class LookChooser extends MenuItem {
      private static final long serialVersionUID = 1L;
      private UIManager.LookAndFeelInfo look;

      public LookChooser(UIManager.LookAndFeelInfo look, Agenda a) {
         super(look.getName());
         this.look = look;
         addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            setLAF0(look.getClassName());
            javax.swing.SwingUtilities.updateComponentTreeUI(a.getParent());
      }
      public String toString() {
         return look.getName();
      }
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
            title, TitledBorder.LEADING, TitledBorder.ABOVE_TOP, font, Color.BLACK);
	}
	
	private static void showPreferences(Agenda age) {
	   final int w = 300;
	   final int h = 300;
	   JFrame f = new JFrame("Preferences");
	   JPanel p = new JPanel();
	   p.setPreferredSize(new Dimension(w, h));
	   p.setLayout(new BorderLayout());
	   JPanel top = new JPanel();
	   JLabel l = new JLabel("Input your preferences"/*, com.varano.resources.ResourceAccess.getImage("Agenda Logo.png"), 
	         javax.swing.SwingConstants.LEADING*/);
	   l.setFont(font);
	   top.add(l);
	   top.setPreferredSize(new Dimension(w,30));
	   p.add(top, BorderLayout.NORTH);
	   
	   JPanel center = new JPanel();
	   center.setLayout(new GridLayout(1, 2));
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
	   for (String s : themes) {
	      ThemeChooser tc = new ThemeChooser(s, age);
	      themeModel.addElement(tc);
	      if (readDoc("theme.txt", THEME_ID).equals(s))
	         themeList.setSelectedValue(tc, true);
	   }
	   center.add(themeList);
	   p.add(center, BorderLayout.CENTER);
	   
	   JPanel bottom = new JPanel();
	   bottom.setLayout(new GridLayout(1,2));
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
	   f.setVisible(true);
	   f.setResizable(false);
	}
	
	/**
	 * the themes available for the application
	 */
	public static final String[] themes = {"Clean (Default)", "Night Mode", "Neutral", "Muted", "Colorful", "Minimal", "Bare"}; 

	/**
	 * asks if the user would wish to continue to pursue the action specified
	 * @param action a string which specifies the action which will be taken
	 * @return true if the user wishes to continue
	 */
   public static boolean checkIntentions(String action) {
      return (JOptionPane.showOptionDialog(null,
            "You are about to:\n" + action
                  + ".\nAre you sure you want to do this?",
            Agenda.APP_NAME + " WARNING", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE, null, null, null) == 0);
   }
   
   public static void showWelcome() {
      String html = "<html> <h1> Welcome to " + Agenda.APP_NAME + " </h1>"
            + "<h2>Version " + Agenda.BUILD + "</h2> "
            + "<p>***Program is still in beta. Please report all errors / bugs by emailing me the log"
            + "<br>at " + FileHandler.LOG_ROUTE + ""
            + "<br>email log or any ideas to " + Addresses.CONTACT_EMAIL + "***"
            + "<br>You can use this program to keep track of classes, schedules, assignments, or grades"
            + "<br>in Pascack Hills or Valley."
            + "<ul>"
            + "<li>On the home screen, you can see the current class's data "
            + "<br>on the top panel and view other classes' data and memos in the bottom panel</li>"
            + "<li>To edit your classes, click File > Input Schedule.</li>"
            + "<li>To edit grades, click File > View GPA.</li>"
            + "<li>To change the look of the program, either go to preferences (\u2318 + ,) or the View Menu"
            + "</ul>"
            + "<p>Feel free to look at the source code (Useful Links > Agenda Source) to suggest any improvements."
            + "<br> - Thomas Varano"
            + "</html>";
      javax.swing.JEditorPane content = new javax.swing.JEditorPane("text/html", html);
      content.setFont(font);
      HTMLEditorKit kit = new HTMLEditorKit();
      content.setEditorKit(kit);
      kit.getStyleSheet().addRule("body {color:#000; font-family:"
            + font.getFamily() + "; margin: 4px; }");

      Document doc = kit.createDefaultDocument();
      content.setDocument(doc);
      content.setText(html);
      content.setOpaque(false);
      JOptionPane.showMessageDialog(null, content, "Welcome",
            JOptionPane.INFORMATION_MESSAGE,
            ResourceAccess.getImage("Agenda Logo.png"));
   }
   
   public static void showAbout() {
      String html = "<html> <h1> " + Agenda.APP_NAME + " </h1> <h2>Version " + Agenda.BUILD + "</h2> "
            + "<h3>" + Agenda.LAST_UPDATED + "</h3>"
            + "<p>***Program is still in beta. Please report all errors / bugs by emailing me the log"
            + "<p>at "+FileHandler.LOG_ROUTE
            + "<p>email at "+Addresses.CONTACT_EMAIL + "***"
            + "<p>Agenda is a schedule program for the Pascack Valley High School District"
            + "<p>that can keep track of time, school schedules, assignments, and GPA"
            + "<p>for students."
            + "<br><br>"
            + "<h2>CREDITS:"
            + "<h3>Thomas Varano : Author"
            + "<br><br>Viktor Nakev : Icon Designer"
            + "<br><br>Matthew Ghedduzi : Alpha Tester"
            + "<br><br>Michael Ruberto : Conceptual Designer</html>";
      javax.swing.JEditorPane content = new javax.swing.JEditorPane("text/html", html);
      content.setFont(font);
      HTMLEditorKit kit = new HTMLEditorKit();
      content.setEditorKit(kit);
      kit.getStyleSheet().addRule("body {color:#000; font-family:"+font.getFamily()+"; margin: 4px; }");

      Document doc = kit.createDefaultDocument();
      content.setDocument(doc);
      content.setText(html);
      content.setOpaque(false);
      JOptionPane.showMessageDialog(null, 
            content, "About " + Agenda.APP_NAME, 
            JOptionPane.INFORMATION_MESSAGE, ResourceAccess.getImage("Agenda Logo.png"));
   }

   public static void setRotation(Agenda age, com.varano.information.constants.Rotation r) {
      age.getManager().setRotation(r);
   }
   
   public synchronized static MenuBar configureMenuBar(JFrame frame, Agenda age) {
      if (Desktop.isDesktopSupported()) {
         Desktop.getDesktop().setAboutHandler(new AboutHandler() {
            @Override
            public void handleAbout(AboutEvent arg0) {
               showAbout();
            }
         });
         Desktop.getDesktop().setPreferencesHandler(new PreferencesHandler() {
            @Override
            public void handlePreferences(PreferencesEvent arg0) {
               showPreferences(age);
            } 
         });
      }
	   //---------------------------Time Bar--------------------------
      MenuBar bar = new MenuBar();
      Menu m = new Menu("Time Left In Class: ");
      bar.add(m);
      
      //---------------------------File Bar--------------------------
      m = new Menu("File");
           
      MenuItem mi = m.add(new MenuItem("Input Schedule"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            age.getManager().setCurrentPane(PanelManager.INPUT);
         }
      }); 
      mi.setShortcut(new MenuShortcut(KeyEvent.VK_I));
      
      mi = m.add(new MenuItem("View GPA"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            age.getManager().setCurrentPane(PanelManager.GPA);
         }
      });
      mi.setShortcut(new MenuShortcut(KeyEvent.VK_G));
      //to test day checking
      if (debug) {
         mi = m.add(new MenuItem("TEST DAYCHECK"));
         mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
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
                     setRotation(age, r);
                  }
               });
            }
         }
      }
      mi = m.add(new MenuItem("Refresh"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            Agenda.log("REFRESH");
            age.getManager().reset();
         }
      });
      mi.setShortcut(new MenuShortcut(KeyEvent.VK_R));

      mi = m.add(new MenuItem("Hard Refresh"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            Agenda.log("HARD REFRESH");
            if (checkIntentions("Reread and refresh data.")) {
               age.getManager().setCurrentPane(PanelManager.DISPLAY);
               com.varano.information.constants.DayType.reread();
               Rotation.reread();
               age.getManager().getDisplay().hardRefresh();
            }
         }
      });
      mi.setShortcut(new MenuShortcut(KeyEvent.VK_R, true));
      
      m.addSeparator();
      
      mi = m.add(new MenuItem("Clear Schedule"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (checkIntentions("Reset Your Schedule")) {
               SchedWriter s = new SchedWriter();
               s.write(RotationConstants.defaultSchedule());
               age.getManager().reset();
            }
         }
      });
      mi = m.add(new MenuItem("Clear Preferences"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (checkIntentions("Clear Preferences (Look and Theme).")) {
               BufferedWriter bw;
               try {
                  setLAF0(UIManager.getSystemLookAndFeelClassName());
                  bw = new BufferedWriter(new FileWriter(FileHandler.THEME_ROUTE));
                  bw.write(themes[0]);
                  bw.close();
                  bw = new BufferedWriter(new FileWriter(FileHandler.LAF_ROUTE));
                  bw.write(UIManager.getSystemLookAndFeelClassName());
                  bw.close();
                  setColors();
               } catch (IOException e1) {
                  ErrorID.showError(e1, true);
               }
            }
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
            JOptionPane.showMessageDialog(null, "Your Current Theme Is:\n"+readDoc("theme.txt", THEME_ID),
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
               FileHandler.sendEmail();
            else if (choice == 1)
               FileHandler.openDesktopFile(FileHandler.LOG_ROUTE);
         }
      });
      mi = m.add(new LinkChooser("Submit Issue", Addresses.createURI(Addresses.GITHUB_ISSUES)));
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
      
      m.addSeparator();
      
      mi = m.add(new MenuItem("Contact"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            FileHandler.sendEmail();
         }
      });
      bar.setHelpMenu(m);
      frame.setMenuBar(bar);
      if (debug) System.out.println("BARUI "+ bar);
      System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Agenda"); 
      return bar;
	}
   
   public static MouseListener buttonPaintListener(AbstractButton parent) {
      return new MouseListener() {
         @Override public void mouseClicked(MouseEvent e) {}
         @Override public void mousePressed(MouseEvent e) {}
         @Override public void mouseReleased(MouseEvent e) {}
         @Override
         public void mouseEntered(MouseEvent e) {
            parent.setBorderPainted(true);
            parent.setForeground(Color.BLACK);
         }
         @Override
         public void mouseExited(MouseEvent e) {
            parent.setBorderPainted(false);
            parent.setForeground(foreground);
         }
      };
   }
   
   private static String readDoc(String fileName, int type) {
      try {
         Scanner s = new Scanner(new File(FileHandler.RESOURCE_ROUTE+fileName));
         String ret = s.nextLine();
         s.close();
         return ret;
         
      } catch (IOException e) {
         if (e instanceof FileNotFoundException) {
            try {
               BufferedWriter bw = new BufferedWriter(new FileWriter(new File(FileHandler.RESOURCE_ROUTE+fileName)));
               if (type == THEME_ID)
                  bw.write(themes[0]);
               else if (type == LAF_ID)
                  bw.write(UIManager.getSystemLookAndFeelClassName());
               else {
                  ErrorID.showError(new DataFormatException("type "+type+" is undefined for writing a UI trait"), true);
                  bw.write("");
               }
               bw.close();
            } catch (IOException e1) {
               e1.printStackTrace();
               FileHandler.initAndCreateFiles();
            }
         }
         return themes[0];
      }
   }
	
	/**
    * used for text
    */
   public static MutableColor foreground;
   /**
    * used for a vast majority of backgrounds, is the solid back
    */
   public static MutableColor background;
   /**
    * used for the borders of titled borders. 
    */
   public static MutableColor titleBorderColor;
   /**
    * used as background of list and disabled buttons
    */
   public static MutableColor secondary;
   /**
    * used for highlights - JMenubar, selected items in list, etc
    */
   public static MutableColor tertiary;
   /**
    * background for info panes, tabs, etc
    */
   public static MutableColor quaternary;
   /**
    * used for the titles of titled borders.
    */
   public static MutableColor titleColor;
   
   public static void initColors() {
      foreground = new MutableColor();
      background = new MutableColor();
      secondary = new MutableColor();
      tertiary = new MutableColor();
      quaternary = new MutableColor();
      titleColor = new MutableColor();
      titleBorderColor = new MutableColor();
   }
   
	public static void setColors() {
	   String theme = themes[0];
	   String str = readDoc("theme.txt", THEME_ID);
	   for (String th : themes)
         if (str.equals(th))
            theme = str;
	   Color text = null;
	   if (theme.equals(themes[1])) {
	      // night mode
	      text = new Color(238, 238, 238);
	      Color noir = new Color(Integer.decode("#1d2731"));
	      Color navy = new Color(25,40,55);
	      
	      Color royal = new Color(0,144,221);
	      Color gray = new Color(Integer.decode("#5a666b"));
	      Color indigo = new Color(13, 74, 108);
	      
	      background.setValue(noir);
	      secondary.setValue(gray);
	      tertiary.setValue(navy);
	      quaternary.setValue(navy);
	      titleColor.setValue(royal);
	      titleBorderColor.setValue(indigo);
	   } else if (theme.equals(themes[2])) {
	      // neutral
	      Color noir = new Color(Integer.decode("#1d2731"));
	      Color gray = new Color(Integer.decode("#757575"));
	      Color carbon = new Color(Integer.decode("#a9a9a9"));
	      text = noir;
	      Color neutral = new Color(Integer.decode("#efefef"));
	      
	      background.setValue(neutral);
	      secondary.setValue(carbon);
	      tertiary.setValue(gray);
	      quaternary.setValue(carbon);
	      titleColor.setValue(noir);
	      titleBorderColor.setValue(noir);
	   } else if (theme.equals(themes[3])) {
	     // muted
	      text = new Color(Integer.decode("#373737"));
	      Color paleGold = new Color(Integer.decode("#c0b283"));
	      Color silk = new Color(Integer.decode("#dcd0c0"));
	      Color paper = new Color(Integer.decode("#f4f4f4"));
	      
	      background.setValue(paper);
	      secondary.setValue(silk);
	      tertiary.setValue(paleGold);
	      quaternary.setValue(paleGold);
	      titleColor.setValue(text);
	      titleBorderColor.setValue(silk);
	   } else if (theme.equals(themes[4])) {
	      // colorful
	      text = new Color(Integer.decode("#373737"));
	      Color salmon = new Color(Integer.decode("#ff6f78"));
	      Color rain = new Color(Integer.decode("#6ec4db"));
	      Color buttermilk = new Color(Integer.decode("#fff7c0"));
	      Color leaf = new Color(Integer.decode("#66a8bc"));
	      
	      background.setValue(buttermilk);
	      secondary.setValue(rain);
	      tertiary.setValue(salmon);
	      quaternary.setValue(salmon);
	      titleColor.setValue(text);
	      titleBorderColor.setValue(leaf);
	   } else if (theme.equals(themes[5])) {
	      // minimal
	      text = Color.BLACK;
	      Color tangerine = new Color(Integer.decode("#FFCCBC"));
	      Color tropacana = new Color(Integer.decode("#FF8A65"));
	      Color chalk = new Color(Integer.decode("#F5F5F5"));
	      
	      background.setValue(chalk);
	      secondary.setValue(chalk);
	      tertiary.setValue(tropacana);
	      quaternary.setValue(chalk);
	      titleColor.setValue(text);
         titleBorderColor.setValue(tangerine);
      } else if (theme.equals(themes[6])) {
         // bare
         text = Color.BLACK;
         Color neutral = new Color(Integer.decode("#efefef"));
         Color carbon = Color.LIGHT_GRAY;

         background.setValue(neutral);
         secondary.setValue(neutral);
         tertiary.setValue(carbon);
         quaternary.setValue(neutral);
         titleColor.setValue(text);
         titleBorderColor.setValue(carbon);
      } else {
         // default
         text = new Color(40, 40, 40);
         Color noir = new Color(Integer.decode("#1d2731"));
         Color carbon = new Color(Integer.decode("#a9a9a9"));
         Color sky = new Color(Integer.decode("#caebf2"));
         Color watermelon = new Color(Integer.decode("#ff6a5c"));
         Color neutral = new Color(Integer.decode("#efefef"));

         background .setValue(neutral);
         secondary.setValue(carbon);
         tertiary.setValue(watermelon);
         quaternary.setValue(sky);
         titleColor.setValue(noir);
         titleBorderColor.setValue(carbon);
      }
      if (debug) 
         System.out.println("uih 784 background: " + tertiary + " and location "
               + Integer.toHexString(tertiary.hashCode()));
	   foreground.setValue(text);
	}
	
	public static void setLAFOcean() {
      try {
         UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
         MetalLookAndFeel.setCurrentTheme(new OceanTheme());
         Agenda.log("LAF set: "+UIManager.getLookAndFeel().getID());
      } catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e1) {
         ErrorID.showError(e1, true);
      }
      if (debug) System.out.println("UI DONE");
	}
	
	private synchronized static void setLAF0(String name) {
	   try {
         UIManager.setLookAndFeel(name);
      } catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e) {
         ErrorID.showError(e, true);
      }
	}
	
	public synchronized static void setLAF() {
	   String name = readDoc("look.txt", LAF_ID);
	   setLAF0(name);
	}
	
	public static Border getTitledBorder(String title, int justification, int position) {
      return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(titleBorderColor, 2),
            title, justification, position, font, titleColor);
	}
	
	public static Border getTitledBorder(String title) {
	   return getTitledBorder(title, TitledBorder.LEADING, TitledBorder.ABOVE_TOP);
	}
	
	public static Font getInputLabelFont() {
	   return font.deriveFont(15F);
	}
	
	public static Font getInputFieldFont() {
	   return getInputLabelFont().deriveFont(Font.PLAIN);
	}
	
	public static Font getTabFont() {
	   return getButtonFont();
	}
	
	public static Font getButtonFont() {
	   return font.deriveFont(13F);
	}
	
	public static Font getToolTipFont() {
	   return font.deriveFont(13F);
	}
}
