//Thomas Varano
//[Program Descripion]
//Jan 3, 2018

package com.varano.managers;

public interface PanelView
{
   void refresh();
   
   void open();
   
   void close();   
   
   String getName();
   
   void save();
   
   void revalidate();
   
   Agenda getMain();
}
