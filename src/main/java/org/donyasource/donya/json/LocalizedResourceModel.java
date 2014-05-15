package org.donyasource.donya.json;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;


public class LocalizedResourceModel {
   @JsonProperty private final String targetField;
   @JsonProperty private final String resourceFamily;
   @JsonProperty private final String key;
   @JsonProperty private final String[] argumentFields;
   @JsonProperty private final String argumentsField;
   
   public LocalizedResourceModel(String targetField, String bundleFamily, String key) {
      this(targetField, bundleFamily, key, null, null);
   }

   public LocalizedResourceModel(@JsonProperty("targetField")String targetField, 
                                 @JsonProperty("bundleFamily")String resourceFamily, 
                                 @JsonProperty("key")String key, 
                                 @JsonProperty("argumentFields")String[] argumentFields, 
                                 @JsonProperty("argumentsField")String argumentsField) { 
      this.targetField = targetField;
      this.resourceFamily = resourceFamily;
      this.key = key;
      this.argumentFields = argumentFields;
      this.argumentsField = argumentsField;
   }
   
   
   
   public String getTargetField() {
      return targetField;
   }

   public String getResourceFamily() {
      return resourceFamily;
   }

   public String getKey() {
      return key;
   }

   public String[] getArgumentFields() {
      return argumentFields;
   }

   public String getArgumentsField() {
      return argumentsField;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null || obj.getClass() != LocalizedResourceModel.class) {
         return false;
      }
      
      LocalizedResourceModel other = (LocalizedResourceModel)obj;
      
      return new EqualsBuilder().
                     append(targetField, other.targetField).
                     append(resourceFamily, other.resourceFamily).
                     append(key, other.key).
                     append(argumentsField, other.argumentsField).
                     append(argumentFields, other.argumentFields).
                     isEquals();
   }
   
   @Override
   public String toString() {
      return ToStringBuilder.reflectionToString(this);
   }
   
   public static class Builder {
      private String targetField;
      private String bundleFamily;
      private String key;
      private String[] argumentFields;
      private String argumentsField;

      public Builder setTargetField(String targetField) {
         this.targetField = targetField;
         return this;
      }
      public Builder setBundleFamily(String bundleFamily) {
         this.bundleFamily = bundleFamily;
         return this;
      }

      public Builder setKey(String key) {
         this.key = key;
         return this;
      }

      public Builder setArgumentFields(String[] argumentFields) {
         this.argumentFields = argumentFields;
         return this;
      }

      public Builder setArgumentsField(String argumentsField) {
         this.argumentsField = argumentsField;
         return this;
      }
      
      public LocalizedResourceModel build() {
         return new LocalizedResourceModel(targetField, bundleFamily, key, argumentFields, argumentsField);
      }
   }
}
