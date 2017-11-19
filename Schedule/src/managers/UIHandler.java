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
import java.time.LocalTime;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import ioFunctions.SchedWriter;

//Thomas Varano
public class UIHandler {
	public static Color foreground, background, titleBorderColor, secondary, tertiary, titleColor, quaternary;
	public static Font font;
	private static boolean debug;
	
	public static void init() { 
	   debug = false;
	   font = new Font("Georgia", Font.PLAIN, 16);
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
            return new Dimension(Main.MIN_W,Main.MIN_H);
         }
         public Dimension getPreferredSize() {
            return new Dimension(Main.PREF_W, Main.PREF_H);
         }
      };
      retval.setVisible(true);
      retval.repaint();
      return retval;
   }
	
   public static JFrame createEmptyLoad() {
      return new JFrame(Main.APP_NAME + " " + Main.BUILD);
   }
   
	public static JFrame createLoadingScreen(JFrame f) {
	   JPanel p = getLoadingPanel();
	   f.getContentPane().add(p);
	   f.setMinimumSize(new Dimension(Main.MIN_W, Main.MIN_H));
	   f.pack();
	   f.setLocationRelativeTo(null);
	   f.setVisible(true);
	   return f;
	}
	
	public static void putValues() {
	   UIManager.put("List.selectionBackground", tertiary);
	   UIManager.put("ToolTip.font", getToolTipFont());
	   UIManager.put("Button.disabledText", secondary);
	}
	
	//TODO menu ideas... change color scheme, maybe font.
	// have a help tab which would describe where the log is and my email
	
	
	public synchronized static MenuBar configureMenuBar(JFrame frame) {
      MenuBar bar = new MenuBar();
      Menu m = new Menu("Time Left In Class: ");
      m.add("ACTION1");
      bar.add(m);
      
      m = new Menu("Help");
      MenuItem mi = new MenuItem("Error Help");
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, 
                  "Error logging helps the efficiency and ease of use for \n"
                  + "this program. Logs are kept at:\n"
                  + SchedWriter.LOG_ROUTE + "\n"
                  + "and keep internal information about the program as it runs.\n"
                  + "If an error occurs, its message will be printed in the log.\n"
                  + "The best thing to do is simply send the entire log when this\n"
                  + "occurs. It gives the most information possible and will allow\n"
                  + "for the error to be fixed most quickly.\n"
                  + "Email the log to varanoth@pascack.org", 
                  Main.APP_NAME, JOptionPane.INFORMATION_MESSAGE, null);
         }
      });
      m.add(mi);
      mi = new MenuItem("Sharing Protocol");
      mi.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, 
                  "To share this application, please share the entire folder\n"
                  + "this application came in. The program comes with a README\n"
                  + "file, which will help users who do not have all the \n"
                  + "necessary items on their computer for running this program.",
                  Main.APP_NAME, JOptionPane.INFORMATION_MESSAGE, null);
         }
      });
      m.add(mi);
      bar.setHelpMenu(m);
      m.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            System.out.println("CLICKED IT WORKDS");
         }
      });
      frame.setMenuBar(bar);
      if (debug) System.out.println("BARUI "+ bar);
      return bar;
	}
	
	public static void setColors() {
	   Color text = new Color(40,40,40);
	   Color noir = new Color(Integer.decode("#1d2731"));
	   Color carbon = new Color(Integer.decode("#a9a9a9"));
	   Color sky = new Color(Integer.decode("#caebf2"));
	   Color watermelon = new Color(Integer.decode("#ff6a5c"));
	   Color neutral = new Color(Integer.decode("#efefef"));
	   
	   foreground = text;
	   background = neutral;
	   secondary = carbon;
	   tertiary = watermelon;
	   quaternary = sky;
	   titleColor = noir;
	   titleBorderColor = carbon;
	}
	
	public static Color mutateColor(Color o, int diff) {
	   int[] newRGB = cap255(o.getRed(), o.getGreen(), o.getBlue(), diff); 
	   return new Color(newRGB[0], newRGB[1], newRGB[2], o.getAlpha());
	}
	
	private static int[] cap255(int r, int g, int b, int diff) {
	   int max = Math.max(Math.max(r, g), b);
	   if (max + diff > 255)
	      diff = 255 - max;
	   return new int[] {r + diff, g + diff, b + diff};
	}
	
	public static void setLAF() {
      try {
         UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
         MetalLookAndFeel.setCurrentTheme(new OceanTheme());   
         if (Main.statusU) Main.log("LAF set: "+UIManager.getLookAndFeel().getID());
      } catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e1) {
         e1.printStackTrace();
      }
      if (debug) System.out.println("UI DONE");
	}
	
	public static Border getTitledBorder(String title, int justification, int position) {
      return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(titleBorderColor, 2),
            title, justification, position, font, titleColor);
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
