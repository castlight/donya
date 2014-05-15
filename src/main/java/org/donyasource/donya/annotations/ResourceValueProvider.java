package org.donyasource.donya.annotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.donyasource.donya.util.ReflectionUtil;

import com.google.common.base.Strings;

public class ResourceValueProvider {
   private static final ReflectionUtil reflectionUtil = new ReflectionUtil();
   private static final ResourceKeyHelper keyHelper = new ResourceKeyHelper();
   private static final ResourceFamilyHelper familyHelper = new ResourceFamilyHelper();
   private static final ResourceArgumentListHelper argumentListHelper = new ResourceArgumentListHelper();
   
   public String getResourceFamily(Field resourceField, Object containingObject) {
      String resourceFamily = familyHelper.getFieldValueAsString(resourceField, containingObject);
      assertNotEmpty("resource family", resourceFamily);
      
      return resourceFamily;
   }

   public String getResourceKey(Field resourceField, Object containingObject) {
      String resourceKey = keyHelper.getFieldValueAsString(resourceField, containingObject);
      assertNotEmpty("resource key", resourceKey);
      
      return resourceKey;
   }
   
   public List<String> getResourceKeyAsStringList(Field keyField, Object containingObject) {
      Object keyValue = keyHelper.getFieldValue(keyField, containingObject);
      assertKeyValueIsList(keyField, containingObject, keyValue);

      // TODO - compile-time validation ensures this is a list of objects so this is a safe cast
      @SuppressWarnings("unchecked")
      List<Object> keyList = (List<Object>)keyValue;

      assertKeyListNotBlank(keyField, containingObject, keyList);
      
      List<String> keyStrings = new ArrayList<>();
      for (Object key : keyList) {
         keyStrings.add(key == null ? null : key.toString());
      }
      
      return keyStrings;
   }

        private void assertKeyListNotBlank(Field resourceField, Object containingObject, List<Object> keyList) {
      if (keyList == null || keyList.isEmpty()) {
         throw new RuntimeException("Field " + resourceField + " for object " + containingObject + " does not have any keys");
      }
   }

   private void assertKeyValueIsList(Field resourceField, Object containingObject, Object key) {
      if (!(List.class.isInstance(key))) {
         throw new RuntimeException("Expected key field " + resourceField.getName() + " on object " + containingObject + 
                  " to be a collection, but it is a " + key.getClass());
      }
   }

   public Object[] getResourceArguments(Field resourceField, Object containingObject) {
      Object[] arguments = argumentListHelper.getFieldValue(resourceField, containingObject);
      if (arguments != null && arguments.length > 0) {
         return arguments;
      }
      return buildArguments(resourceField, containingObject);
   }
   
   private Object[] buildArguments(Field resourceField, Object containingObject) {
      // A sorted map so that arguments are ordered correctly
      Map<Integer, Object> arguments = new TreeMap<>();
      String resourceName = resourceField.getName();

      for (Field field : reflectionUtil.getAllDeclaredFields(containingObject.getClass())) {
         ResourceArgument argument = field.getAnnotation(ResourceArgument.class);
         if (isMatchingArgument(resourceName, argument)) {
            arguments.put(argument.index(), getFieldValue(containingObject, field));
         }
      }
      
      return arguments.values().toArray();
   }

   private boolean isMatchingArgument(String resourceName, ResourceArgument argument) {
      if (argument == null) {
         return false;
      }

      String argumentResourceName = argument.resourceName();
      return argument != null && (Strings.isNullOrEmpty(argumentResourceName) || resourceName.equals(argumentResourceName));
   }

   public String getResourceValue(Field resourceField, Object containingObject) {
      return (String)getFieldValue(containingObject, resourceField);
   }
   
   public Object getArgument(Field resourceField, Object containingObject) {
      return getFieldValue(containingObject, resourceField);
   }
   
   private void assertNotEmpty(String value, String fieldName) {
      if (Strings.isNullOrEmpty(value)) {
         throw new RuntimeException("field " + fieldName + " is null or empty");
      }
   }
   
   private Object getFieldValue(Object containingObject, Field field) {
      try {
         return reflectionUtil.getFieldValue(containingObject, field);
      } catch (Exception e) {
         throw new RuntimeException("Unable to get value for field " + field.getName() + " in object " + containingObject, e);
      }
   }
   
}
