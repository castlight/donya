package org.donyasource.donya;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.google.common.collect.ImmutableList;


public class LocalizedResourceResolverTest {
   @Mock ResourceBundleLoader bundleLoaderOne;
   @Mock ResourceBundleLoader bundleLoaderTwo;
   private LocalizedResourceResolver underTest;
   
   private static final Locale usEnglishLocale = new Locale("en", "US");
   private static final Locale usSpanishLocale = new Locale("es", "US");

   private static final String FAMILY_ONE = "familyOne";
   private static final String FAMILY_TWO = "familyTwo";

   private static ResourceBundleLoader familyOneLoader = new ResourceBundleLoader() {
      public final ResourceBundle englishBundle = new ListResourceBundle() {
         @Override
         protected Object[][] getContents() {
            return new Object[][] { 
                     {"a" , "Howdy"},
                     {"b", "Neighbor"}
            };
         }
      };

      public final ResourceBundle spanishBundle = new ListResourceBundle() {
         @Override
         protected Object[][] getContents() {
            return new Object[][] { 
                     {"a" , "Ola"},
                     {"b", "Amigo"}
            };
         }
      };

      @Override
      public ResourceBundle loadBundle(String resourceFamily, Locale locale) {
         if (locale.equals(usEnglishLocale)) {
            return englishBundle;
         } else if (locale.equals(usSpanishLocale)){
            return spanishBundle;
         } else {
            return null;
         }
      }

      @Override
      public boolean supportsResourceFamily(String resourceFamily) {
         return FAMILY_ONE.equals(resourceFamily);
      }
   };

   private static ResourceBundleLoader familyTwoLoader = new ResourceBundleLoader() {
      private final ResourceBundle englishBundle = new ListResourceBundle() {
         @Override
         protected Object[][] getContents() {
            return new Object[][] { 
                     {"gday" , "Wotcher"}
            };
         }
      };

      @Override
      public ResourceBundle loadBundle(String resourceFamily, Locale locale) {
         if (locale.equals(usEnglishLocale)) {
            return englishBundle;
         }
         return null;
      }

      @Override
      public boolean supportsResourceFamily(String resourceFamily) {
         return FAMILY_TWO.equals(resourceFamily);
      }
   };

   @Before
   public void setup() {
      initMocks(this);

      loaders = ImmutableList.of(familyOneLoader, familyTwoLoader);
   }


   @Test
   public void getsCorrectUsEnglishStringForFamilyOne() {
      createUnderTest(usEnglishLocale);
      assertEquals("Howdy", getResourceString(FAMILY_ONE, "a"));
      assertEquals("Neighbor", getResourceString(FAMILY_ONE, "b"));
   }

   @Test
   public void getsCorrectUsSpanishStringForFamilyOne() {
      createUnderTest(usSpanishLocale);
      assertEquals("Ola", getResourceString(FAMILY_ONE, "a"));
      assertEquals("Amigo", getResourceString(FAMILY_ONE, "b"));
   }
   
   @Test
   public void getCorrectStringForFamilyTwo() {
      createUnderTest(usEnglishLocale);
      assertEquals("Wotcher", getResourceString(FAMILY_TWO, "gday"));
   }

   @Test(expected = MissingResourceException.class)
   public void missingResourceForWrongFamily() {
      createUnderTest(usEnglishLocale);
      getResourceString(FAMILY_TWO, "a");
   }
   
   @Test(expected = MissingResourceException.class)
   public void returnsNullForInvalidKey() {
      createUnderTest(usEnglishLocale);
      assertNull(getResourceString(FAMILY_ONE, "invalidKey"));
   }
   
   private static final ResourceBundle englishBundle = new ListResourceBundle() {
      @Override
      protected Object[][] getContents() {
         return new Object[][] { 
             {"a" , "Howdy"}
         };
      }
   };
   @Test
   public void cachesResourceBundle() {
      ResourceBundleLoader loader = mock(ResourceBundleLoader.class);
      when(loader.loadBundle(FAMILY_ONE, usEnglishLocale)).thenReturn(englishBundle);
      when(loader.supportsResourceFamily(FAMILY_ONE)).thenReturn(true);

      underTest = new LocalizedResourceResolver(usEnglishLocale, ImmutableList.of(loader));

      assertEquals("Howdy", getResourceString(FAMILY_ONE, "a"));
      
      when(loader.loadBundle(FAMILY_ONE, usEnglishLocale)).thenReturn(null);

      assertEquals("Howdy", getResourceString(FAMILY_ONE, "a"));
   }
   
   @Test
   public void doesNotKeepPollingWhenBundleLoaderNotFound() {
      ResourceBundleLoader loader = mock(ResourceBundleLoader.class);
      when(loader.supportsResourceFamily(FAMILY_ONE)).thenReturn(false);

      underTest = new LocalizedResourceResolver(usEnglishLocale, ImmutableList.of(loader));

      try {
         getResourceString(FAMILY_ONE, "a");
      } catch (MissingResourceException e) {
         // expected
      }
      
      when(loader.loadBundle(FAMILY_ONE, usEnglishLocale)).thenReturn(null);
      when(loader.supportsResourceFamily(FAMILY_ONE)).thenReturn(true);

      // Should still get missing resource exception - we don't keep hitting the loaders
      // once we determine a given family doesn't have a resource bundle
      try {
         getResourceString(FAMILY_ONE, "a");
      } catch (MissingResourceException e) {
         // expected
      }
   }
   
   private String getResourceString(String bundleFamily, String resourceKey) {
      return underTest.resolveAsString(bundleFamily, resourceKey);
   }
   
   private void createUnderTest(Locale context) {
      underTest = new LocalizedResourceResolver(context, loaders);
   }

   private List<ResourceBundleLoader> loaders;
   @Test
   public void getsBundleFromFirstLoader() {
      
   }
   
   @Test
   public void cachesBundle() {
      
   }

}
