package org.donyasource.donya.examples.simple;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonFilter;
import org.donyasource.donya.annotations.LocalizedResource;
import org.donyasource.donya.annotations.ResourceFamily;
import org.donyasource.donya.annotations.ResourceKey;
import org.donyasource.donya.json.LocalizingObjectMapper;

@JsonFilter(LocalizingObjectMapper.I18N_FILTER_NAME)
public class UserMessage {
   @ResourceFamily 
   public static final String RESOURCE_FAMILY = "org.donyasource.donya.examples.simple.messages";

   @LocalizedResource
   @JsonProperty
   // The message will be formatted and provided as a field in the JSON object during serialization
   private final String message = null;
   
   @ResourceKey
   @JsonProperty
   private final MessageCode messageCode;
   
   public UserMessage(MessageCode messageCode) {
      this.messageCode = messageCode;
   }
}
