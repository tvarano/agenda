//Thomas Varano
//Mar 27, 2018

package com.varano.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;

import com.varano.managers.Agenda;
import com.varano.resources.ResourceAccess;


public class Notif extends JDialog {
   private static final long serialVersionUID = -6816539005364067301L;

   private static final int PREF_W = 230;
   public static final Dimension THREE_LINE_SIZE = new Dimension(PREF_W,90);
   public static final Dimension TWO_LINE_SIZE = new Dimension(PREF_W, 75);
   private static final Dimension DEF_PREF_SIZE = TWO_LINE_SIZE;
   private static final int defaultMillisToShow = 10_000;
   
   private Timer removalChecker;
   private long timeStarted;
   private JPanel content;
   private int millisToShow; 
   private boolean debug = false;
   
   public Notif(Window parent, String message, Border border, Image icon, int millisToShow) {
      super(parent, "no title", ModalityType.MODELESS);
      setUndecorated(true);
      this.millisToShow = millisToShow;
      content = new Content(message, border, icon, DEF_PREF_SIZE);
      add(content);
      setPreferredSize(THREE_LINE_SIZE);      
      init0();
   }
   
   public Notif(String message, Border border, Image icon) {
      this(null, message, border, icon, defaultMillisToShow);
   }
   
   public Notif(String message, int millisToShow) {
      this(null, message, BorderFactory.createRaisedBevelBorder(), null, millisToShow);
   }
   
   public Notif(String message) {
      this(message, defaultMillisToShow);
   }
   
   public Notif(String message, Image icon) {
      this(message, BorderFactory.createRaisedBevelBorder(), icon);
   }
   
   public Notif(Window owner, String message, Image icon) {
      this(owner, message, BorderFactory.createRaisedBevelBorder(), icon, defaultMillisToShow);
   }
   
   public Notif(JPanel content) {
      super(null, "no title", ModalityType.MODELESS);
      setUndecorated(true);
      this.content = content;
      init0();
   }
   
   
   private class Content extends JPanel {
      private static final long serialVersionUID = 8018857125585567105L;
      private String message;
      private Image image;
      private JPanel text;
      
      public Content(String message, Border border, Image icon, Dimension prefSize) {
         super(new BorderLayout());
         this.message = message; image = icon; 
         setFont(new Font("Futura", Font.BOLD, 13));
         setBackground(Color.WHITE);
         JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
         ((FlowLayout) bottom.getLayout()).setVgap(5);
         bottom.setOpaque(false);
         JButton close = new JButton(ResourceAccess.getIcon("closeButton.png"));
         close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               disappear();
            }
         });
         close.setPreferredSize(new Dimension(20, 20));
         close.setBorderPainted(false);
         bottom.add(close);
         add(bottom, BorderLayout.SOUTH);
         text = new JPanel();
         text.setOpaque(false);
         add(text, BorderLayout.EAST);
         
         formatMessage();
         setPreferredSize(prefSize);
         setBorder(border);
      }
      
      protected void paintComponent(Graphics g) {
         super.repaint();
         if (image == null) return;
         Graphics2D g2 = (Graphics2D) g;
         int sideLen =  (image.getHeight(this) > getHeight()) ? getHeight() : image.getHeight(this);
         g2.drawImage(image, 0, 0, sideLen, sideLen, this);
         g2.setColor(new Color(255, 255, 255, 110));
         g2.fillRect(0, 0, getWidth(), getHeight());
      }
      
      private void formatMessage() {
         if (message == null) return;
         for (String s : separateLines(message)) {      
            if (debug) System.out.println(s);
            JLabel l = new JLabel(s);
            l.setFont(getFont());
            l.setAlignmentX(Component.RIGHT_ALIGNMENT);
            text.add(l);
         }
         text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
         int gap = 5;
         text.setBorder(BorderFactory.createEmptyBorder(gap, gap, gap, gap));
      }
   }
   
   private void appear() {
      if (debug) System.out.println("app");
      
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            setVisible(true);
            setFocusableWindowState(false);
         }
      });
   }
   
   private static String[] separateLines(String text) {
      int amtLines = findLines(text);
      if (amtLines == 1) return new String[] {text};
      
      String[] ret = new String[amtLines];
      for (int i = 0; i < amtLines - 1; i++) {
         ret[i] = text.substring(0, text.indexOf(newLn));
         text = text.substring(text.indexOf(newLn) + newLn.length());
      }
      ret[ret.length - 1] = text;
      return ret;
   }
   
   private static final String newLn = "\n";
   private static int findLines(String text) {
      if (!text.contains(newLn)) return 1;
      return 1 + findLines(text.substring(text.indexOf(newLn) + newLn.length()));
   }
   
   private void init0() {
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
      setLocation((int)screenSize.getWidth() - getWidth(), 0);
      if (debug) System.out.println(getLocation().getY());
      add(content);
      setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      setAlwaysOnTop(true);
      setFocusable(false);
      setAutoRequestFocus(false);
      pack();
      setResizable(false);
      timeStarted = System.currentTimeMillis();
      if (millisToShow != -1) {
         removalChecker = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
               if (debug) System.out.println(System.currentTimeMillis() - timeStarted);
               if (System.currentTimeMillis() - timeStarted > millisToShow) disappear();
            }
         });
         removalChecker.start();
      }
      if (debug) System.out.println("here");
      appear();
   }
   
   public void disappear() {
      if (debug) System.out.println("diss");
      Agenda.log("removing notificaiton");
      dispose();
      if (removalChecker != null) removalChecker.stop();
      removalChecker = null;
   }
   
   public void setPreferredSize(Dimension preferredSize) {
      super.setPreferredSize(preferredSize);
      content.setPreferredSize(preferredSize);
      pack();
      revalidate();
   }

   public long getTimeStarted() {
      return timeStarted;
   }
   
   public static void main(String[] args) {
      new Notif("hello");
   }
}
