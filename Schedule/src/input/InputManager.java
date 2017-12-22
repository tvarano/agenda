//Thomas Varano
//[Program Descripion]
//Dec 21, 2017

package input;

import information.ClassPeriod;

public interface InputManager
{
   void addClass(int index);
   
   void addClass(ClassPeriod c);
         
   void save();
}
