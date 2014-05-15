package org.donyasource.donya;

public class Resource {
   public final String locale_key;
   public final String family_key;
   public final String resource_key;
   public final String resource;

   public Resource(String locale_key, String family_key, String resource_key,
         String resource) {

      this.locale_key = locale_key;
      this.family_key = family_key;
      this.resource_key = resource_key;
      this.resource = resource;
   }
}