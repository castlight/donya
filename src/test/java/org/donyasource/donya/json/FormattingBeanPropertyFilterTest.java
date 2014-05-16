package org.donyasource.donya.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.annotate.JsonFilter;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.donyasource.donya.LocalizedResourceFormatter;
import org.donyasource.donya.annotations.LocalizedResource;
import org.donyasource.donya.annotations.LocalizedResources;
import org.donyasource.donya.annotations.ResourceFamily;
import org.donyasource.donya.annotations.ResourceKey;
import org.donyasource.donya.util.ReflectionUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


public class FormattingBeanPropertyFilterTest {
   private static final ReflectionUtil reflectionUtil = new ReflectionUtil();

   private ObjectMapper objectMapper;
   private FormattingBeanPropertyFilter underTest;
   @Mock private LocalizedResourceFormatter resourceFormatter;

   @JsonFilter(LocalizingObjectMapper.I18N_FILTER_NAME)
   private static class BaseClass {
      private @JsonProperty final String fieldOne;
      private @JsonProperty final int fieldTwo;

      public BaseClass(@JsonProperty("fieldOne")String fieldOne, @JsonProperty("fieldTwo")int fieldTwo) {
         super();
         this.fieldOne = fieldOne;
         this.fieldTwo = fieldTwo;
      }
      
      @Override
      public boolean equals(Object obj) {
         return EqualsBuilder.reflectionEquals(this, obj);
      }

      public String getFieldOne() {
         return fieldOne;
      }

      public int getFieldTwo() {
         return fieldTwo;
      }
   }

   @Before
   public void setup() {
      initMocks(this);
      objectMapper = new ObjectMapper();
      underTest = new FormattingBeanPropertyFilter(resourceFormatter);
   }
   
   @Test
   public void correctlySerializesObjectWithNoResources() throws Exception {
      BaseClass testObj = new BaseClass("foo", 27);
      BaseClass result = serializeAndBackAgain(testObj, BaseClass.class);
      
      assertEquals(testObj, result);
   }
   
   private static class BaseResourceClass extends BaseClass {
      @LocalizedResource
      @JsonProperty
      private final String myResource;
      
      public BaseResourceClass() {
         this(null, "foo", 27);
      }
      
      public BaseResourceClass(@JsonProperty("myResource")String myResource,
                               @JsonProperty("fieldOne")String fieldOne, 
                               @JsonProperty("fieldTwo")int fieldTwo) {
         super(fieldOne, fieldTwo);
         this.myResource = myResource;
      }
      
      @ResourceKey
      private static final String KEY = "myKey";
      
      @ResourceFamily
      private static final String RESOURCE_FAMILY = "resourceFamily";
   }
   @Test
   public void correctlySetsResource() throws Exception {
      BaseResourceClass testObj = new BaseResourceClass();
      setupResource("myResource", testObj, "Hello!");

      BaseResourceClass result = serializeAndBackAgain(testObj, BaseResourceClass.class);
      
      assertEquals("foo", result.getFieldOne());
      assertEquals(27, result.getFieldTwo());
      assertEquals("Hello!", result.myResource);
   }
   
   @JsonFilter(LocalizingObjectMapper.I18N_FILTER_NAME)
   private static class MultipleResources {
      @LocalizedResource
      @JsonProperty
      private final String myResource;
      
      @LocalizedResource
      @JsonProperty
      private final String otherResource;
      
      public MultipleResources(@JsonProperty("myResource")String myResource, 
                               @JsonProperty("otherResource")String otherResource) {
         super();
         this.myResource = myResource;
         this.otherResource = otherResource;
      }

      public boolean equals(Object other) {
         return EqualsBuilder.reflectionEquals(this, other);
      }
   }
   @Test
   public void serializesWithNonStaticKeyAndBundleFamily() throws Exception {
      MultipleResources testObj = new MultipleResources(null, null);
      setupResource("myResource", testObj, "Howdy!");
      setupResource("otherResource", testObj, "Hey There!");
      
      MultipleResources result = serializeAndBackAgain(testObj, MultipleResources.class);
      
      assertEquals("Howdy!", result.myResource);
      assertEquals("Hey There!", result.otherResource);
   }
   
   @JsonFilter(LocalizingObjectMapper.I18N_FILTER_NAME)
   private static class ListClass {
      @LocalizedResources
      @JsonProperty
      private final List<String> myList;
      
      public ListClass(@JsonProperty("myList")List<String> myList) {
         this.myList = myList;
      }
   }
   @Test
   public void worksWithListOfStrings() {
      ListClass testObj = new ListClass(null);
      
      List<String> expected = Arrays.asList(new String[]{"a", "b"});
      
      Field resourceField = getField("myList", testObj);
      
      when(resourceFormatter.formatResources(resourceField, testObj)).thenReturn(expected);
      
      ListClass actual = serializeAndBackAgain(testObj, ListClass.class);

      assertTrue("Expected " + expected + ", got " + actual.myList, expected.containsAll(actual.myList));
      assertTrue("Expected " + expected + ", got " + actual.myList, actual.myList.containsAll(expected));
   }
   
   @JsonFilter(LocalizingObjectMapper.I18N_FILTER_NAME)
   private static class MethodProperty {
      @JsonProperty("foo")
      public String getFoo() {
         return "blah";
      }
      
      public boolean equals(Object other) {
         return EqualsBuilder.reflectionEquals(this, other);
      }
   }
   public void serializesMethodProperty() throws Exception {
      MethodProperty testObj = new MethodProperty();
      
      MethodProperty result = serializeAndBackAgain(testObj, MethodProperty.class);
      
      assertEquals(testObj, result);
      
   }
   
   
   private void setupResource(String resourceName, Object object, String message) {
     Field field = getField(resourceName, object);
     
     when(resourceFormatter.formatResourceAsString(field, object)).thenReturn(message);
   }

   private Field getField(String resourceName, Object object) {
      Field field;
        try {
           field = reflectionUtil.getField(object, resourceName);
        } catch (NoSuchFieldException e) {
           throw new RuntimeException(e);
        }
      return field;
   }

   private <T> T serializeAndBackAgain(T testObj, Class<T> clazz) {
      try {
         FilterProvider provider = new SimpleFilterProvider().setDefaultFilter(underTest);
         ObjectWriter writer = objectMapper.writer(provider);
         String json = writer.writeValueAsString(testObj);
         return objectMapper.readValue(json, clazz);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }

   }
   
   

}
