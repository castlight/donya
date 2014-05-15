package org.donyasource.donya.examples.simple;

import java.util.Locale;
import java.util.ResourceBundle;

import org.donyasource.donya.ResourceBundleLoader;

public class PropertyBundleLoader implements ResourceBundleLoader {
   @Override
   public ResourceBundle loadBundle(String resourceFamily, Locale locale) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean supportsResourceFamily(String resourceFamily) {
      // We support any resource family
      return true;
   }

}
