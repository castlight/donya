package org.donyasource.donya.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * The key for a localized resource.  The key identifies a unique resource within a resource bundle
 */
public @interface ResourceKey {
   /**
    * The name of the localized resource.  This is the name of a field that has
    * an @LocalizedResource annotation.
    * 
    * This does not need to be specified if there is only one localized resource in this class.
    */
   public String value() default "";
}
