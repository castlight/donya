package org.donyasource.donya.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.donyasource.donya.util.ReflectionUtil;

import com.google.common.base.Strings;

public abstract class ResourceAnnotationHelper<T extends Annotation, R> {
   private static final ReflectionUtil reflectionUtil = new ReflectionUtil();
   private final Class<T> annotationClass;
   
   protected ResourceAnnotationHelper(Class <T> annotationClass)  {
      this.annotationClass = annotationClass;
   }
   
   public abstract String getValue(T annotation);
   
   public String getFieldValueAsString(Field resourceField, Object containingObject) {
      Object valueObject = getFieldValue(resourceField, containingObject);
      
      return valueObject == null ? null : valueObject.toString();
   }
   
   public R getFieldValue(Field resourceField, Object containingObject) {
      String resourceName = resourceField.getName();
      
      Field field = null;
      try {
         field = getAnnotationField(containingObject, resourceName);
      } catch (Exception e) {
         throw new RuntimeException("Error finding the field with annotation " + annotationClass.getName() + 
                  " for resource " + resourceName + " in object " + containingObject, e) ;
      }
      
      return field == null ? null : getFieldValue(containingObject, field);
   }
   
   /**
    * Get the right field in the object.  If there are multiple fields with the same annotation, pick 
    * the one whose value matches the name of the resource we are processing.
    */
   private Field getAnnotationField(Object containingObject, String resourceName) {
      for (Field field : reflectionUtil.getAllDeclaredFields(containingObject.getClass())) {
         T annotation = field.getAnnotation(annotationClass);
         if (annotation == null) {
            continue;
         }

         String value = getValue(annotation);
         if (Strings.isNullOrEmpty(value)) {
            return field;
         }

         if (resourceName.equals(value)) {
            return field;
         }
      }
      
      return null;
   }
   
   private R getFieldValue(Object containingObject, Field field) {
      try {
         // TODO - verify field value is of correct type in compile-time validations
         return (R)reflectionUtil.getFieldValue(containingObject, field);
      } catch (Exception e) {
         throw new RuntimeException("Unable to get value for field " + field.getName() + " in object " + containingObject, e);
      }
   }
}
