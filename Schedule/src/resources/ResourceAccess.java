//Thomas Varano
//[Program Descripion]
//Nov 28, 2017

package resources;

import java.io.File;

import javax.swing.ImageIcon;

import constants.ErrorID;

public final class ResourceAccess
{
   public static final File TEMP_PATH_CHECK = new File(System.getProperty("user.home")+"/Desktop/tempPathCheck.txt");
   public static File getResource(String localPath) {
      return new File(getResourceBinPath(localPath));
   }
   
   public static String getResourceBinPath(String localPath) {
      try {
         return ResourceAccess.class.getResource(localPath).getFile();
      } catch (NullPointerException e) {
         ErrorID.showError(e, true);
         return null;
      }
   }
   
   public static ImageIcon getImage(String localPath) {
      try {
         return new ImageIcon(ResourceAccess.class.getResource(localPath));
      } catch (NullPointerException e) {
         ErrorID.showError(e, true);
         return null;
      }
   }
}
