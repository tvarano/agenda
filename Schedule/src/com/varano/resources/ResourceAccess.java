//Thomas Varano
//[Program Descripion]
//Nov 28, 2017

package com.varano.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.ImageIcon;

import com.varano.constants.ErrorID;

public final class ResourceAccess
{
   public static InputStream getResourceStream(String localPath) {
      return ResourceAccess.class.getResourceAsStream(localPath);
   }
   
   public static ImageIcon getImage(String localPath) {
      try {
         return new ImageIcon(ResourceAccess.class.getResource(localPath));
      } catch (NullPointerException e) {
         ErrorID.showError(e, true);
         return null;
      }
   }
   
   public static String readHtml(URL site) throws IOException {
      java.io.BufferedReader in = null;
      in = new java.io.BufferedReader(new java.io.InputStreamReader(site.openStream()));
      java.lang.StringBuilder b = new java.lang.StringBuilder();
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
         b.append(inputLine);
         b.append("\n");
      }
      in.close();
      return b.toString();
   }
}
