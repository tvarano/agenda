package managers;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

//Thomas Varano
public class UIHandler {
	public static Color foreground, background, titleBorderColor, secondary, tertiary, titleColor, quaternary;
	public static Font font;
	private static boolean debug;
	
	public static void init() { 
	   debug = true;
	   font = new Font("Times New Roman", Font.PLAIN, 16);
	   setLAF();
	   setColors();
	   putValues();
	}
	
	public static void putValues() {
	   UIManager.put("List.selectionBackground", tertiary);
	   UIManager.put("TextField.background", quaternary);
	}
	
	public static void setColors() {
	   Color noir = new Color(Integer.decode("#1d2731"));
	   Color gris = new Color(40,40,40);
	   Color carbon = new Color(Integer.decode("#a9a9a9"));
	   Color sky = new Color(Integer.decode("#caebf2"));
	   Color watermelon = new Color(Integer.decode("#ff3b3f"));
	   Color neutral = new Color(Integer.decode("#efefef"));
	   
	   titleColor = noir;
	   tertiary = watermelon;
	   background = neutral;
	   secondary = carbon;
	   quaternary = sky;
	   foreground = gris;
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
      
//      for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//         if ("Nimbus".equals(info.getName())) {
//            try {
//               UIManager.setLookAndFeel(info.getClassName());
//            } catch (ClassNotFoundException | InstantiationException
//                  | IllegalAccessException
//                  | UnsupportedLookAndFeelException e) {
//               e.printStackTrace();
//            }
//            break;
//         }
//      }
      try {
         UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
         MetalLookAndFeel.setCurrentTheme(new OceanTheme());       
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
	
	public static Border getTitledBorder(String title) {
	   return getTitledBorder(title, TitledBorder.LEADING, TitledBorder.ABOVE_TOP);
	}
}
