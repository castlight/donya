package org.donyasource.donya.examples.simple;

import java.io.IOException;
import java.util.Locale;

import org.donyasource.donya.ResourceBundleLoader;
import org.donyasource.donya.json.LocalizingJsonMapper;

import com.google.common.collect.ImmutableList;

public class SimpleExample {

   public static void main(String[] args) {
      if (args.length == 0) {
         System.out.println("Please provide a locale");
         System.exit(1);
      }

      String localeString = args[0];
      Locale locale = new Locale(localeString);

      UserMessage message = new UserMessage(MessageCode.HELLO_WORLD);
       
      ResourceBundleLoader bundleLoader = new PropertyBundleLoader(UserMessage.RESOURCE_FAMILY);
      LocalizingJsonMapper mapper = new LocalizingJsonMapper(locale, ImmutableList.of(bundleLoader));
      try {
         String json = mapper.writeValueAsString(message);
         System.out.println(json);
      } catch (IOException e) {
         e.printStackTrace();
         System.exit(1);
      }
      
      System.exit(0);
   }
}
