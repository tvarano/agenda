package managers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.util.Scanner;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import constants.ErrorID;
import constants.RotationConstants;
import ioFunctions.SchedWriter;

//Thomas Varano
public class UIHandler {

   private static final int THEME_ID = 0, LAF_ID = 1;
   
	public static Font font;
	public static String fontFam;
	private static boolean debug;
	
	public static void init() { 
	   debug = false;
	   font = new Font("Georgia", Font.PLAIN, 16);
	   fontFam = "Georgia";
//	   setLAFOcean();
	   setLAF();
	   setColors();
	   putValues();
	}
	
   public static JPanel getLoadingPanel() {
      JPanel retval = new JPanel() {
         private static final long serialVersionUID = 1L;
         
         @Override
         protected void paintComponent(Graphics g) {
            System.out.println("CALLED PAINT");
            setBackground(Color.BLUE);
            super.paintComponent(g);
         }
         public Dimension getMinimumSize() {
            return new Dimension(Agenda.MIN_W,Agenda.MIN_H);
         }
         public Dimension getPreferredSize() {
            return new Dimension(Agenda.PREF_W, Agenda.PREF_H);
         }
      };
      retval.setVisible(true);
      retval.repaint();
      return retval;
   }
	
   public static JFrame createEmptyLoad() {
      return new JFrame(Agenda.APP_NAME + " " + Agenda.BUILD);
   }
   
	public static JFrame createLoadingScreen(JFrame f) {
	   f.setTitle(Agenda.APP_NAME + " " + Agenda.BUILD);
	   JPanel p = getLoadingPanel();
	   f.getContentPane().add(p);
	   f.setMinimumSize(new Dimension(Agenda.MIN_W, Agenda.MIN_H));
	   f.pack();
	   f.setLocationRelativeTo(null);
	   f.setVisible(true);
	   return f;
	}
	
	public static void putValues() {
	   
//	   UIManager.put("ScrollBarUI", "managers.CustomScrollUI"); 
//	   UIManager.put("ScrollBar.width", 12);
	   UIManager.put("List.selectionBackground", tertiary);
	   UIManager.put("List.selectionForeground", foreground);
	   UIManager.put("List.foreground", foreground);
      UIManager.put("TabbedPane.selected", quaternary);
      UIManager.put("TabbedPane.selectHighlight", quaternary);
      UIManager.put("TabbedPane.foreground", foreground);
      UIManager.put("TabbedPane.insets", secondary);
	   UIManager.put("ToolTip.font", getToolTipFont());
	   UIManager.put("Button.disabledText", secondary);
	   UIManager.put("OptionPane.font", getButtonFont());
	}
	
	//TODO menu ideas... change color scheme, maybe font.
	// have a help tab which would describe where the log is and my email
	
	private static class ThemeChooser extends MenuItem {
      private static final long serialVersionUID = 1L;

      public ThemeChooser(String themeName) {
	      super(themeName);
	      addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               try {
                  BufferedWriter bw = new BufferedWriter(new FileWriter(Agenda.RESOURCE_ROUTE+"theme.txt"));
                  bw.write(themeName);
                  bw.close();
                  if (JOptionPane.showOptionDialog(null,
                        "Changing the theme requires a restart", Agenda.APP_NAME,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null,
                        new String[]{"Restart", "Close"}, "Restart") == 0)
                     Agenda.restart();
               } catch (IOException e1) {
                  ErrorID.showError(e1, true);
               }
            } 
	      });
	   }
	}

	private static class LookChooser extends MenuItem {
      private static final long serialVersionUID = 1L;

      public LookChooser(UIManager.LookAndFeelInfo look) {
         super(look.getName());
         addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               try {
                  BufferedWriter bw = new BufferedWriter(new FileWriter(Agenda.RESOURCE_ROUTE+"look.txt"));
                  bw.write(look.getClassName());
                  bw.close();
                  if (JOptionPane.showOptionDialog(null,
                        "Changing the look requires a restart", Agenda.APP_NAME,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null,
                        new String[]{"Restart", "Close"}, "Restart") == 0)
                     Agenda.restart();
               } catch (IOException e1) {
                  ErrorID.showError(e1, true);
               }
            } 
         });
      }
   }
	
	/**
	 * the themes available for the application
	 */
	public static final String[] themes = {"Clean (Default)", "Night Mode", "Neutral", "Muted", "Colorful", "Minimal", "Bare"}; 

   private static boolean checkIntentions(String action) {
      return (JOptionPane.showOptionDialog(null,
            "You are about to:\n" + action
                  + ".\nAre you sure you want to do this?",
            Agenda.APP_NAME + " WARNING", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE, null, new String[]{"Yes", "No"},
            "No") == 0);
   }

   public synchronized static MenuBar configureMenuBar(JFrame frame) {
      MenuBar bar = new MenuBar();
      Menu m = new Menu("Time Left In Class: ");
      bar.add(m);
      
      //---------------------------File Bar--------------------------
      m = new Menu("File");
      m.addSeparator();
      MenuItem mi = m.add(new MenuItem("Reset Schedule"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (checkIntentions("Reset your schedule")) {
               SchedWriter s = new SchedWriter();
               s.write(RotationConstants.defaultSchedule());
               Agenda.restart();
            }
         }
      });
      mi = m.add(new MenuItem("Restart"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (checkIntentions("Restart the applicaiton"))
               Agenda.restart();
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
            JOptionPane.showMessageDialog(null, "Your Current Look Is:\n"+readDoc("look.txt", LAF_ID),
                  Agenda.APP_NAME, JOptionPane.INFORMATION_MESSAGE, null);
         }
      });
      
      Menu themes = (Menu)m.add(new Menu("Set Theme..."));
      for (String str : UIHandler.themes) {
         themes.add(new ThemeChooser(str));
      }
      Menu looks = (Menu)m.add(new Menu("Set Look..."));
      for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels())
         if (!laf.getName().equals("Nimbus"))
            looks.add(new LookChooser(laf));
      bar.add(m);
      
      
      //---------------------------Help Bar--------------------------
      m = new Menu("Help");
      mi = m.add(new MenuItem("Error Help"));
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, 
                  "Error logging helps the efficiency and ease of use for \n"
                  + "this program. Logs are kept at:\n"
                  + Agenda.LOG_ROUTE + "\n"
                  + "and keep internal information about the program as it runs.\n"
                  + "If an error occurs, its message will be printed in the log.\n"
                  + "The best thing to do is simply send the entire log when this\n"
                  + "occurs. It gives the most information possible and will allow\n"
                  + "for the error to be fixed most quickly.\n"
                  + "Email the log to varanoth@pascack.org", 
                  Agenda.APP_NAME, JOptionPane.INFORMATION_MESSAGE, null);
         }
      });
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
      bar.setHelpMenu(m);
      frame.setMenuBar(bar);
      if (debug) System.out.println("BARUI "+ bar);
      System.setProperty("com.apple.mrj.application.apple.menu.about.name", "WikiTeX"); 
      System.out.println(System.getProperty("com.apple.mrj.application.apple.menu.about.name"));
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
         Scanner s = new Scanner(new File(Agenda.RESOURCE_ROUTE+fileName));
         String ret = s.nextLine();
         s.close();
         return ret;
         
      } catch (IOException e) {
         if (e instanceof FileNotFoundException) {
            try {
               BufferedWriter bw = new BufferedWriter(new FileWriter(new File(Agenda.RESOURCE_ROUTE+fileName)));
               if (type == THEME_ID)
                  bw.write(themes[0]);
               else if (type == LAF_ID)
                  bw.write(UIManager.getSystemLookAndFeelClassName());
               else {
                  ErrorID.showError(new IllegalClassFormatException("type "+type+" is undefined for writing a UI trait"), true);
                  bw.write("");
               }
               bw.close();
            } catch (IOException e1) {
               Agenda.setFileLocation();
            }
         }
         return themes[0];
      }
   }
	
	/**
    * used for text
    */
   public static Color foreground;
   /**
    * used for a vast majority of backgrounds, is the solid back
    */
   public static Color background;
   /**
    * used for the borders of titled borders. 
    */
   public static Color titleBorderColor;
   /**
    * used as background of list and disabled buttons
    */
   public static Color secondary;
   /**
    * used for highlights - JMenubar, selected items in list, etc
    */
   public static Color tertiary;
   /**
    * background for info panes, tabs, etc
    */
   public static Color quaternary;
   /**
    * used for the titles of titled borders.
    */
   public static Color titleColor;
	
	public static void setColors() {
	   String theme = themes[0];
	   String str = readDoc("theme.txt", THEME_ID);
	   for (String th : themes)
         if (str.equals(th))
            theme = str;
	   //TODO account for themes
	   Color text = null;
	   if (theme.equals(themes[0])) {
	      //default
	      text = new Color(40,40,40);
      	   Color noir = new Color(Integer.decode("#1d2731"));
      	   Color carbon = new Color(Integer.decode("#a9a9a9"));
      	   Color sky = new Color(Integer.decode("#caebf2"));
      	   Color watermelon = new Color(Integer.decode("#ff6a5c"));
      	   Color neutral = new Color(Integer.decode("#efefef"));
      	   
      	   background = neutral;
      	   secondary = carbon;
      	   tertiary = watermelon;
      	   quaternary = sky;
      	   titleColor = noir;
      	   titleBorderColor = carbon;
	   } else if (theme.equals(themes[1])) {
	      //night mode
	      text = new Color(238, 238, 238);
	      Color noir = new Color(Integer.decode("#1d2731"));
	      Color navy = new Color(25,40,55);
	      
	      Color royal = new Color(0,144,221);
	      Color gray = new Color(Integer.decode("#5a666b"));
	      Color indigo = new Color(13, 74, 108);
	      
	      background = noir;
	      secondary = gray;
	      tertiary = navy;
	      quaternary = navy;
	      titleColor = royal;
	      titleBorderColor = indigo;
	   } else if (theme.equals(themes[2])) {
	      //neutral
	      Color noir = new Color(Integer.decode("#1d2731"));
	      Color gray = new Color(Integer.decode("#757575"));
	      Color carbon = new Color(Integer.decode("#a9a9a9"));
	      text = noir;
	      Color neutral = new Color(Integer.decode("#efefef"));
	      
	      background = neutral;
	      secondary = carbon;
	      tertiary = gray;
	      quaternary = carbon;
	      titleColor = noir;
	      titleBorderColor = noir;
	   } else if (theme.equals(themes[3])) {
	     //muted
	      text = new Color(Integer.decode("#373737"));
	      Color paleGold = new Color(Integer.decode("#c0b283"));
	      Color silk = new Color(Integer.decode("#dcd0c0"));
	      Color paper = new Color(Integer.decode("#f4f4f4"));
	      
	      background = paper;
	      secondary = silk;
	      tertiary = paleGold;
	      quaternary = paleGold;
	      titleColor = text;
	      titleBorderColor = silk;
	   } else if (theme.equals(themes[4])) {
	      //colorful
	      text = new Color(Integer.decode("#373737"));
	      Color sky = new Color(Integer.decode("#7cdbd5"));
	      Color brightCoral = new Color(Integer.decode("#f53240"));
	      Color golden = new Color(Integer.decode("#f9be02"));
	      Color aqua = new Color(Integer.decode("#02c8a7"));
	      
	      background = sky;
	      secondary = aqua;
	      tertiary = brightCoral;
	      quaternary = golden;
	      titleColor = text;
	      titleBorderColor = golden;
	   } else if (theme.equals(themes[4])) {
	      //minimal
	      text = Color.BLACK;
	      Color tangerine = new Color(Integer.decode("#FFCCBC"));
	      Color tropacana = new Color(Integer.decode("#FF8A65"));
	      Color chalk = new Color(Integer.decode("#F5F5F5"));
	      
	      background = chalk;
	      secondary = chalk;
	      tertiary = tropacana;
	      quaternary = chalk;
	      titleColor = text;
	      titleBorderColor = tangerine;
	   } else {
	      //bare
	      text = Color.BLACK;
         Color neutral = new Color(Integer.decode("#efefef"));
         Color carbon = Color.LIGHT_GRAY;

         background = neutral;
         secondary = neutral;
         tertiary = carbon;
         quaternary = neutral;
         titleColor = text;
         titleBorderColor = carbon;
	   }
	   foreground = text;
}
	
	public static void setLAFOcean() {
      try {
         UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
         MetalLookAndFeel.setCurrentTheme(new OceanTheme());
         if (Agenda.statusU) Agenda.log("LAF set: "+UIManager.getLookAndFeel().getID());
      } catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e1) {
         ErrorID.showError(e1, true);
      }
      if (debug) System.out.println("UI DONE");
	}
	
	public static void setLAF() {
	   String name = readDoc("look.txt", LAF_ID);
	   try {
         UIManager.setLookAndFeel(name);
      } catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e) {
         ErrorID.showError(e, true);
      }
	}
	
	public static Border getTitledBorder(String title, int justification, int position) {
      return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(titleBorderColor, 2),
            title, justification, position, font, titleColor);
	}
	
	public static Color getScrollColor() {
	   return new Color(foreground.getRed(), foreground.getBlue(), foreground.getGreen(), 150);
	}
	
	private static Font getBold(float size) {
	   return font.deriveFont(size).deriveFont(Font.BOLD);
	}
	
	public static Border getTitledBorder(String title) {
	   return getTitledBorder(title, TitledBorder.LEADING, TitledBorder.ABOVE_TOP);
	}
	
	public static Font getInputLabelFont() {
	   return getBold(14F);
	}
	
	public static Font getInputFieldFont() {
	   return getInputLabelFont().deriveFont(Font.PLAIN);
	}
	
	public static Font getTabFont() {
	   return getButtonFont();
	}
	
	public static Font getButtonFont() {
	   return getBold(12F);
	}
	
	public static Font getToolTipFont() {
	   return font.deriveFont(13F);
	}
}
