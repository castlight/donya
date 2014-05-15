package org.donyasource.donya.annotations;

import java.lang.reflect.Field;

import com.google.common.base.Strings;


public class ResourceKeyHelper extends ResourceAnnotationHelper<ResourceKey, Object> {
   
   public ResourceKeyHelper() {
      super(ResourceKey.class);
   }

   @Override
   public String getValue(ResourceKey annotation) {
      return annotation.value();
   }

   @Override
   public String getFieldValueAsString(Field resourceField, Object containingObject) {
      String key = super.getFieldValueAsString(resourceField, containingObject);
      if (Strings.isNullOrEmpty(key)) {
         throw new RuntimeException("Key is empty for resource " + resourceField.getName() + " in object " + containingObject);
      }
      
      return key;
   }
}
