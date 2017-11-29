//Thomas Varano
//[Program Descripion]
//Nov 28, 2017

package resources;

import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

import constants.ErrorID;

public final class ResourceAccess
{
   public static File getResource(String localPath) {
      try {
         return new File(ResourceAccess.class.getResource(localPath).getFile());
      } catch (NullPointerException e) {
         ErrorID.showError(e, true);
         return null;
      }
   }
   
   public static String getResourceBinPath(String localPath) {
      try {
         return ResourceAccess.class.getResource(localPath).getFile();
      } catch (NullPointerException e) {
         ErrorID.showError(e, true);
         return null;
      }
   }
   
   public static String getResourceSrcPath(String localPath) {
      String binPath = getResourceBinPath(localPath);
      return binPath.substring(0, binPath.indexOf("bin"))+"/src/resources/"+localPath;
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
