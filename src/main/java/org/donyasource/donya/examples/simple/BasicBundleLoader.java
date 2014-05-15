package org.donyasource.donya.examples.simple;

import java.util.Locale;
import java.util.ResourceBundle;

import org.donyasource.donya.ResourceBundleLoader;

public class BasicBundleLoader implements ResourceBundleLoader {
   @Override
   public ResourceBundle loadBundle(String resourceFamily, Locale locale) {
      // Just delegate to getBundle
      return ResourceBundle.getBundle(resourceFamily, locale);
   }

   @Override
   public boolean supportsResourceFamily(String resourceFamily) {
      // We support any resource family
      return true;
   }

}
