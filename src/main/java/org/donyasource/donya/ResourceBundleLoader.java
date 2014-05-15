package org.donyasource.donya;

import java.util.Locale;
import java.util.ResourceBundle;

public interface ResourceBundleLoader {

   public ResourceBundle loadBundle(String resourceFamily, Locale locale);

   public boolean supportsResourceFamily(String resourceFamily);
}
