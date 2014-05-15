package org.donyasource.donya.annotations;

public class ResourceArgumentListHelper extends ResourceAnnotationHelper<ResourceArgumentList, Object[]> {
   public ResourceArgumentListHelper() {
      super(ResourceArgumentList.class);
   }

   @Override
   public String getValue(ResourceArgumentList annotation) {
      return annotation.value();
   }
}
