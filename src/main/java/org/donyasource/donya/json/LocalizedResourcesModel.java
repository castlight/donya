package org.donyasource.donya.json;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;


public class LocalizedResourcesModel {
   @JsonProperty private final LocalizedResourceModel[] resources;

   public LocalizedResourcesModel(@JsonProperty("resources")LocalizedResourceModel ... resources) {
      this.resources = resources;
   }
   
   public LocalizedResourceModel[] getResources() {
      return resources;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      
      if (obj.getClass() != LocalizedResourcesModel.class) {
         return false;
      }
      
      LocalizedResourcesModel other = (LocalizedResourcesModel)obj;

      return new EqualsBuilder().append(resources, other.resources).isEquals();
   }
   
   @Override 
   public int hashCode() {
      return new HashCodeBuilder().append(resources).toHashCode();
   }
   
   @Override
   public String toString() {
      return new ToStringBuilder(this).append("resources", resources).toString();
   }
}
