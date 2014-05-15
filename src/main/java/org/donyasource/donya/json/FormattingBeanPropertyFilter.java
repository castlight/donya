package org.donyasource.donya.json;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.BeanPropertyFilter;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;
import org.donyasource.donya.LocalizedResourceFormatter;
import org.donyasource.donya.annotations.LocalizedResource;
import org.donyasource.donya.annotations.LocalizedResources;

/**
 * This class is called by the Jackson serializer for each field in an object.
 * If the field is marked with an @LocalizedResource annotation, we get the
 * resource and format it and emit the resulting value as the value of the field
 * in the JSON.
 */
public class FormattingBeanPropertyFilter implements BeanPropertyFilter {
   private final LocalizedResourceFormatter resourceFormatter;

   public FormattingBeanPropertyFilter(LocalizedResourceFormatter resourceFormatter) {
      this.resourceFormatter = resourceFormatter;
   }

   @Override
   public void serializeAsField(Object object, JsonGenerator jsonGenerator, SerializerProvider prov, BeanPropertyWriter writer)
            throws Exception {
      Member member = writer.getMember().getMember();

      if (!(member instanceof Field)) {
         writer.serializeAsField(object, jsonGenerator, prov);
         return;
      }

      Field field = (Field) member;

      if (field.isAnnotationPresent(LocalizedResource.class)) {
         String formattedResource = resourceFormatter.formatResourceAsString(field, object);
         jsonGenerator.writeStringField(field.getName(), formattedResource);
      } else if (field.isAnnotationPresent(LocalizedResources.class)) {
         List<String> formattedResource = resourceFormatter.formatResources(field, object);
         jsonGenerator.writeObjectField(field.getName(), formattedResource);
      } else {
         writer.serializeAsField(object, jsonGenerator, prov);
      }
   }
}
