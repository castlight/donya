package org.donyasource.donya.annotations;

import java.lang.reflect.Field;

import com.google.common.base.Strings;

public class ResourceFamilyHelper extends ResourceAnnotationHelper<ResourceFamily, Object> {
   public ResourceFamilyHelper() {
      super(ResourceFamily.class);
   }

   @Override
   public String getValue(ResourceFamily annotation) {
      return annotation.value();
   }

   @Override
   public String getFieldValueAsString(Field resourceField, Object containingObject) {
      String resourceFamily = super.getFieldValueAsString(resourceField, containingObject);
      
      if (Strings.isNullOrEmpty(resourceFamily)) {
         throw new RuntimeException("Resource family is empty for resource " + resourceField.getName() + " is empty for object " + containingObject);
      }
      
      return resourceFamily;
   }
   
   
}
