package com.varano.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.zip.DataFormatException;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import com.varano.information.constants.ErrorID;
import com.varano.managers.Agenda;
import com.varano.managers.FileHandler;
import com.varano.managers.ProcessHandler;
import com.varano.resources.Addresses;
import com.varano.resources.ResourceAccess;

//Thomas Varano

/**
 * Handles all UI necessities, including LAF and theme. Uninstantiatable and unextendable. Only for static calls.
 * 
 * @author Thomas Varano
 */
public final class UIHandler {

   static final int THEME_ID = 0, LAF_ID = 1;
   
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
	   UIManager.put("OptionPane.errorIcon", ResourceAccess.getIcon("ErrorIcon.png"));
	   UIManager.put("OptionPane.warningIcon", ResourceAccess.getIcon("WarningIcon.png"));
	   UIManager.put("OptionPane.informationIcon", ResourceAccess.getIcon("InfoIcon.png"));
	   UIManager.put("OptionPane.questionIcon", ResourceAccess.getIcon("QuestionIcon.png"));
	   
	   final int divThickness = 4;
	   UIManager.put("SplitPane.background", background);
      UIManager.put("SplitPaneDivider.border", BorderFactory.createLineBorder(titleBorderColor, divThickness));
      UIManager.put("SplitPaneDivider.draggingColor", titleBorderColor.darker());
      UIManager.put("SplitPane.dividerSize", divThickness);
	   }
	
	public static final int FIELD_HEIGHT = 25;
	
	
	
	/**
	 * the themes available for the application
	 */
	public static final String[] themes = 
	   {"Clean (Default)", "Night Mode", "Neutral", "Muted", "Colorful", "Minimal", "Grayscale"}; 

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
   
   public static boolean showWelcome() {
      String html = "<html> <h1> Welcome to " + Agenda.APP_NAME + " </h1>"
            + "<h2>Version " + Agenda.BUILD + "</h2> "
            + "<p>Agenda is a program for staying on top of classes in the Pascack Valley District."
            + "<ul>"
            + "<li>On the home screen, you can see the current class's data on the top panel"
            + "<br>and view other classes' data and memos in the bottom panel</li>"
            + "<li>To edit your classes, click File > Input Schedule.</li>"
            + "<li>To edit grades, click File > View GPA.</li>"
            + "<li>To change the look of the program, either go to preferences (\u2318 + ,) or the View Menu"
            + "</ul>"
            + "<br> - Thomas Varano"
            + "</html>";
      javax.swing.JEditorPane text = new javax.swing.JEditorPane("text/html", html);
      text.setFont(font);
      HTMLEditorKit kit = new HTMLEditorKit();
      text.setEditorKit(kit);
      kit.getStyleSheet().addRule("body {color:#000; font-family:" + font.getFamily() + "; margin: 4px; }");
      JPanel content = new JPanel(new BorderLayout());
      Document doc = kit.createDefaultDocument();
      text.setDocument(doc);
      text.setText(html);
      text.setOpaque(false);
      text.setEditable(false);
      JCheckBox check = new JCheckBox("Don't Show This Again");
      check.setFont(UIHandler.getButtonFont());
      content.add(text);
      content.add(check, BorderLayout.SOUTH);
      JOptionPane.showMessageDialog(null, content, "Welcome",
            JOptionPane.INFORMATION_MESSAGE,
            ResourceAccess.getIcon("Agenda Logo.png"));
      return !check.isSelected();
   }
   
   public static void main(String[] args) {
//      showNews();
      showWelcome();
   }
   
   public static void showAbout() {
      String html = "<html> <h1> " + Agenda.APP_NAME + " </h1> <h2>Version " + Agenda.BUILD + "</h2> "
            + "<h3>" + Agenda.LAST_UPDATED + "</h3>"
            + "<p>Agenda is a schedule program for the Pascack Valley High School District"
            + "<p>that can keep track of time, school schedules, assignments, and GPA"
            + "<p>for students."
            + "<br><br>"
            + "<h2>CREDITS:"
            + "<h3>Thomas Varano : Author"
            + "<br><br>Viktor Nakev : Icon Designer"
            + "<br><br>Matthew Gheduzzi : Alpha Tester"
            + "<br><br>Michael Ruberto : Enforcer"
            + "</html>";
      showSimplePopUp(html, "About "+Agenda.APP_NAME);
   }

   public static void showNews() {
      String html;
      try {
         html = ProcessHandler.futureCall(2000, new Callable<String>() {
            @Override
            public String call() throws Exception {
               return ResourceAccess.readHtml(Addresses.createURL(Addresses.NEWS));
            }
         }, "read news");
      } catch (Exception e) {
         Agenda.logError("error retrieving news", e);
         html = "<html> <body> <h1> Unable to load News."
               + "<br> Check your internet Connection.</h1></body></html>";
      }
      showSimplePopUp(html, Agenda.APP_NAME + " News");
   }
   
   private static void showSimplePopUp(String html, String title) {
      javax.swing.JEditorPane content = new javax.swing.JEditorPane("text/html", html);
      content.setFont(font);
      HTMLEditorKit kit = new HTMLEditorKit();
      content.setEditorKit(kit);
      kit.getStyleSheet().addRule("body {color:#000; font-family:"+font.getFamily()+"; margin: 4px; }");

      Document doc = kit.createDefaultDocument();
      content.setDocument(doc);
      content.setText(html);
      content.setOpaque(false);
      content.setEditable(false);
      JOptionPane.showMessageDialog(null, 
            content, title, 
            JOptionPane.INFORMATION_MESSAGE, ResourceAccess.getIcon("Agenda Logo.png"));
   }
   
   public static void setRotation(Agenda age, com.varano.information.constants.Rotation r) {
      age.getManager().setRotation(r);
   }
   
   /**
    * @deprecated Not using time bar
    * @return
    */
   @Deprecated(since = "1.8")
   public static int timeBarIndex() {
      return 0;
   }
   

   
   //allows for rollover animations for buttons.
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
   
   static String readDoc(String fileName, int type) {
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
   
   public static String readTheme() {
		return readDoc("theme.txt", THEME_ID);
   }
   
   public static String readLAF() {
		return readDoc("look.txt", LAF_ID);
}
   
	public static void setColors() {
	   String theme = themes[0];
	   String str = readTheme();
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
	
	static void setLAF0(String name) {
		Agenda.log("set LAF to "+ name);
	   try {
         UIManager.setLookAndFeel(name);
      } catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e) {
         ErrorID.showError(e, true);
      }
	}
	
	public static void setLAF() {
	   String name = readLAF();
	   setLAF0(name);
	}
	
	public static Border getTitledBorder(String title, int justification, int position) {
      return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(titleBorderColor, 1),
//            BorderFactory.createEmptyBorder(2, 2, 2, 2),
            title, justification, position, font, titleColor);
	}
	
	public static Border getTitledBorder(String title) {
	   return getTitledBorder(title, TitledBorder.LEADING, TitledBorder.ABOVE_TOP);
	}

	public static final Font font = new Font("Futura", Font.PLAIN, 16);
	
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
