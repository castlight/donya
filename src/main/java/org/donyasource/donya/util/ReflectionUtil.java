package org.donyasource.donya.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ReflectionUtil {
   public List<Class<?>> getFullInheritanceChain(Class<?> clazz) {
      List<Class<?>> retVal = new ArrayList<Class<?>>();
      do {
         retVal.add(clazz);
         clazz = clazz.getSuperclass();
      } while(clazz != null);
      
      return retVal;
   }
   
   public Object getFieldValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
      Field field = getField(obj, fieldName);
      return getFieldValue(obj, field);
   }
   
   public Object getFieldValue(Object obj, Field field) throws IllegalAccessException {
      boolean accessible = field.isAccessible();

      field.setAccessible(true);
      Object result = field.get(obj);
      field.setAccessible(accessible);
      
      return result;
   }
   
   public Field getField(Object obj, String fieldName) throws NoSuchFieldException {
      Class<? extends Object> clazz = obj.getClass();
      while (clazz != null) {
         Field field = getField(clazz, fieldName);
         if (field != null) {
            return field;
         }
         
         clazz = clazz.getSuperclass();
      }
      
      throw new NoSuchFieldException("Field " + fieldName + " not found in object " + obj + " or its superclasses");
   }
   
   private Field getField(Class<? extends Object> clazz, String fieldName) {
      Field[] fields = clazz.getDeclaredFields();
      for (Field field : fields) {
         if (fieldName.equals(field.getName())) {
            return field;
         }
      }
      
      return null;
      
   }

   public Collection<Field> getAllDeclaredFields(Class<? extends Object> clazz) {
      List<Field> fields = new ArrayList<Field>();
      while (clazz != null) {
         fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
         clazz = clazz.getSuperclass();
      }
      
      return fields;
   }

}
