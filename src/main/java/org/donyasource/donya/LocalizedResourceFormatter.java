package org.donyasource.donya;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.donyasource.donya.annotations.ResourceValueProvider;

public class LocalizedResourceFormatter {
   private final Logger logger; 
   private final LocalizedResourceResolver resourceResolver;
   private final ResourceValueProvider valueProvider;
   private final boolean throwExceptionOnFailure;
   

   public LocalizedResourceFormatter(Locale locale, List<ResourceBundleLoader> bundleLoaders) {
      this(new LocalizedResourceResolver(locale, bundleLoaders), Logger.getLogger(LocalizedResourceFormatter.class.getName()),
               new ResourceValueProvider(), true);
   }

   LocalizedResourceFormatter(LocalizedResourceResolver resourceResolver, Logger logger,
            ResourceValueProvider resourceValueProvider, boolean throwExceptionOnFailure) {
      this.resourceResolver = resourceResolver;
      this.logger = logger;
      this.valueProvider = resourceValueProvider;
      this.throwExceptionOnFailure = throwExceptionOnFailure;
   }
   
   public List<String> formatResources(Field resourceField, Object obj) {
      List<String> keys = valueProvider.getResourceKeyAsStringList(resourceField, obj);
      String resourceFamily = valueProvider.getResourceFamily(resourceField, obj);
      
      List<String> resources = new ArrayList<>();
      
      for (String key : keys) {
         if (key == null) {
            throw new RuntimeException("Key is null in key list for resource " + resourceField.getName() + " in object " + obj);
         }
         // Not currently supporting arguments for lists
         resources.add(resourceResolver.resolveAsString(resourceFamily, key));
      }
      
      return resources;
   }


   /**
    * Given a LocalizedResource annotation and the object for that annotation,
    * return a locale-specific formatting of the resource. Assumes that the
    * resource is a string
    * 
    * @param resourceField the Field object for a localized resource
    * @param obj the object that contains the localized resource
    */
   public String formatResourceAsString(Field resourceField, Object obj) {
      try {
         String resource = getResource(resourceField, obj);
         
         if (resource == null) {
            return null;
         }
         
         Object[] arguments = getArguments(resourceField, obj);

         return MessageFormat.format(resource, arguments);
      } catch (Exception e) {
         if (throwExceptionOnFailure) {
            throw new FormatException(e);
         }

         logger.log(Level.SEVERE, "Unable to format field " + resourceField.getName() + " for object " + obj + "; using current value in object.", e);
         return valueProvider.getResourceValue(resourceField, obj);
      }
   }

   private Object[] getArguments(Field resourceField, Object obj) {
      return valueProvider.getResourceArguments(resourceField, obj);
   }

   
   private String getResource(Field resourceField, Object obj) {
      String resourceFamily = valueProvider.getResourceFamily(resourceField, obj);
      String key = valueProvider.getResourceKey(resourceField, obj);

      try {
         return resourceResolver.resolveAsString(resourceFamily, key);
      } catch (MissingResourceException e) {
         logger.log(Level.SEVERE, "Could not find resource with key " + key + " for resource family " + resourceFamily + "; using existing value in object", e);
         return valueProvider.getResourceValue(resourceField, obj);
      }
   }
}
