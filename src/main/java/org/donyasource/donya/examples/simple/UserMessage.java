package org.donyasource.donya.examples.simple;

import org.codehaus.jackson.annotate.JsonProperty;
import org.donyasource.donya.annotations.LocalizedResource;
import org.donyasource.donya.annotations.ResourceFamily;
import org.donyasource.donya.annotations.ResourceKey;

public class UserMessage {
   @ResourceFamily 
   public static final String RESOURCE_FAMILY = "messages";

   @LocalizedResource
   private String message;
   
   @ResourceKey
   @JsonProperty
   private final MessageCode messageCode;
   
   public UserMessage(MessageCode messageCode) {
      this.messageCode = messageCode;
   }
}
