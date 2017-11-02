package managers;

import java.awt.Color;
import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

//Thomas Varano
public class UIHandler {
	public static Color foreground, background;
	public static Font font;
	private boolean debug;
	
	public static void init() {
		System.out.println("UI DONE");
	}
}
