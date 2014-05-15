package org.donyasource.donya.json;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.donyasource.donya.LocalizedResourceFormatter;
import org.donyasource.donya.ResourceBundleLoader;

public class LocalizingJsonMapper extends ObjectMapper {
   public static final String I18N_FILTER_NAME = "i18n_filter";

   ObjectWriter writer;
   public LocalizingJsonMapper(Locale locale, List<ResourceBundleLoader> bundleLoaders) {
      super();
      FormattingBeanPropertyFilter filter = new FormattingBeanPropertyFilter(
               new LocalizedResourceFormatter(locale, bundleLoaders));
      FilterProvider provider = new SimpleFilterProvider().addFilter(I18N_FILTER_NAME, filter);
      writer = super.writer(provider);
   }

   
   @Override
   public String writeValueAsString(Object value) throws IOException, JsonGenerationException, JsonMappingException {
      return writer.writeValueAsString(value);
   }
}