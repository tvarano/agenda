//Thomas Varano
//Mar 5, 2018

package com.varano.managers;

public interface UIComponent {
   void reset();
   
   void primaryInitialization();
   
   void repaint();
   
   UIComponent[] children();
}
