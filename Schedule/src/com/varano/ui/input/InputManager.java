//Thomas Varano
//[Program Descripion]
//Dec 21, 2017

package com.varano.ui.input;

import com.varano.information.ClassPeriod;

/**
 * Specifies an input manager, necessarily adding methods that are needed for each of them. 
 * they may not all be completely implemented in both classes, but they are necessary for work between panels
 * 
 * @author Thomas Varano
 *
 */
public interface InputManager
{
   void addCustomClass();
   
   void addClass(int index);
   
   void addClass(ClassPeriod c);
         
   void save();
   
   void closeToDisp();
   
   boolean isSaved();
}
