package org.donyasource.donya;

import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class LocalizedResourceResolver {
   private static final Logger logger = Logger.getLogger(LocalizedResourceResolver.class.getName());

   private static final ResourceBundle NULL_BUNDLE = new ListResourceBundle() {
      @Override
      protected Object[][] getContents() {
         return null;
      }
   };

   private final List<ResourceBundleLoader> bundleLoaders;
   private final Map<String, ResourceBundle> cachedBundles = new ConcurrentHashMap<>();
   private final Locale locale;
   
   public LocalizedResourceResolver(Locale locale, List<ResourceBundleLoader> bundleLoaders) {
      this.locale = locale;
      this.bundleLoaders = bundleLoaders == null ? bundleLoaders : new ArrayList<>(bundleLoaders);
   }
   
   public String resolveAsString(String resourceFamily, String resourceKey) {
      ResourceBundle bundle = getBundle(resourceFamily);
      if (bundle == null) {
         throw new MissingResourceException("No resource bundle found for resource family " + resourceFamily, resourceFamily, resourceKey);
      }
      
      return bundle.getString(resourceKey);
   }
   
   private synchronized ResourceBundle getBundle(String resourceFamily) {
      String bundleKey = this.locale.toString() + "/" + resourceFamily;
      ResourceBundle bundle = cachedBundles.get(bundleKey);
      if (bundle == NULL_BUNDLE) {
         logger.fine("NULL bundle in cache for resource family " + bundleKey);
         return null;
      }
      
      if (bundle == null) {
         logger.fine("Bundle not found in cache for resource family " + bundleKey + " getting it from resource bundle loaders");
         bundle = loadBundle(resourceFamily);
         cachedBundles.put(bundleKey, (bundle == null) ? NULL_BUNDLE :  bundle);
      }
      
      return bundle;
   }

   private ResourceBundle loadBundle(String resourceFamily) {
      if (bundleLoaders == null) {
         return null;
      }
      
      for (ResourceBundleLoader loader : bundleLoaders) {
         logger.fine("Looking for bundle for resource family " + resourceFamily + " and locale" + locale);
         if (loader.supportsResourceFamily(resourceFamily)) {
            logger.fine("Trying bundle loader " + loader);
            ResourceBundle bundle = loader.loadBundle(resourceFamily, locale);
            if (bundle != null) {
               logger.fine("Bundle found");
               return bundle;
            }
         }
      }
      
      return null;
      
   }
}
