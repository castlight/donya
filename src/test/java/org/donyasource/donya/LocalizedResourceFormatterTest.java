package org.donyasource.donya;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.donyasource.donya.annotations.LocalizedResource;
import org.donyasource.donya.annotations.LocalizedResources;
import org.donyasource.donya.annotations.ResourceArgument;
import org.donyasource.donya.annotations.ResourceArgumentList;
import org.donyasource.donya.annotations.ResourceFamily;
import org.donyasource.donya.annotations.ResourceKey;
import org.donyasource.donya.annotations.ResourceValueProvider;
import org.donyasource.donya.util.ReflectionUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


public class LocalizedResourceFormatterTest {
   private static final ReflectionUtil reflectionUtil = new ReflectionUtil();

   @Mock private LocalizedResourceResolver resourceResolver;
   @Mock private Logger logger;

   private LocalizedResourceFormatter underTest;
   
   private static class BaseClass {
      @LocalizedResource
      protected String myResource;
      
      @ResourceFamily
      protected static final String RESOURCE_FAMILY="com.castlight.bundle";
   }
   

   @Before
   public void setup() {
      initMocks(this);
      
      underTest = new LocalizedResourceFormatter(resourceResolver, logger, new ResourceValueProvider(), false);
   }
   
   private void setupResource(Object key, String resource) {
      setupResource(BaseClass.RESOURCE_FAMILY, key, resource);
   }

   private void setupResource(String resourceFamily, Object key, String resource) {
      when(resourceResolver.resolveAsString(resourceFamily, key.toString())).thenReturn(resource);
   }

   private static class ArgumentList extends BaseClass {
      @ResourceKey
      private final String myKey = "messageKey";

      @ResourceArgument(index=0)
      private final String myName = "David";
      
      @ResourceArgument(index=1)
      private final String myCity = "Berkeley";
   }
   @Test
   public void correctlyFormatsResourceWithArgumentsAsFields() {
      ArgumentList testClass = new ArgumentList();
      String resourceString = "This is my message, my name is {0} and I live in {1}";
      setupResource("messageKey", resourceString);

      String formattedString = formatResource(testClass);
      String expectedString = MessageFormat.format(resourceString, testClass.myName, testClass.myCity);

      assertEquals(expectedString, formattedString);
   }
   
   private static class NonStringKey extends BaseClass {
      @ResourceKey
      public static final int key = 27;
   }
   @Test
   public void worksWithNonStringKey() {
      NonStringKey testObj = new NonStringKey();

      setupResource(27, "hello");
      
      assertEquals("hello", formatResource(testObj));
   }
   
   private static class PrimitiveType extends BaseClass {
      @ResourceKey
      public static final int key = 44;
   }
   @Test
   public void worksWithPrimitiveType() {
      PrimitiveType testObj = new PrimitiveType();
      
      setupResource(44, "hello");
      
      assertEquals("hello", formatResource(testObj));
   }

   private String formatResource(Object testClass) {
      return formatResource(testClass, "myResource");
   }
   
   private static class ListClass {
      @ResourceFamily
      private static final String RESOURCE_FAMILY = "myFamily";

      @LocalizedResources
      private List<String> myThings;
      
      @ResourceKey
      private static List<Integer> myKeys = Arrays.asList(new Integer[] { 1, 3, 2 });
      
   }
   @Test
   public void supportsListOfResources() {
      setupResource(ListClass.RESOURCE_FAMILY, 1, "Groucho");
      setupResource(ListClass.RESOURCE_FAMILY, 2, "Chico");
      setupResource(ListClass.RESOURCE_FAMILY, 3, "Harpo");
      
      ListClass testObject = new ListClass();
      
      List<String> expected = Arrays.asList(new String[] { "Groucho", "Harpo", "Chico" });
      
      Field resourceField = getField(testObject, "myThings");
        List<String> actual = underTest.formatResources(resourceField, testObject);
      
      assertTrue("Expected " + expected + ", got " + actual, expected.containsAll(actual));
      assertTrue("Expected " + expected + ", got " + actual, actual.containsAll(expected));
   }
   
   private static class NullDefaultValue extends BaseClass {
       @LocalizedResource private String nullValue = null;
   }
   @Test
   public void correctlyHandlesNullDefaultValue() {
      NullDefaultValue testObj = new NullDefaultValue();
      assertNull(formatResource(testObj));
      
   }

   private String formatResource(Object testObject, String resourceFieldName) {
      Field resourceField = getField(testObject, resourceFieldName);
      return underTest.formatResourceAsString(resourceField, testObject);
   }

   private Field getField(Object testClass, String resourceName) {
      Field field;
      try {
         field = reflectionUtil.getField(testClass, resourceName);
      } catch (NoSuchFieldException e) {
         throw new RuntimeException(e);
      }
      return field;
   }
   
   private static class AllArguments extends BaseClass {
      @ResourceKey
      private static final String messageId="245";

      @ResourceArgumentList
      private final Object[] messageArguments = new Object[] {"David", "Berkeley"};
   }
   @Test
   public void correctlyFormatsResourceWithAllArgumentsField() {
      AllArguments testClass = new AllArguments();
      String resource = "This is my message, my name is {0} and I live in {1}";
      setupResource("245", resource);
      String formattedString = formatResource(testClass);
      String expectedString = MessageFormat.format(resource, new Object[] {"David", "Berkeley"});

      assertEquals(expectedString, formattedString);
   }
   
   private static final class WithKey extends BaseClass { 
      @ResourceKey
      private static final String key = "myKey";
   }
   @Test
   public void worksWithStaticKey() {
      WithKey testClass = new WithKey();
      setupResource("myKey", "myResource");
      String result = formatResource(testClass);
      
      assertEquals("myResource", result);
   }
   
   private static final class WithKeyField extends BaseClass{
      @ResourceKey
      private final String myKey = "theKey";
   }
   @Test
   public void worksWithInstanceKey() {
      WithKeyField testClass = new WithKeyField();
      setupResource("theKey", "myResource");

      String result = formatResource(testClass);
      
      assertEquals("myResource", result);
      
   }
   
   private static final class EnumClass {
      enum Enumerations { A, B };
      
      @LocalizedResource
      private String myResource;

      @ResourceFamily
      private static final Enumerations resourceFamily = Enumerations.A;

      @ResourceKey
      private static final Enumerations resourceKey = Enumerations.B;
      
   }
   @Test
   public void worksWithEnum() {
      EnumClass testClass = new EnumClass();
      setupResource(EnumClass.Enumerations.A.toString(), EnumClass.Enumerations.B.toString(), "hello");
      
      assertEquals("hello", formatResource(testClass));
   }
   
   private static final class MultipleResources {
      @LocalizedResource
      private String resourceOne;
      
      @LocalizedResource 
      private String resourceTwo;
      
      @ResourceKey("resourceOne") 
      public static final String KEY_ONE = "keyOne";
      
      @ResourceKey("resourceTwo")
      public static final String KEY_TWO = "keyTwo";
      
      @ResourceFamily("resourceOne")
      public static final String FAMILY_ONE = "familyOne";
      
      @ResourceFamily("resourceTwo")
      public static final String FAMILY_TWO = "familyTwo";
      
      @ResourceArgumentList("resourceOne")
      public static final Object[] argumentsOne = new String[] { "Romeo" };
      
      @ResourceArgument(resourceName="resourceTwo", index=0)
      public static final String argument = "Juliet";
   }
   @Test
   public void worksWithMultipleResources() {
      setupResource(MultipleResources.FAMILY_ONE, MultipleResources.KEY_ONE, "hello {0}");
      setupResource(MultipleResources.FAMILY_TWO, MultipleResources.KEY_TWO, "goodbye {0}");
      
      MultipleResources testObj = new MultipleResources();
      
      assertEquals("hello Romeo", formatResource(testObj, "resourceOne"));
      assertEquals("goodbye Juliet", formatResource(testObj, "resourceTwo"));
   }
   
   private static class SameKey {
      @LocalizedResource
      private String resourceOne;
      
      @LocalizedResource
      private String resourceTwo;
      
      @ResourceFamily
      private static final String RESOURCE_FAMILY = "family";
      
      @ResourceKey("resourceOne")
      private static final String KEY_ONE = "keyOne";
      
      @ResourceKey("resourceTwo")
      private static final String KEY_TWO = "keyTwo";
   }
   @Test
   public void multipleResourcesCanUseSameFamily() {
      SameKey testObj = new SameKey();
      setupResource(SameKey.RESOURCE_FAMILY, "keyOne", "Hello, Ernie");
      setupResource(SameKey.RESOURCE_FAMILY, "keyTwo", "Hello, Bert");

      assertEquals("Hello, Ernie", formatResource(testObj, "resourceOne"));
      assertEquals("Hello, Bert", formatResource(testObj, "resourceTwo"));
   }
      
   private static final class NullKeyField extends BaseClass {
      @ResourceKey
      private final String myKey = null;
   }
   @Test
   public void errorLoggedIfKeyFieldValueIsNull() {
      underTest = new LocalizedResourceFormatter(resourceResolver, logger, new ResourceValueProvider(), false);

      NullKeyField testClass = new NullKeyField();
      testClass.myResource = "foo";
      setupResource("myKey", "myResource");
      assertEquals("foo", formatResource(testClass));
      verify(logger).log(any(Level.class), anyString(), any(Exception.class));
   }
   
   public void errorLoggedIfNoKeyIsProvided() {
      BaseClass missingField = new BaseClass();
      setupResource("theKey", "myResource");
      formatResource(missingField);
      verify(logger).severe(anyString());
   }
   
   private static class NullKeyInList {
      @LocalizedResources
      private List<String> myResources;
      
      @ResourceKey
      private List<String> keys = Arrays.asList(new String[] {"a", null, "b"});
   }
   
   private static class NoResourceFamily {
      @LocalizedResource private String resource="blah";
      @ResourceKey private static final String myKey = "foo";

   }
   public void errorLoggedIfNoResourceFamily() {
      NoResourceFamily testClass = new NoResourceFamily();
      setupResource(NoResourceFamily.myKey, "myResource");
      
      assertEquals("blah", formatResource(testClass));
      verify(logger).severe(anyString());
   }
   
   private static class FailureClass {
      @LocalizedResource
      private String myResource="CaptainAmerica";
      
      @ResourceFamily
      private static final String resourceFamily = BaseClass.RESOURCE_FAMILY;

      @ResourceKey
      private static final String myKey = "foo";
   }
   @Test
   public void usesExistingValueAndErrorLoggedIfResourceNotFound() {
      FailureClass testClass = new FailureClass();
      
      when(resourceResolver.resolveAsString(BaseClass.RESOURCE_FAMILY, "foo")).thenThrow(new MissingResourceException("foo", "foo", "foo"));
      
      assertEquals("CaptainAmerica", formatResource(testClass));
      verify(logger).log(eq(Level.SEVERE), anyString(), any(MissingResourceException.class));
   }
}
